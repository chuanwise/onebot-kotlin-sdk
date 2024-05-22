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

import cn.chuanwise.onebot.lib.HEARTBEAT
import cn.chuanwise.onebot.lib.LIFECYCLE
import cn.chuanwise.onebot.lib.META_EVENT_TYPE
import cn.chuanwise.onebot.lib.deserializeTo
import cn.chuanwise.onebot.lib.getNotNull
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.deser.BeanDeserializerFactory
import com.fasterxml.jackson.databind.deser.std.StdNodeBasedDeserializer

/**
 * Meta event low-level representation based on
 * [OneBot 11 Meta Event](https://github.com/botuniverse/onebot-11/blob/master/event/meta.md).
 */
interface MetaEventData : EventData {
    val metaEventType: String
}


data class LifecycleMetaEventData(
    override val time: Long,
    override val selfID: Long,

    // "meta_event"
    override val postType: String,

    // "lifecycle"
    override val metaEventType: String,

    // "enable", "disable" or "connect"
    val subType: String
) : MetaEventData


data class HeartbeatEventData(
    override val time: Long,
    override val selfID: Long,

    // "meta_event"
    override val postType: String,

    // "heartbeat"
    override val metaEventType: String,

    val status: Any?,
    val interval: Long
) : MetaEventData


object MetaEventDataDeserializer : StdNodeBasedDeserializer<MetaEventData>(MetaEventData::class.java) {
    private fun readResolve(): Any = MetaEventDataDeserializer
    override fun convert(root: JsonNode, ctxt: DeserializationContext): MetaEventData {
        val mapper = ctxt.parser.codec as ObjectMapper

        val beanDeserializer = BeanDeserializerFactory.instance.createBeanDeserializer(
            ctxt,
            ctxt.typeFactory.constructType(MetaEventData::class.java),
            ctxt.config.introspect(ctxt.typeFactory.constructType(MetaEventData::class.java))
        )

        return when (val subType = root.getNotNull(META_EVENT_TYPE).asText()) {
            LIFECYCLE -> root.deserializeTo<LifecycleMetaEventData>(mapper)
            HEARTBEAT -> root.deserializeTo<HeartbeatEventData>(mapper)
            else -> throw IllegalArgumentException("Unexpected sub type: $subType")
        }
    }
}