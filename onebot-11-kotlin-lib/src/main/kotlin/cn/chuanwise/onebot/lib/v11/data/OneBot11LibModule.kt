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

package cn.chuanwise.onebot.lib.v11.data

import cn.chuanwise.onebot.lib.v11.data.action.ResponseData
import cn.chuanwise.onebot.lib.v11.data.action.ResponseDataDeserializer
import cn.chuanwise.onebot.lib.v11.data.event.EventData
import cn.chuanwise.onebot.lib.v11.data.event.EventDataDeserializer
import cn.chuanwise.onebot.lib.v11.data.event.MessageEventData
import cn.chuanwise.onebot.lib.v11.data.event.MessageEventDataDeserializer
import cn.chuanwise.onebot.lib.v11.data.event.MetaEventData
import cn.chuanwise.onebot.lib.v11.data.event.MetaEventDataDeserializer
import cn.chuanwise.onebot.lib.v11.data.event.NoticeEventData
import cn.chuanwise.onebot.lib.v11.data.event.NoticeEventDataDeserializer
import cn.chuanwise.onebot.lib.v11.data.event.RequestEventData
import cn.chuanwise.onebot.lib.v11.data.event.RequestEventDataDeserializer
import com.fasterxml.jackson.databind.module.SimpleDeserializers
import com.fasterxml.jackson.databind.module.SimpleModule
import io.github.oshai.kotlinlogging.KotlinLogging

class OneBot11LibModule : SimpleModule() {
    private val logger = KotlinLogging.logger { }

    override fun setupModule(context: SetupContext) {
        logger.debug { "Setting up OneBot 11 module" }

        context.addDeserializers(
            SimpleDeserializers().apply {
                addDeserializer(EventData::class.java, EventDataDeserializer)

                addDeserializer(ResponseData::class.java, ResponseDataDeserializer)
                addDeserializer(MetaEventData::class.java, MetaEventDataDeserializer)
                addDeserializer(MessageEventData::class.java, MessageEventDataDeserializer)
                addDeserializer(NoticeEventData::class.java, NoticeEventDataDeserializer)
                addDeserializer(RequestEventData::class.java, RequestEventDataDeserializer)
            }
        )
    }
}