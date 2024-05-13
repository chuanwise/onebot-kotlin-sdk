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
import cn.chuanwise.onebot.v11.io.data.BUSID
import cn.chuanwise.onebot.v11.io.data.DURATION
import cn.chuanwise.onebot.v11.io.data.FILE
import cn.chuanwise.onebot.v11.io.data.FRIEND_ADD
import cn.chuanwise.onebot.v11.io.data.FRIEND_RECALL
import cn.chuanwise.onebot.v11.io.data.GROUP_ADMIN
import cn.chuanwise.onebot.v11.io.data.GROUP_BAN
import cn.chuanwise.onebot.v11.io.data.GROUP_DECREASE
import cn.chuanwise.onebot.v11.io.data.GROUP_ID
import cn.chuanwise.onebot.v11.io.data.GROUP_INCREASE
import cn.chuanwise.onebot.v11.io.data.GROUP_POKE
import cn.chuanwise.onebot.v11.io.data.GROUP_RECALL
import cn.chuanwise.onebot.v11.io.data.GROUP_UPLOAD
import cn.chuanwise.onebot.v11.io.data.HONOR
import cn.chuanwise.onebot.v11.io.data.HONOR_TYPE
import cn.chuanwise.onebot.v11.io.data.ID
import cn.chuanwise.onebot.v11.io.data.LUCKY_KING
import cn.chuanwise.onebot.v11.io.data.MESSAGE_ID
import cn.chuanwise.onebot.v11.io.data.NAME
import cn.chuanwise.onebot.v11.io.data.NOTICE_TYPE
import cn.chuanwise.onebot.v11.io.data.OPERATOR_ID
import cn.chuanwise.onebot.v11.io.data.SIZE
import cn.chuanwise.onebot.v11.io.data.SUB_TYPE
import cn.chuanwise.onebot.v11.io.data.TARGET_ID
import cn.chuanwise.onebot.v11.io.data.USER_ID
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.node.ObjectNode


/**
 * # Notice Event Data
 *
 * Notice event low-level representation based on
 * [OneBot 11 Notice Event](https://github.com/botuniverse/onebot-11/blob/master/event/notice.md).
 *
 * @author Chuanwise
 */
@JsonDeserialize(using = NoticeEventDataDeserializer::class)
sealed class NoticeEventData : EventData() {
    @get:JsonProperty(NOTICE_TYPE)
    abstract val noticeType: String
}

data class FileData(
    @JsonProperty(ID)
    val id: String,

    @JsonProperty(NAME)
    val name: String,

    // bytes
    @JsonProperty(SIZE)
    val size: Long,

    // unknown usages
    @JsonProperty(BUSID)
    val busid: Long,
)

data class GroupFileUploadEventData(
    override val time: Long,
    override val selfID: Long,

    // "notice"
    override val postType: String,

    // "group_upload"
    override val noticeType: String,

    @JsonProperty(GROUP_ID)
    val groupID: Long,

    @JsonProperty(USER_ID)
    val userID: Long,

    @JsonProperty(FILE)
    val file: FileData
) : NoticeEventData()


data class GroupAdminChangedEventData(
    override val time: Long,
    override val selfID: Long,

    // "notice"
    override val postType: String,

    // "group_admin"
    override val noticeType: String,

    @JsonProperty(GROUP_ID)
    val groupID: Long,

    @JsonProperty(USER_ID)
    val userID: Long,

    // "set" or "unset"
    @JsonProperty(SUB_TYPE)
    val subType: String,
) : NoticeEventData()


data class GroupMemberChangedEventData(
    override val time: Long,
    override val selfID: Long,

    // "notice"
    override val postType: String,

    // "group_decrease" or "group_increase"
    override val noticeType: String,

    @JsonProperty(GROUP_ID)
    val groupID: Long,

    @JsonProperty(USER_ID)
    val userID: Long,

    @JsonProperty(OPERATOR_ID)
    val operatorID: Long,

    // "leave", "kick" or "kick_me",
    // "invite" or "approve"
    @JsonProperty(SUB_TYPE)
    val subType: String,
) : NoticeEventData()


data class GroupMuteEventData(
    override val time: Long,
    override val selfID: Long,

    // "notice"
    override val postType: String,

    // "group_ban"
    override val noticeType: String,

    // "ban" or "lift_ban"
    @JsonProperty(SUB_TYPE)
    val subType: String,

    @JsonProperty(GROUP_ID)
    val groupID: Long,

    @JsonProperty(USER_ID)
    val userID: Long,

    @JsonProperty(OPERATOR_ID)
    val operatorID: Long,

    // seconds
    @JsonProperty(DURATION)
    val duration: Long,
) : NoticeEventData()


data class NewFriendEventData(
    override val time: Long,
    override val selfID: Long,

    // "notice"
    override val postType: String,

    // "friend_add"
    override val noticeType: String,

    @JsonProperty(USER_ID)
    val userID: Long,
) : NoticeEventData()


data class GroupMessageRecallEventData(
    override val time: Long,
    override val selfID: Long,

    // "notice"
    override val postType: String,

    // "group_recall"
    override val noticeType: String,

    @JsonProperty(GROUP_ID)
    val groupID: Long,

    @JsonProperty(USER_ID)
    val userID: Long,

    @JsonProperty(OPERATOR_ID)
    val operatorID: Long,

    @JsonProperty(MESSAGE_ID)
    val messageID: Long,
) : NoticeEventData()


data class FriendMessageRecallEventData(
    override val time: Long,
    override val selfID: Long,

    // "notice"
    override val postType: String,

    // "friend_recall"
    override val noticeType: String,

    @JsonProperty(USER_ID)
    val userID: Long,

    @JsonProperty(MESSAGE_ID)
    val messageID: Long,
) : NoticeEventData()


data class GroupPokeEventData(
    override val time: Long,
    override val selfID: Long,

    // "notice"
    override val postType: String,

    // "notify"
    override val noticeType: String,

    // "poke"
    @JsonProperty(SUB_TYPE)
    val subType: String,

    @JsonProperty(GROUP_ID)
    val groupID: Long,

    @JsonProperty(USER_ID)
    val userID: Long,

    @JsonProperty(TARGET_ID)
    val targetID: Long,
) : NoticeEventData()


data class GroupRedPacketLuckyKingEventData(
    override val time: Long,
    override val selfID: Long,

    // "notice"
    override val postType: String,

    // "lucky_king"
    override val noticeType: String,

    @JsonProperty(GROUP_ID)
    val groupID: Long,

    // red packet sender
    @JsonProperty(USER_ID)
    val userID: Long,

    // lucky king
    @JsonProperty(TARGET_ID)
    val targetID: Long,
) : NoticeEventData()


data class GroupMemberHonorChangedEventData(
    override val time: Long,
    override val selfID: Long,

    // "notice"
    override val postType: String,

    // "notify"
    override val noticeType: String,

    @JsonProperty(SUB_TYPE)
    val subType: String,

    @JsonProperty(GROUP_ID)
    val groupID: Long,

    @JsonProperty(USER_ID)
    val userID: Long,

    // "talkative", "performer" or "emotion"
    @JsonProperty(HONOR_TYPE)
    val honorType: String,
) : NoticeEventData()

object NoticeEventDataDeserializer : StdDeserializer<NoticeEventData>(NoticeEventData::class.java) {
    private fun readResolve(): Any = NoticeEventDataDeserializer
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): NoticeEventData {
        val node = p.codec.readTree<ObjectNode>(p)
        val value = JacksonObject(p.codec as ObjectMapper, node)

        return when (val noticeType = value[NOTICE_TYPE].toPrimitive().toString()) {
            GROUP_UPLOAD -> value.deserializeTo<GroupFileUploadEventData>()
            GROUP_ADMIN -> value.deserializeTo<GroupAdminChangedEventData>()
            GROUP_DECREASE, GROUP_INCREASE -> value.deserializeTo<GroupMemberChangedEventData>()
            GROUP_BAN -> value.deserializeTo<GroupMuteEventData>()
            FRIEND_ADD -> value.deserializeTo<NewFriendEventData>()
            GROUP_RECALL -> value.deserializeTo<GroupMessageRecallEventData>()
            FRIEND_RECALL -> value.deserializeTo<FriendMessageRecallEventData>()
            GROUP_POKE -> value.deserializeTo<GroupPokeEventData>()
            LUCKY_KING -> value.deserializeTo<GroupRedPacketLuckyKingEventData>()
            HONOR -> value.deserializeTo<GroupMemberHonorChangedEventData>()
            else -> throw IllegalArgumentException("Unexpected notice type: $noticeType")
        }
    }
}