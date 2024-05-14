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

package cn.chuanwise.onebot.v11.io.data

import cn.chuanwise.onebot.io.data.deserializeTo
import cn.chuanwise.onebot.io.data.getOptionalNotNull
import cn.chuanwise.onebot.v11.io.data.action.ResponseData
import cn.chuanwise.onebot.v11.io.data.event.EventData
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.node.ObjectNode

/**
 * Marked interface for incoming data.
 *
 * @author Chuanwise
 */
@JsonDeserialize(using = IncomingDataDeserializer::class)
interface IncomingData

object IncomingDataDeserializer : StdDeserializer<IncomingData>(IncomingData::class.java) {
    private fun readResolve(): Any = IncomingDataDeserializer
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): IncomingData {
        // 1. incoming message may be event or response
        val node = p.codec.readTree<ObjectNode>(p)
        val optionalRetCode = node.getOptionalNotNull(RETCODE)
        return if (optionalRetCode === null) {
            // 2. if field "retcode" not present, it is an event.
            node.deserializeTo<EventData>()
        } else {
            // 3. if field "retcode" present, it is a response.
            node.deserializeTo<ResponseData<*>>()
        }
    }
}
