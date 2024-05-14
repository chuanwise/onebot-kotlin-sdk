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

package cn.chuanwise.onebot.io.connection

import cn.chuanwise.onebot.io.AuthorizationReceiptType
import cn.chuanwise.onebot.io.data.Codec
import cn.chuanwise.onebot.io.data.JSON
import cn.chuanwise.onebot.io.data.getNotNull
import cn.chuanwise.onebot.io.data.getNullable
import cn.chuanwise.onebot.io.data.getOptionalNotNull
import cn.chuanwise.onebot.io.isValidAccessTokenFromHeader
import cn.chuanwise.onebot.io.isValidAccessTokenFromQuery
import cn.chuanwise.onebot.v11.io.data.DATA
import cn.chuanwise.onebot.v11.io.data.ECHO
import cn.chuanwise.onebot.v11.io.data.RETCODE
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.TextNode
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.ApplicationCallPipeline
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.origin
import io.ktor.server.response.respondText
import io.ktor.server.routing.routing
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.webSocket
import io.ktor.util.pipeline.PipelineContext
import io.ktor.websocket.Frame
import io.ktor.websocket.WebSocketSession
import io.ktor.websocket.readText
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull

abstract class WebSocketConnection : Connection {
    private val logger = KotlinLogging.logger { }
    private val listeners = ConcurrentHashMap<UUID, suspend (JsonNode) -> Unit>()

    abstract suspend fun send(node: JsonNode)

    fun registerListener(action: suspend (JsonNode) -> Unit): UUID {
        val uuid = UUID.randomUUID()
        listeners[uuid] = action
        return uuid
    }

    fun unregisterListener(uuid: UUID): Boolean {
        return listeners.remove(uuid) != null
    }

    protected suspend fun push(value: JsonNode) {
        listeners.values.forEach {
            it.invoke(value)
        }
    }

    protected suspend fun PipelineContext<Unit, ApplicationCall>.authorize(): Boolean {
        val address = call.request.origin.remoteAddress

        // 1. authorization
        val authorizationHeader = call.request.headers[AUTHORIZATION]
        val authorizationQuery = call.request.queryParameters[ACCESS_TOKEN]

        val authorizationHeaderReceipt = isValidAccessTokenFromHeader(configuration, authorizationHeader)
        val authorizationQueryReceipt = isValidAccessTokenFromQuery(configuration, authorizationQuery)

        authorizationHeaderReceipt.warning?.let { logger.warn { it } }
        authorizationQueryReceipt.warning?.let { logger.warn { it } }

        val accepted: Boolean = when (authorizationHeaderReceipt.type) {
            AuthorizationReceiptType.VALID -> when (authorizationQueryReceipt.type) {
                AuthorizationReceiptType.VALID -> {
                    logger.warn {
                        "Multi same valid access token provided from $address, " +
                                "but `allowMultiSameAccessTokenProvided` is ${configuration.allowMultiSameAccessTokenProvided}."
                    }

                    configuration.allowMultiSameAccessTokenProvided
                }

                AuthorizationReceiptType.INVALID -> {
                    logger.warn {
                        "Multi different access token provided from $address, " +
                                "and `allowDifferentAccessTokenIfPresent` is ${configuration.allowDifferentAccessTokenIfPresent}."
                    }
                    configuration.allowDifferentAccessTokenIfPresent
                }

                AuthorizationReceiptType.FORMAT_ERROR -> {
                    logger.warn {
                        "Multi access token provided from $address, " +
                                "but the one from header is valid, and the format of other from query is error, " +
                                "and ${configuration.allowFormatErrorWhenMultiAccessTokenProvided}."
                    }
                    configuration.allowFormatErrorWhenMultiAccessTokenProvided
                }

                AuthorizationReceiptType.PROVIDE_REQUIRED -> {
                    if (configuration.accessToken === null ||
                        (configuration.ignoreConfiguredEmptyAccessToken && configuration.accessToken!!.isEmpty())
                    ) {
                        logger.info { "Authorized by the access token from header." }
                    } else {
                        logger.info { "Authorized because no access token required." }
                    }
                    true
                }
            }

            AuthorizationReceiptType.INVALID -> when (authorizationQueryReceipt.type) {
                AuthorizationReceiptType.VALID -> {
                    logger.warn {
                        "Multi different access token provided from $address, " +
                                "but `allowDifferentAccessTokenIfPresent` is ${configuration.allowDifferentAccessTokenIfPresent}."
                    }
                    configuration.allowDifferentAccessTokenIfPresent
                }

                AuthorizationReceiptType.INVALID -> {
                    logger.warn {
                        "Multi access token provided from $address, " +
                                "but both are invalid."
                    }
                    false
                }

                AuthorizationReceiptType.FORMAT_ERROR -> {
                    logger.warn {
                        "Multi access token provided from $address, " +
                                "but the one from header is invalid, and the format of other from query is error."
                    }
                    false
                }

                AuthorizationReceiptType.PROVIDE_REQUIRED -> {
                    false
                }
            }

            AuthorizationReceiptType.FORMAT_ERROR -> when (authorizationQueryReceipt.type) {
                AuthorizationReceiptType.VALID -> {
                    logger.warn {
                        "Multi different access token provided from $address, " +
                                "but `allowDifferentAccessTokenIfPresent` is ${configuration.allowDifferentAccessTokenIfPresent}."
                    }
                    configuration.allowDifferentAccessTokenIfPresent
                }

                AuthorizationReceiptType.INVALID -> {
                    logger.warn {
                        "Multi access token provided from $address, " +
                                "but the one from query is invalid, and the format of other from header is error."
                    }
                    false
                }

                AuthorizationReceiptType.FORMAT_ERROR -> {
                    logger.warn {
                        "Multi access token provided from $address, " +
                                "but the format of both are error."
                    }
                    false
                }

                AuthorizationReceiptType.PROVIDE_REQUIRED -> false
            }

            AuthorizationReceiptType.PROVIDE_REQUIRED -> when (authorizationQueryReceipt.type) {
                AuthorizationReceiptType.VALID -> {
                    if (configuration.accessToken === null ||
                        (configuration.ignoreConfiguredEmptyAccessToken && configuration.accessToken!!.isEmpty())
                    ) {
                        logger.info { "Authorized by the access token from query." }
                    } else {
                        logger.info { "Authorized because no access token required." }
                    }
                    true
                }

                AuthorizationReceiptType.INVALID -> {
                    logger.warn {
                        "Multi access token provided from $address, " +
                                "but the one from query is invalid, and the format of other from header is error."
                    }
                    false
                }

                AuthorizationReceiptType.FORMAT_ERROR -> {
                    logger.warn {
                        "Multi access token provided from $address, " +
                                "but the format of both are error."
                    }
                    false
                }

                AuthorizationReceiptType.PROVIDE_REQUIRED -> false
            }
        }
        if (!accepted) {
            call.respondText("Unauthorized", status = HttpStatusCode.Unauthorized)
            finish()
            return false
        }

        // 2. check self id.
        val selfIDHeader = call.request.headers[X_SELF_ID]
        if (selfIDHeader === null) {
            logger.warn { "Missing `$X_SELF_ID` header from $address." }
            call.respondText("Missing `$X_SELF_ID` header", status = HttpStatusCode.BadRequest)
            finish()
            return false
        }

        val selfID = selfIDHeader.toLongOrNull()
        if (selfID === null) {
            logger.warn { "Invalid `$X_SELF_ID` header from $address: $selfIDHeader." }
            call.respondText("Invalid `$X_SELF_ID` header", status = HttpStatusCode.BadRequest)
            finish()
            return false
        }
        return true
    }
}

abstract class WebSocketConnectionAPI(
    override val connection: WebSocketConnection
) : ConnectionAPI {

    private var listenerUUID: UUID? = null
    private val listenerUUIDLock = ReentrantReadWriteLock()

    private val logger = KotlinLogging.logger { }
    private val channels = ConcurrentHashMap<UUID, Channel<JsonNode>>()

    private suspend fun receive(node: JsonNode) {
        val optionalEcho = node.getOptionalNotNull(ECHO)
        if (optionalEcho == null) {
            // TODO: handle events.
        } else {
            // handle responses.

            // 1. check ret code.
            when (val retCode = node.getNotNull(RETCODE).asInt()) {
                1404 -> TODO()
            }

            // 2. check if channel exists.
            val uuid = UUID.fromString(optionalEcho.asText())
            val channel = channels.remove(uuid)

            // 3. send to channel if present.
            if (channel == null) {
                logger.warn { "Received an unexpected response: $node with uuid: $uuid." }
                return
            }
            channel.send(node.getNullable(DATA))
        }
    }

    private suspend fun ensureListenerRegistered() {
        listenerUUIDLock.read {
            if (listenerUUID !== null) {
                return
            }
        }
        listenerUUIDLock.write {
            if (listenerUUID !== null) {
                return
            }
            listenerUUID = connection.registerListener {
                receive(it)
            }
        }
    }

    override suspend fun sendAndWait(node: JsonNode): JsonNode {
        ensureListenerRegistered()
        if (node !is ObjectNode) {
            throw IllegalArgumentException("The node must be a ObjectNode for WebSocketConnectionAPI.")
        }

        // 1. create channels.
        val channel = Channel<JsonNode>()
        try {
            // 2. bind channel with a new generated UUID.
            var uuid: UUID
            do {
                uuid = UUID.randomUUID()
            } while (channels.putIfAbsent(uuid, channel) != null)

            // 3. prepare data to send.
            node.set<TextNode>(ECHO, TextNode(uuid.toString()))

            // 4. send and wait.
            return withContext(Dispatchers.IO) {
                connection.send(node)
                connection.configuration.responseTimeout?.let {
                    withTimeoutOrNull(it) {
                        channel.receive()
                    }
                } ?: channel.receive()
            }
        } finally {
            channel.close()
        }
    }

    override fun close() {
        listenerUUIDLock.read {
            listenerUUID?.let {
                require(connection.unregisterListener(it)) { "Fail to detach from connection: $connection" }
            }
        }
    }
}

class BackwardWebSocketConnectionConfiguration(
    var host: String,
    var port: Int,
    var path: String,
    override var accessToken: String?,
    override var codec: Codec = JSON,
    override var heartBeatInterval: Long = DEFAULT_HEARTBEAT_INTERVAL,
    override var responseTimeout: Long? = DEFAULT_RESPONSE_TIMEOUT,
    override var allowNonPrefixAccessTokenHeader: Boolean = DEFAULT_ALLOW_NON_PREFIX_ACCESS_TOKEN_HEADER,
    override var allowDifferentAccessTokenIfAbsent: Boolean = DEFAULT_ALLOW_DIFFERENT_ACCESS_TOKEN_IF_ABSENT,
    override var allowAccessTokenProvidedIfAbsent: Boolean = DEFAULT_ALLOW_ACCESS_TOKEN_PROVIDED_IF_ABSENT,
    override var allowMultiSameAccessTokenProvided: Boolean = DEFAULT_ALLOW_MULTI_SAME_ACCESS_TOKEN_PROVIDED,
    override var ignoreConfiguredEmptyAccessToken: Boolean = DEFAULT_IGNORE_CONFIGURED_EMPTY_ACCESS_TOKEN,
    override var ignoreProvidedEmptyAccessToken: Boolean = DEFAULT_IGNORE_PROVIDED_EMPTY_ACCESS_TOKEN,
    override var allowDifferentAccessTokenIfPresent: Boolean = DEFAULT_ALLOW_DIFFERENT_ACCESS_TOKEN_IF_PRESENT,
    override var allowFormatErrorWhenMultiAccessTokenProvided: Boolean = DEFAULT_ALLOW_FORMAT_ERROR_WHEN_MULTI_ACCESS_TOKEN_PROVIDED
) : ConnectionConfiguration

class BackwardWebSocketConnection(
    override val configuration: BackwardWebSocketConnectionConfiguration
) : WebSocketConnection() {

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

                        push(configuration.codec.decode(text))
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

    override suspend fun send(node: JsonNode) {
        val currentSession = sessionLock.read { session }
        if (currentSession === null) {
            throw IllegalStateException("The connection is not established!")
        }
        val message = configuration.codec.encode(node)
        currentSession.send(Frame.Text(message))
        logger.debug { "Sent text: $message" }
    }

    override suspend fun await(): BackwardWebSocketConnection {
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