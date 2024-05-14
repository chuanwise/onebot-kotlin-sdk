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

import cn.chuanwise.onebot.io.connection.WebSocketConnection
import cn.chuanwise.onebot.io.connection.WebSocketConnectionAPI
import cn.chuanwise.onebot.io.data.deserializeTo
import cn.chuanwise.onebot.io.data.objectMapper
import cn.chuanwise.onebot.v11.io.OneBot11Action
import cn.chuanwise.onebot.v11.io.data.action.ActionRequestPacket
import com.fasterxml.jackson.databind.JsonNode

class WebSocketConnectionOneBot11API(
    override val connection: WebSocketConnection
) : WebSocketConnectionAPI(connection), OneBot11API {

    override suspend fun <P, R> request(action: OneBot11Action<P, R>, params: P): R {
        val packet = ActionRequestPacket(action.name, params)
        val node = objectMapper.valueToTree<JsonNode>(packet)
        return sendAndWait(node).deserializeTo(action.respTypeReference)
    }
}