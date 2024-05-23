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

package cn.chuanwise.onebot.lib.v11.data.event

import cn.chuanwise.onebot.lib.MESSAGE
import cn.chuanwise.onebot.lib.META_EVENT
import cn.chuanwise.onebot.lib.NOTICE
import cn.chuanwise.onebot.lib.POST_TYPE
import cn.chuanwise.onebot.lib.REQUEST
import cn.chuanwise.onebot.lib.deserializeTo
import cn.chuanwise.onebot.lib.getNotNull
import cn.chuanwise.onebot.lib.v11.data.OneBot11ToAppPack
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.node.ObjectNode

/**
 * Event low-level representation based on
 * [OneBot 11](https://github.com/botuniverse/onebot-11/blob/master/event).
 *
 * @author Chuanwise
 */
interface EventData : OneBot11ToAppPack {
    val time: Long
    val selfID: Long
    val postType: String
}

object EventDataDeserializer : StdDeserializer<EventData>(EventData::class.java) {
    private fun readResolve(): Any = EventDataDeserializer
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): EventData {
        val node = p.readValueAsTree<ObjectNode>()
        return when (val postType = node.getNotNull(POST_TYPE).asText()) {
            MESSAGE -> node.deserializeTo<MessageEventData>(ctxt)
            NOTICE -> node.deserializeTo<NoticeEventData>(ctxt)
            REQUEST -> node.deserializeTo<RequestEventData>(ctxt)
            META_EVENT -> node.deserializeTo<MetaEventData>(ctxt)
            else -> throw IllegalArgumentException("Unknown post type: $postType")
        }
    }
}