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

package cn.chuanwise.onebot.v11.io.api

import cn.chuanwise.onebot.io.connection.Connection
import cn.chuanwise.onebot.io.data.Array
import cn.chuanwise.onebot.io.data.Literal
import cn.chuanwise.onebot.io.data.Null
import cn.chuanwise.onebot.io.data.Object
import cn.chuanwise.onebot.io.data.Value
import cn.chuanwise.onebot.io.data.toNotNullPlainValue
import cn.chuanwise.onebot.io.data.toPrimitive
import cn.chuanwise.onebot.v11.io.OneBot11Action
import cn.chuanwise.onebot.v11.io.data.DATA
import cn.chuanwise.onebot.v11.io.data.ECHO
import cn.chuanwise.onebot.v11.io.data.RETCODE
import cn.chuanwise.onebot.v11.io.data.action.ActionRequestPacket
import io.github.oshai.kotlinlogging.KotlinLogging
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull

class WebSocketConnectionOneBot11API(
    val connection: Connection
) : OneBot11API {
    private val listenerUUID = connection.registerListener {
        receive(it)
    }

    private val logger = KotlinLogging.logger { }

    private val channels = ConcurrentHashMap<UUID, Channel<Value>>()

    /**
     * Receive a value.
     *
     * @param value Received value.
     */
    private suspend fun receive(value: Value) {
        require(value is Object) { "The received value must be a tree." }

        val optionalRetCode = value.getOptionalNotNull(RETCODE)
        if (optionalRetCode == null) {
            // TODO: handle events.
            logger.debug { "Received an event: $value." }
        } else {
            // handle responses.

            // 1. check ret code.
            when (val retCode = optionalRetCode.toPrimitive().toInt()) {
                1404 -> TODO()
            }

            // 2. check if channel exists.
            val uuid = UUID.fromString(value.getNotNull(ECHO).toPrimitive().toString())
            val channel = channels.remove(uuid)

            // 3. send to channel if present.
            if (channel == null) {
                logger.warn { "Received an unexpected response: $value with uuid: $uuid." }
                return
            }
            channel.send(value.getNullable(DATA))

            // 4. log
            logger.debug { "Received a response: $value with uuid: $uuid." }
        }
    }

    override suspend fun <P, R> request(action: OneBot11Action<P, R>, params: P): R {
        // 1. create channels.
        val channel = Channel<Value>()
        try {
            // 2. bind channel with a new generated UUID.
            var uuid: UUID
            do {
                uuid = UUID.randomUUID()
            } while (channels.putIfAbsent(uuid, channel) != null)

            // 3. prepare data to send.
            val data = ActionRequestPacket(action.name, params, echo = uuid.toString())

            // 4. send and wait.
            val response = withContext(Dispatchers.IO) {
                connection.sendValue(data.toNotNullPlainValue())?.let {
                    return@withContext it
                }
                connection.configuration.responseTimeout?.let {
                    withTimeoutOrNull(it) {
                        channel.receive()
                    }
                } ?: channel.receive()
            }

            // 5. decode
            @Suppress("UNCHECKED_CAST")
            return when (response) {
                is Object -> response.deserializeTo(action.respTypeReference)
                is Array -> response.deserializeTo(action.respTypeReference)
                is Literal -> response.value?.let { it as R } ?: Null as R
                else -> throw IllegalStateException(
                    "The response is not the expected type. " +
                            "Except: ${action.respTypeReference.type.typeName}, " +
                            "but got: ${response}."
                )
            }
        } finally {
            channel.close()
        }
    }

    override fun close() {
        require(connection.unregisterListener(listenerUUID)) { "Fail to detach from connection: $connection" }
    }
}