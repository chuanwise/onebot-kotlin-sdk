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

import cn.chuanwise.onebot.lib.FRIEND_ADD
import cn.chuanwise.onebot.lib.FRIEND_RECALL
import cn.chuanwise.onebot.lib.GROUP_ADMIN
import cn.chuanwise.onebot.lib.GROUP_BAN
import cn.chuanwise.onebot.lib.GROUP_DECREASE
import cn.chuanwise.onebot.lib.GROUP_INCREASE
import cn.chuanwise.onebot.lib.GROUP_POKE
import cn.chuanwise.onebot.lib.GROUP_RECALL
import cn.chuanwise.onebot.lib.GROUP_UPLOAD
import cn.chuanwise.onebot.lib.HONOR
import cn.chuanwise.onebot.lib.LUCKY_KING
import cn.chuanwise.onebot.lib.NOTICE_TYPE
import cn.chuanwise.onebot.lib.deserializeTo
import cn.chuanwise.onebot.lib.getNotNull
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.node.ObjectNode


/**
 * Notice event low-level representation based on
 * [OneBot 11 Notice Event](https://github.com/botuniverse/onebot-11/blob/master/event/notice.md).
 *
 * @author Chuanwise
 */
interface NoticeEventData : EventData {
    val noticeType: String
}

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class FileData(
    val id: String,
    val name: String,

    // bytes
    val size: Long,

    // unknown usages
    val busid: Long,
)

data class GroupFileUploadEventData(
    override val time: Long,
    override val selfID: Long,

    // "notice"
    override val postType: String,

    // "group_upload"
    override val noticeType: String,

    val groupID: Long,
    val userID: Long,
    val file: FileData
) : NoticeEventData


data class GroupAdminChangedEventData(
    override val time: Long,
    override val selfID: Long,

    // "notice"
    override val postType: String,

    // "group_admin"
    override val noticeType: String,

    val groupID: Long,
    val userID: Long,

    // "set" or "unset"
    val subType: String,
) : NoticeEventData


data class GroupMemberChangedEventData(
    override val time: Long,
    override val selfID: Long,

    // "notice"
    override val postType: String,

    // "group_decrease" or "group_increase"
    override val noticeType: String,

    val groupID: Long,
    val userID: Long,
    val operatorID: Long,

    // "leave", "kick" or "kick_me",
    // "invite" or "approve"
    val subType: String,
) : NoticeEventData


data class GroupMuteEventData(
    override val time: Long,
    override val selfID: Long,

    // "notice"
    override val postType: String,

    // "group_ban"
    override val noticeType: String,

    // "ban" or "lift_ban"
    val subType: String,
    val groupID: Long,
    val userID: Long,
    val operatorID: Long,

    // seconds
    val duration: Long,
) : NoticeEventData


data class NewFriendEventData(
    override val time: Long,
    override val selfID: Long,

    // "notice"
    override val postType: String,

    // "friend_add"
    override val noticeType: String,

    val userID: Long,
) : NoticeEventData


data class GroupMessageRecallEventData(
    override val time: Long,
    override val selfID: Long,

    // "notice"
    override val postType: String,

    // "group_recall"
    override val noticeType: String,

    val groupID: Long,
    val userID: Long,
    val operatorID: Long,
    val messageID: Int,
) : NoticeEventData


data class FriendMessageRecallEventData(
    override val time: Long,
    override val selfID: Long,

    // "notice"
    override val postType: String,

    // "friend_recall"
    override val noticeType: String,

    val userID: Long,
    val messageID: Int,
) : NoticeEventData


data class GroupPokeEventData(
    override val time: Long,
    override val selfID: Long,

    // "notice"
    override val postType: String,

    // "notify"
    override val noticeType: String,

    // "poke"
    val subType: String,
    val groupID: Long,
    val userID: Long,
    val targetID: Long,
) : NoticeEventData


data class GroupRedPacketLuckyKingEventData(
    override val time: Long,
    override val selfID: Long,

    // "notice"
    override val postType: String,

    // "lucky_king"
    override val noticeType: String,

    val groupID: Long,

    // red packet sender
    val userID: Long,

    // lucky king
    val targetID: Long,
) : NoticeEventData


data class GroupMemberHonorChangedEventData(
    override val time: Long,
    override val selfID: Long,

    // "notice"
    override val postType: String,

    // "notify"
    override val noticeType: String,

    val subType: String,
    val groupID: Long,
    val userID: Long,

    // "talkative", "performer" or "emotion"
    val honorType: String,
) : NoticeEventData

object NoticeEventDataDeserializer : StdDeserializer<NoticeEventData>(NoticeEventData::class.java) {
    private fun readResolve(): Any = NoticeEventDataDeserializer
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): NoticeEventData {
        val node = p.readValueAsTree<ObjectNode>()
        return when (val noticeType = node.getNotNull(NOTICE_TYPE).asText()) {
            GROUP_UPLOAD -> node.deserializeTo<GroupFileUploadEventData>(ctxt)
            GROUP_ADMIN -> node.deserializeTo<GroupAdminChangedEventData>(ctxt)
            GROUP_DECREASE, GROUP_INCREASE -> node.deserializeTo<GroupMemberChangedEventData>(ctxt)
            GROUP_BAN -> node.deserializeTo<GroupMuteEventData>(ctxt)
            FRIEND_ADD -> node.deserializeTo<NewFriendEventData>(ctxt)
            GROUP_RECALL -> node.deserializeTo<GroupMessageRecallEventData>(ctxt)
            FRIEND_RECALL -> node.deserializeTo<FriendMessageRecallEventData>(ctxt)
            GROUP_POKE -> node.deserializeTo<GroupPokeEventData>(ctxt)
            LUCKY_KING -> node.deserializeTo<GroupRedPacketLuckyKingEventData>(ctxt)
            HONOR -> node.deserializeTo<GroupMemberHonorChangedEventData>(ctxt)
            else -> throw IllegalArgumentException("Unexpected notice type: $noticeType")
        }
    }
}