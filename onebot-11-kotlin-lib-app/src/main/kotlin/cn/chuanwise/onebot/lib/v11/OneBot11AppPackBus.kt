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

import cn.chuanwise.onebot.lib.AbstractPackBus
import cn.chuanwise.onebot.lib.deserializeTo
import cn.chuanwise.onebot.lib.v11.data.action.HandleQuickOperationData
import cn.chuanwise.onebot.lib.v11.data.event.EventData
import com.fasterxml.jackson.core.JacksonException
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import io.github.oshai.kotlinlogging.KLogger

class OneBot11AppPackBus(
    private val objectMapper: ObjectMapper,
    private val logger: KLogger,
) : AbstractPackBus() {

    // The reason why it's late init var is that
    // the connection is created after the packBus is created.
    // it MUST be set in the init block.
    internal lateinit var connection: OneBot11AppConnection

    override suspend fun push(node: JsonNode) {
        val eventData = try {
            node.deserializeTo<EventData>(objectMapper)
        } catch (exception: JacksonException) {
            logger.error { "Failed to deserialize event data: `$node`, did `OneBot11LibModule` installed?" }
            throw exception
        }

        handle(eventData)?.takeIf { it != Unit }?.let {
            connection.send(
                HIDDEN_HANDLE_QUICK_OPERATION, HandleQuickOperationData(
                    context = eventData,
                    operation = it,
                )
            )
        }
    }
}