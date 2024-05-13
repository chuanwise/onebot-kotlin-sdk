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

package cn.chuanwise.onebot.v11.io.connection

import cn.chuanwise.onebot.io.connection.AbstractConnection
import cn.chuanwise.onebot.io.connection.ReverseWebSocketConnectionConfiguration
import cn.chuanwise.onebot.io.data.Value
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCallPipeline
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.origin
import io.ktor.server.routing.routing
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.Frame
import io.ktor.websocket.WebSocketSession
import io.ktor.websocket.readText
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class ReverseWebSocketConnection(
    override val configuration: ReverseWebSocketConnectionConfiguration
) : AbstractConnection() {

    private val logger = KotlinLogging.logger { }
    private val server = embeddedServer(
        Netty,
        port = configuration.port,
        host = configuration.host,
    ) {
        module()
    }.start()

    private var session: WebSocketSession? = null
    private val sessionLock = ReentrantReadWriteLock()
    private val sessionWriteCondition = sessionLock.writeLock().newCondition()

    init {
        runBlocking {
            await()
        }
    }

    private fun Application.module() {
        install(WebSockets)
        intercept(ApplicationCallPipeline.Monitoring) {
            val address = call.request.origin.remoteAddress
            if (authorize()) {
                logger.info { "Accepted connection from $address." }
            } else {
                logger.warn { "Rejected connection from $address." }
            }
        }
        routing {
            webSocket(configuration.path) {
                val address = call.request.origin.remoteAddress

                sessionLock.read {
                    if (session !== null) {
                        logger.warn { "Connection from $address is rejected because the connection is already established." }
                        return@webSocket
                    }
                    sessionLock.write {
                        session = this
                        sessionWriteCondition.signalAll()
                    }
                }
                try {
                    for (frame in incoming) {
                        if (frame !is Frame.Text) {
                            logger.warn { "Received non-text frame: $frame" }
                            continue
                        }

                        val text = frame.readText()
                        logger.debug { "Received text: $text" }

                        val value = configuration.serialization.deserialize(text)
                        notifyListeners(value)
                    }
                } catch (exception: Exception) {
                    logger.error(exception) { "Error occurred in connection from $address." }
                } finally {
                    sessionLock.write {
                        session = null
                        sessionWriteCondition.signalAll()
                    }
                }
            }
        }
    }

    override val established: Boolean
        get() = sessionLock.read { session } !== null

    override suspend fun sendValue(value: Value): Value? {
        val currentSession = sessionLock.read { session }
        if (currentSession === null) {
            throw IllegalStateException("The connection is not established!")
        }
        currentSession.send(Frame.Text(configuration.serialization.serialize(value)))
        return null
    }

    override suspend fun await(): ReverseWebSocketConnection {
        while (sessionLock.read { session } === null) {
            withContext(Dispatchers.IO) {
                sessionLock.write {
                    sessionWriteCondition.await()
                }
            }
        }
        return this
    }

    override fun close() {
        server.stop()
    }
}