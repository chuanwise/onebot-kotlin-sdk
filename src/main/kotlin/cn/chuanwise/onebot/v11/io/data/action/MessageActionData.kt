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
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class MessageIDData(
    val messageID: Int
)

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class IDData(
    val id: String
)

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class SendGroupMessageData(
    val groupID: Long,
    val message: MessageData,
    val autoEscape: Boolean = false,
)

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class SendPrivateMessageData(
    val userID: Long,
    val message: MessageData,
    val autoEscape: Boolean = false,
)

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class SendMessageData(
    // "private" or "group"
    val messageType: String?,

    // if not null, send to this user
    val userID: Long?,

    // if not null, send to this group
    val groupID: Long?,
    val message: MessageData,
    val autoEscape: Boolean = false,
)

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class GetMessageData(
    val time: Int,

    // "private" or "group"
    val messageType: String?,
    val messageID: Int,
    val realID: Int,
    val sender: SenderData,
    val message: MessageData,
)

data class MessageDataWrapper(
    val message: MessageData,
)

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class SendLikeData(
    val userID: Long,
    val times: Int,
)

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class SetGroupKickData(
    val groupID: Long,
    val userID: Long,
    val rejectAddRequest: Boolean = false,
)

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class SetGroupBanData(
    val groupID: Long,
    val userID: Long,

    // seconds, 0 to release
    val duration: Long = 30 * 60
)

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class SetGroupAnonymousBanData(
    val groupID: Long,
    val anonymous: AnonymousSenderData?,
    val flag: String?,

    // seconds, 0 to release
    val duration: Long = 30 * 60
)

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class GroupIDEnableData(
    val groupID: Long,
    val enable: Boolean = true
)

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class GroupIDUserIDEnableData(
    val groupID: Long,
    val userID: Long,
    val enable: Boolean = true
)

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class SetGroupCardData(
    val groupID: Long,
    val userID: Long,
    val card: String? = null
)

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class SetGroupNameData(
    val groupID: Long,
    val groupName: String
)

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class SetGroupLeaveData(
    val groupID: Long,
    val isDismiss: Boolean = false
)

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class SetGroupSpecialTitleData(
    val groupID: Long,
    val userID: Long,
    val specialTitle: String? = null,

    // seconds, 0 to release
    val duration: Long = -1
)

data class SetFriendAddRequestData(
    val flag: String,
    val approve: Boolean = true,
    val remark: String? = null
)

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class SetGroupAddRequestData(
    val flag: String,
    val subType: String,
    val approve: Boolean = true,
    val reason: String? = null
)

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class GetLoginInfoData(
    val userID: Long,
    val nickname: String,
)

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class GetStrangerInfoData(
    val userID: Long,
    val noCache: Boolean = false
)

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class FriendListElement(
    val userID: Long,
    val nickname: String,
    val remark: String? = null
)

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class GetGroupInfoData(
    val groupID: Long,
    val noCache: Boolean = false
)

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class GetGroupInfoResponseData(
    val groupID: Long,
    val groupName: String,
    val memberCount: Int,
    val maxMemberCount: Int,
)

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class GetGroupMemberInfoData(
    val groupID: Long,
    val userID: Long,
    val noCache: Boolean = false
)

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class GroupMemberData(
    val groupID: Long,
    val userID: Long,
    val nickname: String,
    val card: String?,
    val sex: String?,
    val age: Int?,
    val area: String?,
    val joinTime: Int,
    val lastSentTime: Int,
    val level: String,
    val role: String,
    val unfriendly: Boolean,
    val title: String?,
    val titleExpireTime: Int?,
    val cardChangeable: Boolean
)

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class GroupIDData(
    val groupID: Long
)

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class GetGroupHonorInfoData(
    val groupID: Long,

    // "talkative", "performer", "legend", "strong_newbie", "emotion" or "all"
    val type: String,
)

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class GetGroupHonorInfoResponseData(
    val groupID: Long,
    val currentTalkative: GroupCurrentTalkativeData?,
    val talkativeList: List<GroupHonorOwner>,
    val performerList: List<GroupHonorOwner>,
    val legendList: List<GroupHonorOwner>,
    val strongNewbieList: List<GroupHonorOwner>,
    val emotionList: List<GroupHonorOwner>,
)

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class GroupCurrentTalkativeData(
    val userID: Long,
    val nickname: String,
    val avatar: String,
    val dayCount: Int,
)

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class GroupHonorOwner(
    val userID: Long,
    val honor: String,
    val avatar: Int,
    val description: String,
)

data class DomainData(
    val domain: String
)

data class CookiesData(
    val cookies: String
)

data class GetCSRFTokenData(
    val token: String
)

data class GetCredentialsData(
    val cookies: String,
    val token: String
)

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class GetRecordData(
    val file: String,

    // "mp3", "amr", "wma", "m4a", "spx", "ogg", "wav" or "flac"
    val outFormat: String,
)

data class FileData(
    val file: String
)

data class YesOrNoData(
    val yes: Boolean
)

data class DelayData(
    val delay: Int
)

data class HandleQuickOperationData(
    val context: Any,
    val operation: Any,
)