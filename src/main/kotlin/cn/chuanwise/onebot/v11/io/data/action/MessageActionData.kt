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

package cn.chuanwise.onebot.v11.io.data.action

import cn.chuanwise.onebot.v11.io.data.event.AnonymousSenderData
import cn.chuanwise.onebot.v11.io.data.event.SenderData
import cn.chuanwise.onebot.v11.io.data.message.MessageData
import com.fasterxml.jackson.annotation.JsonProperty

data class MessageIDData(
    @JsonProperty("message_id")
    val messageID: Int
)

data class SendGroupMessageData(
    @JsonProperty("group_id")
    val groupID: Long,

    @JsonProperty("message")
    val message: MessageData,

    @JsonProperty("auto_escape")
    val autoEscape: Boolean = false,
)

data class SendPrivateMessageData(
    @JsonProperty("user_id")
    val userID: Long,

    @JsonProperty("message")
    val message: MessageData,

    @JsonProperty("auto_escape")
    val autoEscape: Boolean = false,
)

data class SendMessageData(
    // "private" or "group"
    @JsonProperty("message_type")
    val messageType: String?,

    // if not null, send to this user
    @JsonProperty("user_id")
    val userID: Long?,

    // if not null, send to this group
    @JsonProperty("group_id")
    val groupID: Long?,

    @JsonProperty("message")
    val message: MessageData,

    @JsonProperty("auto_escape")
    val autoEscape: Boolean = false,
)

data class GetMessageData(
    @JsonProperty("time")
    val time: Int,

    // "private" or "group"
    @JsonProperty("message_type")
    val messageType: String?,

    @JsonProperty("message_id")
    val messageID: Int,

    @JsonProperty("real_id")
    val realID: Int,

    @JsonProperty("sender")
    val sender: SenderData,

    @JsonProperty("message")
    val message: MessageData,
)

data class MessageDataWrapper(
    @JsonProperty("message")
    val message: MessageData,
)

data class SendLikeData(
    @JsonProperty("user_id")
    val userID: Long,

    @JsonProperty("times")
    val times: Int,
)

data class SendGroupKickData(
    @JsonProperty("group_id")
    val groupID: Long,

    @JsonProperty("user_id")
    val userID: Long,

    @JsonProperty("reject_add_request")
    val rejectAddRequest: Boolean = false,
)

data class SetGroupBanData(
    @JsonProperty("group_id")
    val groupID: Long,

    @JsonProperty("user_id")
    val userID: Long,

    // seconds, 0 to release
    @JsonProperty("duration")
    val duration: Long = 30 * 60
)

data class SetGroupAnonymousBanData(
    @JsonProperty("group_id")
    val groupID: Long,

    @JsonProperty("anonymous")
    val anonymous: AnonymousSenderData?,

    @JsonProperty("flag")
    val flag: String?,

    // seconds, 0 to release
    @JsonProperty("duration")
    val duration: Long = 30 * 60
)

data class GroupIDEnableData(
    @JsonProperty("group_id")
    val groupID: Long,

    @JsonProperty("enable")
    val enable: Boolean = true
)

data class GroupIDUserIDEnableData(
    @JsonProperty("group_id")
    val groupID: Long,

    @JsonProperty("user_id")
    val userID: Long,

    @JsonProperty("enable")
    val enable: Boolean = true
)

data class SetGroupCardData(
    @JsonProperty("group_id")
    val groupID: Long,

    @JsonProperty("user_id")
    val userID: Long,

    @JsonProperty("card")
    val card: String? = null
)

data class SetGroupNameData(
    @JsonProperty("group_id")
    val groupID: Long,

    @JsonProperty("group_name")
    val groupName: String
)

data class SetGroupLeaveData(
    @JsonProperty("group_id")
    val groupID: Long,

    @JsonProperty("is_dismiss")
    val isDismiss: Boolean = false
)

data class SetGroupSpecialTitleData(
    @JsonProperty("group_id")
    val groupID: Long,

    @JsonProperty("user_id")
    val userID: Long,

    @JsonProperty("special_title")
    val specialTitle: String? = null,

    // seconds, 0 to release
    @JsonProperty("duration")
    val duration: Long = -1
)

data class SetFriendAddRequestData(
    @JsonProperty("flag")
    val flag: String,

    @JsonProperty("approve")
    val approve: Boolean = true,

    @JsonProperty("remark")
    val remark: String? = null
)

data class SetGroupAddRequestData(
    @JsonProperty("flag")
    val flag: String,

    @JsonProperty("sub_type")
    val subType: String,

    @JsonProperty("approve")
    val approve: Boolean = true,

    @JsonProperty("reason")
    val reason: String? = null
)

data class GetLoginInfoData(
    @JsonProperty("user_id")
    val userID: Long,

    @JsonProperty("nickname")
    val nickname: String,
)

data class GetStrangerInfoData(
    @JsonProperty("user_id")
    val userID: Long,

    @JsonProperty("no_cache")
    val noCache: Boolean = false
)

data class FriendListElement(
    @JsonProperty("user_id")
    val userID: Long,

    @JsonProperty("nickname")
    val nickname: String,

    @JsonProperty("remark")
    val remark: String? = null
)

data class GetGroupInfoData(
    @JsonProperty("group_id")
    val groupID: Long,

    @JsonProperty("no_cache")
    val noCache: Boolean = false
)

data class GetGroupInfoResponseData(
    @JsonProperty("group_id")
    val groupID: Long,

    @JsonProperty("group_name")
    val groupName: String,

    @JsonProperty("member_count")
    val memberCount: Int,

    @JsonProperty("max_member_count")
    val maxMemberCount: Int,
)

data class GetGroupMemberInfoData(
    @JsonProperty("group_id")
    val groupID: Long,

    @JsonProperty("user_id")
    val userID: Long,

    @JsonProperty("no_cache")
    val noCache: Boolean = false
)

data class GroupMemberData(
    @JsonProperty("group_id")
    val groupID: Long,

    @JsonProperty("user_id")
    val userID: Long,

    @JsonProperty("nickname")
    val nickname: String,

    @JsonProperty("card")
    val card: String?,

    @JsonProperty("sex")
    val sex: String?,

    @JsonProperty("age")
    val age: Int?,

    @JsonProperty("area")
    val area: String?,

    @JsonProperty("join_time")
    val joinTime: Int,

    @JsonProperty("last_sent_time")
    val lastSentTime: Int,

    @JsonProperty("level")
    val level: String,

    @JsonProperty("role")
    val role: String,

    @JsonProperty("unfriendly")
    val unfriendly: Boolean,

    @JsonProperty("title")
    val title: String?,

    @JsonProperty("title_expire_time")
    val titleExpireTime: Int?,

    @JsonProperty("card_changeable")
    val cardChangeable: Boolean
)

data class GroupIDData(
    @JsonProperty("group_id")
    val groupID: Long
)

data class GetGroupHonorInfoData(
    @JsonProperty("group_id")
    val groupID: Long,

    // "talkative", "performer", "legend", "strong_newbie", "emotion" or "all"
    @JsonProperty("type")
    val type: String,
)

data class GetGroupHonorInfoResponseData(
    @JsonProperty("group_id")
    val groupID: Long,

    @JsonProperty("current_talkative")
    val currentTalkative: GroupCurrentTalkativeData?,

    @JsonProperty("talkative_list")
    val talkativeList: List<GroupHonorOwner>,

    @JsonProperty("performer_list")
    val performerList: List<GroupHonorOwner>,

    @JsonProperty("legend_list")
    val legendList: List<GroupHonorOwner>,

    @JsonProperty("strong_newbie_list")
    val strongNewbieList: List<GroupHonorOwner>,

    @JsonProperty("emotion_list")
    val emotionList: List<GroupHonorOwner>,
)

data class GroupCurrentTalkativeData(
    @JsonProperty("user_id")
    val userID: Long,

    @JsonProperty("nickname")
    val nickname: String,

    @JsonProperty("avatar")
    val avatar: String,

    @JsonProperty("day_count")
    val dayCount: Int,
)

data class GroupHonorOwner(
    @JsonProperty("user_id")
    val userID: Long,

    @JsonProperty("honor")
    val honor: String,

    @JsonProperty("avatar")
    val avatar: Int,

    @JsonProperty("description")
    val description: String,
)

data class DomainData(
    @JsonProperty("domain")
    val domain: String
)

data class CookiesData(
    @JsonProperty("cookies")
    val cookies: String
)

data class GetCSRFTokenData(
    @JsonProperty("token")
    val token: String
)

data class GetCredentialsData(
    @JsonProperty("cookies")
    val cookies: String,

    @JsonProperty("token")
    val token: String
)

data class GetRecordData(
    @JsonProperty("file")
    val file: String,

    // "mp3", "amr", "wma", "m4a", "spx", "ogg", "wav" or "flac"
    @JsonProperty("out_format")
    val outFormat: String,
)

data class FileData(
    @JsonProperty("file")
    val file: String
)

data class YesOrNoData(
    @JsonProperty("yes")
    val yes: Boolean
)

data class DelayData(
    @JsonProperty("delay")
    val delay: Int
)

data class HandleQuickOperationData(
    @JsonProperty("context")
    val context: Any,

    @JsonProperty("operation")
    val operation: Any,
)