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

import cn.chuanwise.onebot.lib.FRIEND
import cn.chuanwise.onebot.lib.GROUP
import cn.chuanwise.onebot.lib.REQUEST_TYPE
import cn.chuanwise.onebot.lib.deserializeTo
import cn.chuanwise.onebot.lib.getNotNull
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.node.ObjectNode

/**
 * Request event low-level representation based on
 * [OneBot 11 Request Event](https://github.com/botuniverse/onebot-11/blob/master/event/request.md).
 *
 * @author Chuanwise
 */
interface RequestEventData : EventData {
    val requestType: String
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
) : RequestEventData


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
) : RequestEventData


data class GroupAddRequestReceiptData(
    val approve: Boolean?,
    val reason: String?
)


object RequestEventDataDeserializer : StdDeserializer<RequestEventData>(RequestEventData::class.java) {
    private fun readResolve(): Any = RequestEventDataDeserializer
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): RequestEventData {
        val node = p.readValueAsTree<ObjectNode>()
        val mapper = p.codec as ObjectMapper

        return when (val requestType = node.getNotNull(REQUEST_TYPE).asText()) {
            FRIEND -> node.deserializeTo<FriendAddRequestEventData>(mapper)
            GROUP -> node.deserializeTo<GroupAddRequestEventData>(mapper)
            else -> throw IllegalArgumentException("Unexpected request type: $requestType")
        }
    }
}
