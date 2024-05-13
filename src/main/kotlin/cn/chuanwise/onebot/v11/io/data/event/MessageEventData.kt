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
import cn.chuanwise.onebot.v11.io.data.AGE
import cn.chuanwise.onebot.v11.io.data.ANONYMOUS
import cn.chuanwise.onebot.v11.io.data.AREA
import cn.chuanwise.onebot.v11.io.data.AT_SENDER
import cn.chuanwise.onebot.v11.io.data.AUTO_ESCAPE
import cn.chuanwise.onebot.v11.io.data.BAN
import cn.chuanwise.onebot.v11.io.data.BAN_DURATION
import cn.chuanwise.onebot.v11.io.data.CARD
import cn.chuanwise.onebot.v11.io.data.DELETE
import cn.chuanwise.onebot.v11.io.data.FLAG
import cn.chuanwise.onebot.v11.io.data.FONT
import cn.chuanwise.onebot.v11.io.data.GROUP
import cn.chuanwise.onebot.v11.io.data.GROUP_ID
import cn.chuanwise.onebot.v11.io.data.ID
import cn.chuanwise.onebot.v11.io.data.KICK
import cn.chuanwise.onebot.v11.io.data.LEVEL
import cn.chuanwise.onebot.v11.io.data.MESSAGE
import cn.chuanwise.onebot.v11.io.data.MESSAGE_ID
import cn.chuanwise.onebot.v11.io.data.MESSAGE_TYPE
import cn.chuanwise.onebot.v11.io.data.NAME
import cn.chuanwise.onebot.v11.io.data.NICKNAME
import cn.chuanwise.onebot.v11.io.data.PRIVATE
import cn.chuanwise.onebot.v11.io.data.RAW_MESSAGE
import cn.chuanwise.onebot.v11.io.data.REPLY
import cn.chuanwise.onebot.v11.io.data.ROLE
import cn.chuanwise.onebot.v11.io.data.SENDER
import cn.chuanwise.onebot.v11.io.data.SEX
import cn.chuanwise.onebot.v11.io.data.SUB_TYPE
import cn.chuanwise.onebot.v11.io.data.TITLE
import cn.chuanwise.onebot.v11.io.data.USER_ID
import cn.chuanwise.onebot.v11.io.data.message.MessageData
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
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
    @get:JsonProperty(MESSAGE_TYPE)
    abstract val messageType: String

    @get:JsonProperty(SUB_TYPE)
    abstract val subType: String

    @get:JsonProperty(MESSAGE_ID)
    abstract val messageID: Long

    @get:JsonProperty(USER_ID)
    abstract val userID: Long

    @get:JsonProperty(MESSAGE)
    abstract val message: MessageData

    @get:JsonProperty(RAW_MESSAGE)
    abstract val rawMessage: String

    @get:JsonProperty(FONT)
    abstract val font: Int

    @get:JsonProperty(SENDER)
    abstract val sender: SenderData
}

sealed class SenderData {
    @get:JsonProperty(USER_ID)
    abstract val userID: Long

    @get:JsonProperty(NICKNAME)
    abstract val nickname: String

    @get:JsonProperty(SEX)
    abstract val sex: String?

    @get:JsonProperty(AGE)
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

    @JsonProperty(CARD)
    val card: String?,

    @JsonProperty(AREA)
    val area: String?,

    @JsonProperty(LEVEL)
    val level: String?,

    @JsonProperty(ROLE)
    val role: String?,

    @JsonProperty(TITLE)
    val title: String?,
) : SenderData()


data class PrivateMessageEventData(
    override val time: Long,
    override val selfID: Long,

    // MESSAGE
    override val postType: String,
    override val messageType: String,

    @JsonProperty(SUB_TYPE)
    override val subType: String,

    @JsonProperty(MESSAGE_ID)
    override val messageID: Long,

    @JsonProperty(USER_ID)
    override val userID: Long,

    @JsonProperty(MESSAGE)
    override val message: MessageData,

    @JsonProperty(RAW_MESSAGE)
    override val rawMessage: String,

    @JsonProperty(FONT)
    override val font: Int,

    @JsonProperty(SENDER)
    override val sender: PrivateSenderData
) : MessageEventData()


sealed class MessageReceipt {
    @get:JsonProperty(REPLY)
    abstract val reply: String?

    @get:JsonProperty(AUTO_ESCAPE)
    abstract val autoEscape: Boolean?
}


data class PrivateMessageEventMessageReceipt(
    override val reply: String,
    override val autoEscape: Boolean
) : MessageReceipt()


data class AnonymousSenderData(
    @JsonProperty(ID)
    val id: Int,

    @JsonProperty(NAME)
    val name: String,

    @JsonProperty(FLAG)
    val flag: String
)


data class GroupMessageEventData(
    override val time: Long,
    override val selfID: Long,
    override val postType: String,
    override val messageType: String,

    @JsonProperty(SUB_TYPE)
    override val subType: String,

    @JsonProperty(MESSAGE_ID)
    override val messageID: Long,

    @JsonProperty(GROUP_ID)
    val groupID: Long,

    @JsonProperty(USER_ID)
    override val userID: Long,

    @JsonProperty(MESSAGE)
    override val message: MessageData,

    @JsonProperty(RAW_MESSAGE)
    override val rawMessage: String,

    @JsonProperty(FONT)
    override val font: Int,

    @JsonProperty(SENDER)
    override val sender: GroupSenderData,

    @JsonProperty(ANONYMOUS)
    val anonymous: AnonymousSenderData?
) : MessageEventData()


data class GroupMessageMessageReceipt(
    override val reply: String,
    override val autoEscape: Boolean,

    @JsonProperty(AT_SENDER)
    val atSender: Boolean,

    @JsonProperty(DELETE)
    val delete: Boolean,

    @JsonProperty(KICK)
    val kick: Boolean,

    @JsonProperty(BAN)
    val ban: Boolean,

    @JsonProperty(BAN_DURATION)
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