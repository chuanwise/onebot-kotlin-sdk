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

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import io.github.oshai.kotlinlogging.KLogger
import io.ktor.websocket.DefaultWebSocketSession
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import kotlinx.coroutines.channels.Channel

/**
 * WebSocket receiving loop, to handle incoming messages.
 *
 * @author Chuanwise
 */
interface WebSocketReceivingLoop : AutoCloseable {
    // Kotlin-friendly API.
    suspend operator fun invoke(session: DefaultWebSocketSession, packBus: PackBus) = receive(session, packBus)

    suspend fun receive(session: DefaultWebSocketSession, packBus: PackBus)
}

/**
 * Tools to filter response message out from incoming messages and
 * send it into corresponding channel automatically, provide functions
 * to allocate and register channels.
 *
 * @author Chuanwise
 */
class WebSocketAppReceivingLoop(
    private val objectMapper: ObjectMapper,
    private val logger: KLogger
) : WebSocketReceivingLoop {

    private val channels = ConcurrentHashMap<UUID, Channel<JsonNode>>()

    override suspend fun receive(session: DefaultWebSocketSession, packBus: PackBus) {
        for (frame in session.incoming) {
            if (frame !is Frame.Text) {
                logger.warn { "Unexpected incoming frame: $frame, except `Frame.Text`!" }
                continue
            }

            val text = frame.readText()
            val node = objectMapper.readTree(text)

            if (node !is ObjectNode) {
                logger.warn { "Unexpected receiving text: $text, whose deserialized node is $node, expect `ObjectNode`!" }
                continue
            }

            logger.debug { "Receiving text: $text" }

            val optionalEcho = node.getOptionalNotNull(ECHO)
            if (optionalEcho === null) {
                // handle events
                packBus(node)
            } else {
                // handle responses
                val uuid = UUID.fromString(optionalEcho.asText())
                val channel = channels.remove(uuid)

                if (channel === null) {
                    logger.warn {
                        "Channel of response with uuid `$uuid` not found! " +
                                "It may cause by response timeout."
                    }
                    continue
                }

                channel.send(node)
            }
        }
    }

    /**
     * Optimize: use instance pool.
     */
    fun allocateChannel() = Channel<JsonNode>()

    fun registerChannel(channel: Channel<JsonNode>): UUID {
        var uuid: UUID
        do {
            uuid = UUID.randomUUID()
        } while (channels.putIfAbsent(uuid, channel) !== null)
        return uuid
    }

    fun unregisterChannel(uuid: UUID): Boolean = channels.remove(uuid) !== null

    override fun close() {
        channels.values.forEach {
            it.close()
        }
    }
}