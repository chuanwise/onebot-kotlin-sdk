/*
 * Copyright 2024 Chuanwise and contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.chuanwise.onebot.lib

import io.github.oshai.kotlinlogging.KLogger
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCallPipeline
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.origin
import io.ktor.server.response.respond
import io.ktor.server.routing.routing
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.CloseReason
import io.ktor.websocket.WebSocketSession
import io.ktor.websocket.close
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write
import kotlin.coroutines.CoroutineContext
import kotlin.time.Duration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

interface ReverseWebSocketConnectionConfiguration {
    val host: String
    val port: Int
    val path: String
    val accessToken: String?

    // require final implementations checking!
    val heartbeatInterval: Duration?
}

abstract class ReverseWebSocketConnection(
    receivingLoop: WebSocketReceivingLoop,
    logger: KLogger,
    configuration: ReverseWebSocketConnectionConfiguration,
    packBus: PackBus
) : WebSocketLikeConnection {

    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    override val coroutineContext: CoroutineContext = coroutineScope.coroutineContext

    private val lock = ReentrantReadWriteLock()
    private val condition = lock.writeLock().newCondition()

    enum class State {
        WAITING,
        CONNECTED,
        CLOSED,
    }

    private var stateWithoutLock: State = State.WAITING
    protected val state: State
        get() = lock.read { stateWithoutLock }

    private var sessionWithoutLock: WebSocketSession? = null
    protected val session: WebSocketSession?
        get() = lock.read { sessionWithoutLock }

    override val isConnected: Boolean
        get() = state == State.CONNECTED

    private val server = embeddedServer(
        factory = Netty,
        host = configuration.host,
        port = configuration.port,
    ) {
        module(receivingLoop, logger, configuration, packBus)
    }.start()

    private fun Application.module(
        receivingLoop: WebSocketReceivingLoop,
        logger: KLogger,
        configuration: ReverseWebSocketConnectionConfiguration,
        packBus: PackBus
    ) {
        install(WebSockets)

        // authorization
        intercept(ApplicationCallPipeline.Plugins) {
            val header = call.request.headers[AUTHORIZATION]
            val query = call.request.queryParameters[ACCESS_TOKEN]
            val authReceipt = auth(configuration.accessToken, logger, header, query, call.request.origin.remoteAddress)
            when (authReceipt) {
                AuthReceipt.SUCCESS -> lock.read {
                    when (stateWithoutLock) {
                        State.WAITING -> when (stateWithoutLock) {
                            State.WAITING -> return@intercept
                            State.CLOSED -> {
                                call.respond(HttpStatusCode.ResetContent, "Connection closed.")
                                return@intercept
                            }

                            State.CONNECTED -> {
                                call.respond(HttpStatusCode.ResetContent, "Connection already established.")
                                return@intercept
                            }
                        }

                        State.CLOSED -> {
                            call.respond(HttpStatusCode.ResetContent, "Connection closed.")
                            return@intercept
                        }

                        else -> throw IllegalStateException("Unexpected state: $stateWithoutLock")
                    }
                }

                AuthReceipt.FORMAT_ERROR, AuthReceipt.REQUIRED, AuthReceipt.TOKEN_ERROR -> {
                    val message = when (authReceipt) {
                        AuthReceipt.FORMAT_ERROR -> "Access token format error."
                        AuthReceipt.REQUIRED -> "Access token required."
                        AuthReceipt.TOKEN_ERROR -> "Access token error."
                        else -> throw IllegalStateException("Unexpected auth receipt: $authReceipt")
                    }
                    call.respond(HttpStatusCode.Unauthorized, message)
                    return@intercept
                }
            }
        }

        routing {
            webSocket(configuration.path) {
                lock.read {
                    when (stateWithoutLock) {
                        State.WAITING -> lock.write {
                            sessionWithoutLock = this
                            stateWithoutLock = State.CONNECTED
                            condition.signalAll()
                        }

                        State.CLOSED -> {
                            close(CloseReason(CloseReason.Codes.NORMAL, "Connection closed."))
                            return@webSocket
                        }

                        State.CONNECTED -> {
                            close(CloseReason(CloseReason.Codes.NORMAL, "Connection already established."))
                            return@webSocket
                        }
                    }
                }

                try {
                    receivingLoop(this, packBus)
                } catch (throwable: Throwable) {
                    logger.error(throwable) { "Exception occurred in session" }
                } finally {
                    lock.read {
                        when (stateWithoutLock) {
                            // if state is CONNECTING, it's because of error.
                            State.CONNECTED -> lock.write {
                                sessionWithoutLock = null
                                stateWithoutLock = State.WAITING
                                condition.signalAll()
                            }

                            State.CLOSED -> lock.write {
                                sessionWithoutLock = null
                                stateWithoutLock = State.CLOSED
                                condition.signalAll()
                            }

                            else -> throw IllegalStateException("Unexpected state: $stateWithoutLock")
                        }
                    }
                }
            }
        }
    }

    override fun await(): ReverseWebSocketConnection {
        lock.write {
            condition.await()
        }
        return this
    }

    override suspend fun disconnect(reason: CloseReason) {
        val currentSession = session
        if (currentSession === null) {
            throw IllegalStateException("Connection is not established.")
        }

        currentSession.close(reason)
    }

    override fun close() {
        lock.write {
            stateWithoutLock = State.CLOSED
            sessionWithoutLock = null
            server.stop()
        }
        coroutineScope.cancel("Connection closed.")
    }
}