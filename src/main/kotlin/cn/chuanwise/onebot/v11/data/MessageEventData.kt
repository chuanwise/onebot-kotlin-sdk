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

package cn.chuanwise.onebot.v11.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// https://github.com/botuniverse/onebot-11/blob/master/event/message.md
@Serializable
sealed class MessageEventData: EventData() {
    @SerialName("message_type")
    abstract val messageType: String

    @SerialName("sub_type")
    abstract val subType: String

    @SerialName("message_id")
    abstract val messageID: Long

    @SerialName("user_id")
    abstract val userID: Long

    @SerialName("message")
    abstract val message: MessageData

    @SerialName("raw_message")
    abstract val rawMessage: String

    @SerialName("font")
    abstract val font: Int

    @SerialName("sender")
    abstract val sender: SenderData
}

@Serializable
sealed class SenderData {
    @SerialName("user_id")
    abstract val userID: Long

    @SerialName("nickname")
    abstract val nickname: String

    @SerialName("sex")
    abstract val sex: String

    @SerialName("age")
    abstract val age: Int
}

@Serializable
data class PrivateSenderData(
    override val userID: Long,
    override val nickname: String,
    override val sex: String,
    override val age: Int,
) : SenderData()

@Serializable
class GroupSenderData(
    override val userID: Long,
    override val nickname: String,
    override val sex: String,
    override val age: Int,

    @SerialName("card")
    val card: String,

    @SerialName("area")
    val area: String,

    @SerialName("level")
    val level: String,

    @SerialName("role")
    val role: String,

    @SerialName("title")
    val title: String,
) : SenderData()

@Serializable
data class PrivateMessageEventData(
    override val time: Long,
    override val selfID: Long,

    // "message"
    override val postType: String,
    override val messageType: String,

    @SerialName("sub_type")
    override val subType: String,

    @SerialName("message_id")
    override val messageID: Long,

    @SerialName("user_id")
    override val userID: Long,

    @SerialName("message")
    override val message: MessageData,

    @SerialName("raw_message")
    override val rawMessage: String,

    @SerialName("font")
    override val font: Int,

    @SerialName("sender")
    override val sender: SenderData
) : MessageEventData()

@Serializable
sealed class MessageReceipt {
    @SerialName("reply")
    abstract val reply: String?

    @SerialName("auto_escape")
    abstract val autoEscape: Boolean?
}

@Serializable
data class PrivateMessageEventMessageReceipt(
    override val reply: String,
    override val autoEscape: Boolean
) : MessageReceipt()

@Serializable
data class AnonymousSenderData(
    @SerialName("id")
    val id: Int,

    @SerialName("name")
    val name: String,

    @SerialName("flag")
    val flag: String
)

@Serializable
data class GroupMessageEventData(
    override val time: Long,
    override val selfID: Long,
    override val postType: String,
    override val messageType: String,

    @SerialName("sub_type")
    override val subType: String,

    @SerialName("message_id")
    override val messageID: Long,

    @SerialName("group_id")
    val groupID: Long,

    @SerialName("user_id")
    override val userID: Long,

    @SerialName("message")
    override val message: MessageData,

    @SerialName("raw_message")
    override val rawMessage: String,

    @SerialName("font")
    override val font: Int,

    @SerialName("sender")
    override val sender: SenderData,

    @SerialName("anonymous")
    val anonymous: AnonymousSenderData?
) : MessageEventData()

@Serializable
data class GroupMessageMessageReceipt(
    override val reply: String,
    override val autoEscape: Boolean,

    @SerialName("at_sender")
    val atSender: Boolean,

    @SerialName("delete")
    val delete: Boolean,

    @SerialName("kick")
    val kick: Boolean,

    @SerialName("ban")
    val ban: Boolean,

    @SerialName("ban_duration")
    val banDuration: Long,
) : MessageReceipt()
