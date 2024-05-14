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
import cn.chuanwise.onebot.v11.io.data.GROUP
import cn.chuanwise.onebot.v11.io.data.MESSAGE_TYPE
import cn.chuanwise.onebot.v11.io.data.PRIVATE
import cn.chuanwise.onebot.v11.io.data.message.MessageData
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.node.ObjectNode

/**
 * # Message Event Data
 *
 * Message event data a representation of message event packet data defined in
 * [OneBot 11 Message Event](https://github.com/botuniverse/onebot-11/blob/master/event/message.md).
 *
 * @author Chuanwise
 */
@JsonDeserialize(using = MessageEventDataDeserializer::class)
sealed class MessageEventData : EventData() {
    abstract val messageType: String
    abstract val subType: String
    abstract val messageID: Long
    abstract val userID: Long
    abstract val message: MessageData
    abstract val rawMessage: String
    abstract val font: Int
    abstract val sender: SenderData
}

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
sealed class SenderData {
    abstract val userID: Long
    abstract val nickname: String
    abstract val sex: String?
    abstract val age: Int?
}

data class PrivateSenderData(
    override val userID: Long,
    override val nickname: String,
    override val sex: String?,
    override val age: Int?,
) : SenderData()

data class GroupSenderData(
    override val userID: Long,
    override val nickname: String,
    override val sex: String?,
    override val age: Int?,
    val card: String?,
    val area: String?,
    val level: String?,
    val role: String?,
    val title: String?,
) : SenderData()


data class PrivateMessageEventData(
    override val time: Long,
    override val selfID: Long,

    // MESSAGE
    override val postType: String,
    override val messageType: String,
    override val subType: String,

    override val messageID: Long,
    override val userID: Long,
    override val message: MessageData,
    override val rawMessage: String,
    override val font: Int,
    override val sender: PrivateSenderData
) : MessageEventData()


@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
sealed class MessageReceipt {
    abstract val reply: String?
    abstract val autoEscape: Boolean?
}


data class PrivateMessageEventMessageReceipt(
    override val reply: String,
    override val autoEscape: Boolean
) : MessageReceipt()


@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class AnonymousSenderData(
    val id: Int,
    val name: String,
    val flag: String
)


data class GroupMessageEventData(
    override val time: Long,
    override val selfID: Long,
    override val postType: String,
    override val messageType: String,

    override val subType: String,
    override val messageID: Long,

    val groupID: Long,
    override val userID: Long,
    override val message: MessageData,
    override val rawMessage: String,
    override val font: Int,
    override val sender: GroupSenderData,
    val anonymous: AnonymousSenderData?
) : MessageEventData()


data class GroupMessageMessageReceipt(
    override val reply: String,
    override val autoEscape: Boolean,

    val atSender: Boolean,
    val delete: Boolean,
    val kick: Boolean,
    val ban: Boolean,
    val banDuration: Long,
) : MessageReceipt()

object MessageEventDataDeserializer : StdDeserializer<MessageEventData>(MessageEventData::class.java) {
    private fun readResolve(): Any = MessageEventDataDeserializer
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): MessageEventData {
        val node = p.codec.readTree<ObjectNode>(p)
        val value = JacksonObject(p.codec as ObjectMapper, node)

        return when (val messageType = value[MESSAGE_TYPE].toPrimitive().toString()) {
            PRIVATE -> value.deserializeTo<PrivateMessageEventData>()
            GROUP -> value.deserializeTo<GroupMessageEventData>()
            else -> throw IllegalArgumentException("Unexpected message type: $messageType")
        }
    }
}