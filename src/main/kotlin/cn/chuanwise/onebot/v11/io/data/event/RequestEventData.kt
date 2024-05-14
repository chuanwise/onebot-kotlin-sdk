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
import cn.chuanwise.onebot.v11.io.data.FRIEND
import cn.chuanwise.onebot.v11.io.data.GROUP
import cn.chuanwise.onebot.v11.io.data.REQUEST_TYPE
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.node.ObjectNode

/**
 * Request event low-level representation based on
 * [OneBot 11 Request Event](https://github.com/botuniverse/onebot-11/blob/master/event/request.md).
 *
 * @author Chuanwise
 */
@JsonDeserialize(using = RequestEventDataDeserializer::class)
sealed class RequestEventData : EventData() {
    abstract val requestType: String
}


data class FriendAddRequestEventData(
    override val time: Long,
    override val selfID: Long,

    // "request"
    override val postType: String,

    // "friend"
    override val requestType: String,

    val userID: Long,
    val comment: String,
    val flag: String,
) : RequestEventData()


data class FriendAddRequestReceiptData(
    val approve: Boolean?,
    val remark: String?
)


data class GroupAddRequestEventData(
    override val time: Long,
    override val selfID: Long,

    // "request"
    override val postType: String,

    // "group"
    override val requestType: String,

    // "add" or "invite"
    val subType: String,
    val groupID: Long,
    val userID: Long,
    val comment: String,
    val flag: String,
) : RequestEventData()


data class GroupAddRequestReceiptData(
    val approve: Boolean?,
    val reason: String?
)


object RequestEventDataDeserializer : StdDeserializer<RequestEventData>(RequestEventData::class.java) {
    private fun readResolve(): Any = RequestEventDataDeserializer
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): RequestEventData {
        val node = p.codec.readTree<ObjectNode>(p)
        val value = JacksonObject(p.codec as ObjectMapper, node)

        return when (val requestType = value[REQUEST_TYPE].toPrimitive().toString()) {
            FRIEND -> value.deserializeTo<FriendAddRequestEventData>()
            GROUP -> value.deserializeTo<GroupAddRequestEventData>()
            else -> throw IllegalArgumentException("Unexpected request type: $requestType")
        }
    }
}
