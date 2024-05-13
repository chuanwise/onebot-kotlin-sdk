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
import cn.chuanwise.onebot.io.data.Object
import cn.chuanwise.onebot.io.data.deserializeTo
import cn.chuanwise.onebot.io.data.toPrimitive
import cn.chuanwise.onebot.v11.io.data.HEARTBEAT
import cn.chuanwise.onebot.v11.io.data.INTERVAL
import cn.chuanwise.onebot.v11.io.data.LIFECYCLE
import cn.chuanwise.onebot.v11.io.data.META_EVENT_TYPE
import cn.chuanwise.onebot.v11.io.data.STATUS
import cn.chuanwise.onebot.v11.io.data.SUB_TYPE
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.node.ObjectNode

/**
 * # Meta Event Data
 *
 * Meta event low-level representation based on
 * [OneBot 11 Meta Event](https://github.com/botuniverse/onebot-11/blob/master/event/meta.md).
 */
@JsonDeserialize(using = MetaEventDataDeserializer::class)
sealed class MetaEventData : EventData() {
    @get:JsonProperty(META_EVENT_TYPE)
    abstract val metaEventType: String
}

data class LifecycleMetaEventData(
    override val time: Long,
    override val selfID: Long,

    // "meta_event"
    override val postType: String,

    // "lifecycle"
    override val metaEventType: String,

    // "enable", "disable" or "connect"
    @JsonProperty(SUB_TYPE)
    val subType: String
) : MetaEventData()

data class HeartbeatEventData(
    override val time: Long,
    override val selfID: Long,

    // "meta_event"
    override val postType: String,

    // "heartbeat"
    override val metaEventType: String,

    @JsonProperty(STATUS)
    val status: Object?,

    @JsonProperty(INTERVAL)
    val interval: Long
) : MetaEventData()


object MetaEventDataDeserializer : StdDeserializer<MetaEventData>(MetaEventData::class.java) {
    private fun readResolve(): Any = MetaEventDataDeserializer
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): MetaEventData {
        val node = p.codec.readTree<ObjectNode>(p)
        val value = JacksonObject(p.codec as ObjectMapper, node)

        return when (val subType = value[META_EVENT_TYPE].toPrimitive().toString()) {
            LIFECYCLE -> value.deserializeTo<LifecycleMetaEventData>()
            HEARTBEAT -> value.deserializeTo<HeartbeatEventData>()
            else -> throw IllegalArgumentException("Unexpected sub type: $subType")
        }
    }
}