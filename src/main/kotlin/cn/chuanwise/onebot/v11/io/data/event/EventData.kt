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

package cn.chuanwise.onebot.v11.io.data.event

import cn.chuanwise.onebot.io.data.JacksonObject
import cn.chuanwise.onebot.io.data.deserializeTo
import cn.chuanwise.onebot.io.data.toPrimitive
import cn.chuanwise.onebot.v11.io.data.IncomingData
import cn.chuanwise.onebot.v11.io.data.MESSAGE
import cn.chuanwise.onebot.v11.io.data.META_EVENT
import cn.chuanwise.onebot.v11.io.data.NOTICE
import cn.chuanwise.onebot.v11.io.data.POST_TYPE
import cn.chuanwise.onebot.v11.io.data.REQUEST
import cn.chuanwise.onebot.v11.io.data.SELF_ID
import cn.chuanwise.onebot.v11.io.data.TIME
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.node.ObjectNode

/**
 * # Event Data
 *
 * Event low-level representation based on
 * [OneBot 11](https://github.com/botuniverse/onebot-11/blob/master/event).
 *
 * @author Chuanwise
 */
@JsonDeserialize(using = EventDataDeserializer::class)
sealed class EventData : IncomingData {
    @get:JsonProperty(TIME)
    abstract val time: Long

    @get:JsonProperty(SELF_ID)
    abstract val selfID: Long

    @get:JsonProperty(POST_TYPE)
    abstract val postType: String
}

object EventDataDeserializer : StdDeserializer<EventData>(EventData::class.java) {
    private fun readResolve(): Any = EventDataDeserializer
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): EventData {
        val node = p.codec.readTree<ObjectNode>(p)
        val value = JacksonObject(p.codec as ObjectMapper, node)

        return when (val postType = value[POST_TYPE].toPrimitive().toString()) {
            MESSAGE -> value.deserializeTo<MessageEventData>()
            NOTICE -> value.deserializeTo<NoticeEventData>()
            REQUEST -> value.deserializeTo<RequestEventData>()
            META_EVENT -> value.deserializeTo<MessageEventData>()
            else -> throw IllegalArgumentException("Unknown post type: $postType")
        }
    }
}