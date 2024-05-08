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

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// https://github.com/botuniverse/onebot-11/blob/master/event/notice.md
@Serializable
sealed class NoticeEventData: EventData() {
    @SerialName("notice_type")
    abstract val noticeType: String
}

@Serializable
class FileData(
    @SerialName("id")
    val id: String,

    @SerialName("name")
    val name: String,

    // bytes
    @SerialName("size")
    val size: Long,

    // unknown usages
    @SerialName("busid")
    val busid: Long,
)

@Serializable
class GroupFileUploadEventData(
    override val time: Long,
    override val selfID: Long,

    // "notice"
    override val postType: String,

    // "group_upload"
    override val noticeType: String,

    @SerialName("group_id")
    val groupID: Long,

    @SerialName("user_id")
    val userID: Long,

    @SerialName("file")
    val file: FileData
): NoticeEventData()

@Serializable
class GroupAdminChangedEventData(
    override val time: Long,
    override val selfID: Long,

    // "notice"
    override val postType: String,

    // "group_admin"
    override val noticeType: String,

    @SerialName("group_id")
    val groupID: Long,

    @SerialName("user_id")
    val userID: Long,

    // "set" or "unset"
    @SerialName("sub_type")
    val subType: String,
): NoticeEventData()

@Serializable
class GroupMemberChangedEventData(
    override val time: Long,
    override val selfID: Long,

    // "notice"
    override val postType: String,

    // "group_decrease" or "group_increase"
    override val noticeType: String,

    @SerialName("group_id")
    val groupID: Long,

    @SerialName("user_id")
    val userID: Long,

    @SerialName("operator_id")
    val operatorID: Long,

    // "leave", "kick" or "kick_me",
    // "invite" or "approve"
    @SerialName("sub_type")
    val subType: String,
): NoticeEventData()

@Serializable
class GroupMuteEventData(
    override val time: Long,
    override val selfID: Long,

    // "notice"
    override val postType: String,

    // "group_ban"
    override val noticeType: String,

    // "ban" or "lift_ban"
    @SerialName("sub_type")
    val subType: String,

    @SerialName("group_id")
    val groupID: Long,

    @SerialName("user_id")
    val userID: Long,

    @SerialName("operator_id")
    val operatorID: Long,

    // seconds
    @SerialName("duration")
    val duration: Long,
): NoticeEventData()

@Serializable
class NewFriendEventData(
    override val time: Long,
    override val selfID: Long,

    // "notice"
    override val postType: String,

    // "friend_add"
    override val noticeType: String,

    @SerialName("user_id")
    val userID: Long,
): NoticeEventData()

@Serializable
class GroupMessageRecallEventData(
    override val time: Long,
    override val selfID: Long,

    // "notice"
    override val postType: String,

    // "group_recall"
    override val noticeType: String,

    @SerialName("group_id")
    val groupID: Long,

    @SerialName("user_id")
    val userID: Long,

    @SerialName("operator_id")
    val operatorID: Long,

    @SerialName("message_id")
    val messageID: Long,
): NoticeEventData()

@Serializable
class FriendMessageRecallEventData(
    override val time: Long,
    override val selfID: Long,

    // "notice"
    override val postType: String,

    // "friend_recall"
    override val noticeType: String,

    @SerialName("user_id")
    val userID: Long,

    @SerialName("message_id")
    val messageID: Long,
): NoticeEventData()

@Serializable
class GroupPokeEventData(
    override val time: Long,
    override val selfID: Long,

    // "notice"
    override val postType: String,

    // "notify"
    override val noticeType: String,

    // "poke"
    @SerialName("sub_type")
    val subType: String,

    @SerialName("group_id")
    val groupID: Long,

    @SerialName("user_id")
    val userID: Long,

    @SerialName("target_id")
    val targetID: Long,
): NoticeEventData()

@Serializable
class GroupRedPacketLuckyKingEventData(
    override val time: Long,
    override val selfID: Long,

    // "notice"
    override val postType: String,

    // "lucky_king"
    override val noticeType: String,

    @SerialName("group_id")
    val groupID: Long,

    // red packet sender
    @SerialName("user_id")
    val userID: Long,

    // lucky king
    @SerialName("target_id")
    val targetID: Long,
): NoticeEventData()

@Serializable
class GroupMemberHonorChangedEventData(
    override val time: Long,
    override val selfID: Long,

    // "notice"
    override val postType: String,

    // "notify"
    override val noticeType: String,

    @SerialName("sub_type")
    val subType: String,

    @SerialName("group_id")
    val groupID: Long,

    @SerialName("user_id")
    val userID: Long,

    // "talkative", "performer" or "emotion"
    @SerialName("honor_type")
    val honorType: String,
): NoticeEventData()