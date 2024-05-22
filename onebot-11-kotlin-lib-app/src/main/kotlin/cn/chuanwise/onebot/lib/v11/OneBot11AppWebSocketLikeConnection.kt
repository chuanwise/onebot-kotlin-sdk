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

package cn.chuanwise.onebot.lib.v11

import cn.chuanwise.onebot.lib.Action
import cn.chuanwise.onebot.lib.Expect
import cn.chuanwise.onebot.lib.WebSocketAppReceivingLoop
import cn.chuanwise.onebot.lib.deserializeTo
import cn.chuanwise.onebot.lib.v11.data.BAD_REQUEST_RET_CODE
import cn.chuanwise.onebot.lib.v11.data.SUCCESS_RET_CODE
import cn.chuanwise.onebot.lib.v11.data.UNSUPPORTED_OPERATION_RET_CODE
import cn.chuanwise.onebot.lib.v11.data.action.ActionRequestPack
import cn.chuanwise.onebot.lib.v11.data.action.ResponseData
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import io.github.oshai.kotlinlogging.KLogger
import io.ktor.websocket.Frame
import io.ktor.websocket.WebSocketSession

suspend fun <P> doCall(
    session: WebSocketSession?,
    receivingLoop: WebSocketAppReceivingLoop,
    objectMapper: ObjectMapper,
    logger: KLogger,
    expect: Expect<P, *>,
    params: P
): ResponseData<JsonNode?> {

    if (expect !is Action) {
        throw IllegalArgumentException("The expect must be an action for app connection.")
    }
    if (session === null) {
        throw IllegalStateException("Connection is not established.")
    }

    val channel = receivingLoop.allocateChannel()
    val uuid = receivingLoop.registerChannel(channel)

    try {
        val pack = ActionRequestPack(
            action = expect.name,
            params = params,
            echo = uuid.toString()
        )
        val string = objectMapper.writeValueAsString(pack)

        logger.debug { "Sending text: $string" }
        session.send(Frame.Text(string))

        val node = channel.receive()
        val resp = node.deserializeTo<ResponseData<JsonNode?>>(objectMapper)

        return when (resp.retCode) {
            SUCCESS_RET_CODE -> resp
            UNSUPPORTED_OPERATION_RET_CODE -> throw UnsupportedOperationException("Unsupported operation.")
            BAD_REQUEST_RET_CODE -> throw IllegalArgumentException("Bad request.")
            else -> throw IllegalStateException("Unknown error.")
        }
    } finally {
        receivingLoop.unregisterChannel(uuid)
    }
}

suspend fun <P> doSend(
    session: WebSocketSession?,
    objectMapper: ObjectMapper,
    logger: KLogger,
    expect: Expect<P, *>,
    params: P
) {
    if (expect !is Action) {
        throw IllegalArgumentException("The expect must be an action for app connection.")
    }
    if (session === null) {
        throw IllegalStateException("Connection is not established.")
    }

    val pack = ActionRequestPack(
        action = expect.name,
        params = params,
    )
    val string = objectMapper.writeValueAsString(pack)

    logger.debug { "Sending: $string" }
    session.send(Frame.Text(string))
}
