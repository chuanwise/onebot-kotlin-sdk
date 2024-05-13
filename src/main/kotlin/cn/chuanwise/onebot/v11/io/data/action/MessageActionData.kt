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

import cn.chuanwise.onebot.v11.io.data.AGE
import cn.chuanwise.onebot.v11.io.data.ANONYMOUS
import cn.chuanwise.onebot.v11.io.data.APPROVE
import cn.chuanwise.onebot.v11.io.data.AREA
import cn.chuanwise.onebot.v11.io.data.AUTO_ESCAPE
import cn.chuanwise.onebot.v11.io.data.AVATAR
import cn.chuanwise.onebot.v11.io.data.CARD
import cn.chuanwise.onebot.v11.io.data.CARD_CHANGEABLE
import cn.chuanwise.onebot.v11.io.data.CONTEXT
import cn.chuanwise.onebot.v11.io.data.COOKIES
import cn.chuanwise.onebot.v11.io.data.CURRENT_TALKATIVE
import cn.chuanwise.onebot.v11.io.data.DAY_COUNT
import cn.chuanwise.onebot.v11.io.data.DELAY
import cn.chuanwise.onebot.v11.io.data.DESCRIPTION
import cn.chuanwise.onebot.v11.io.data.DOMAIN
import cn.chuanwise.onebot.v11.io.data.DURATION
import cn.chuanwise.onebot.v11.io.data.EMOTION_LIST
import cn.chuanwise.onebot.v11.io.data.ENABLE
import cn.chuanwise.onebot.v11.io.data.FILE
import cn.chuanwise.onebot.v11.io.data.FLAG
import cn.chuanwise.onebot.v11.io.data.GROUP_ID
import cn.chuanwise.onebot.v11.io.data.GROUP_NAME
import cn.chuanwise.onebot.v11.io.data.HONOR
import cn.chuanwise.onebot.v11.io.data.ID
import cn.chuanwise.onebot.v11.io.data.IS_DISMISS
import cn.chuanwise.onebot.v11.io.data.JOIN_TIME
import cn.chuanwise.onebot.v11.io.data.LAST_SENT_TIME
import cn.chuanwise.onebot.v11.io.data.LEGEND_LIST
import cn.chuanwise.onebot.v11.io.data.LEVEL
import cn.chuanwise.onebot.v11.io.data.MAX_MEMBER_COUNT
import cn.chuanwise.onebot.v11.io.data.MEMBER_COUNT
import cn.chuanwise.onebot.v11.io.data.MESSAGE
import cn.chuanwise.onebot.v11.io.data.MESSAGE_ID
import cn.chuanwise.onebot.v11.io.data.MESSAGE_TYPE
import cn.chuanwise.onebot.v11.io.data.NICKNAME
import cn.chuanwise.onebot.v11.io.data.NO_CACHE
import cn.chuanwise.onebot.v11.io.data.OPERATION
import cn.chuanwise.onebot.v11.io.data.OUT_FORMAT
import cn.chuanwise.onebot.v11.io.data.PERFORMER_LIST
import cn.chuanwise.onebot.v11.io.data.REAL_ID
import cn.chuanwise.onebot.v11.io.data.REASON
import cn.chuanwise.onebot.v11.io.data.REJECT_ADD_REQUEST
import cn.chuanwise.onebot.v11.io.data.REMARK
import cn.chuanwise.onebot.v11.io.data.ROLE
import cn.chuanwise.onebot.v11.io.data.SENDER
import cn.chuanwise.onebot.v11.io.data.SEX
import cn.chuanwise.onebot.v11.io.data.SPECIAL_TITLE
import cn.chuanwise.onebot.v11.io.data.STRONG_NEWBIE_LIST
import cn.chuanwise.onebot.v11.io.data.SUB_TYPE
import cn.chuanwise.onebot.v11.io.data.TALKATIVE_LIST
import cn.chuanwise.onebot.v11.io.data.TIME
import cn.chuanwise.onebot.v11.io.data.TIMES
import cn.chuanwise.onebot.v11.io.data.TITLE
import cn.chuanwise.onebot.v11.io.data.TITLE_EXPIRE_TIME
import cn.chuanwise.onebot.v11.io.data.TOKEN
import cn.chuanwise.onebot.v11.io.data.TYPE
import cn.chuanwise.onebot.v11.io.data.UNFRIENDLY
import cn.chuanwise.onebot.v11.io.data.USER_ID
import cn.chuanwise.onebot.v11.io.data.YES
import cn.chuanwise.onebot.v11.io.data.event.AnonymousSenderData
import cn.chuanwise.onebot.v11.io.data.event.SenderData
import cn.chuanwise.onebot.v11.io.data.message.MessageData
import com.fasterxml.jackson.annotation.JsonProperty

data class MessageIDData(
    @JsonProperty(MESSAGE_ID)
    val messageID: Int
)

data class IDData(
    @JsonProperty(ID)
    val id: String
)

data class SendGroupMessageData(
    @JsonProperty(GROUP_ID)
    val groupID: Long,

    @JsonProperty(MESSAGE)
    val message: MessageData,

    @JsonProperty(AUTO_ESCAPE)
    val autoEscape: Boolean = false,
)

data class SendPrivateMessageData(
    @JsonProperty(USER_ID)
    val userID: Long,

    @JsonProperty(MESSAGE)
    val message: MessageData,

    @JsonProperty(AUTO_ESCAPE)
    val autoEscape: Boolean = false,
)

data class SendMessageData(
    // "private" or "group"
    @JsonProperty(MESSAGE_TYPE)
    val messageType: String?,

    // if not null, send to this user
    @JsonProperty(USER_ID)
    val userID: Long?,

    // if not null, send to this group
    @JsonProperty(GROUP_ID)
    val groupID: Long?,

    @JsonProperty(MESSAGE)
    val message: MessageData,

    @JsonProperty(AUTO_ESCAPE)
    val autoEscape: Boolean = false,
)

data class GetMessageData(
    @JsonProperty(TIME)
    val time: Int,

    // "private" or "group"
    @JsonProperty(MESSAGE_TYPE)
    val messageType: String?,

    @JsonProperty(MESSAGE_ID)
    val messageID: Int,

    @JsonProperty(REAL_ID)
    val realID: Int,

    @JsonProperty(SENDER)
    val sender: SenderData,

    @JsonProperty(MESSAGE)
    val message: MessageData,
)

data class MessageDataWrapper(
    @JsonProperty(MESSAGE)
    val message: MessageData,
)

data class SendLikeData(
    @JsonProperty(USER_ID)
    val userID: Long,

    @JsonProperty(TIMES)
    val times: Int,
)

data class SetGroupKickData(
    @JsonProperty(GROUP_ID)
    val groupID: Long,

    @JsonProperty(USER_ID)
    val userID: Long,

    @JsonProperty(REJECT_ADD_REQUEST)
    val rejectAddRequest: Boolean = false,
)

data class SetGroupBanData(
    @JsonProperty(GROUP_ID)
    val groupID: Long,

    @JsonProperty(USER_ID)
    val userID: Long,

    // seconds, 0 to release
    @JsonProperty(DURATION)
    val duration: Long = 30 * 60
)

data class SetGroupAnonymousBanData(
    @JsonProperty(GROUP_ID)
    val groupID: Long,

    @JsonProperty(ANONYMOUS)
    val anonymous: AnonymousSenderData?,

    @JsonProperty(FLAG)
    val flag: String?,

    // seconds, 0 to release
    @JsonProperty(DURATION)
    val duration: Long = 30 * 60
)

data class GroupIDEnableData(
    @JsonProperty(GROUP_ID)
    val groupID: Long,

    @JsonProperty(ENABLE)
    val enable: Boolean = true
)

data class GroupIDUserIDEnableData(
    @JsonProperty(GROUP_ID)
    val groupID: Long,

    @JsonProperty(USER_ID)
    val userID: Long,

    @JsonProperty(ENABLE)
    val enable: Boolean = true
)

data class SetGroupCardData(
    @JsonProperty(GROUP_ID)
    val groupID: Long,

    @JsonProperty(USER_ID)
    val userID: Long,

    @JsonProperty(CARD)
    val card: String? = null
)

data class SetGroupNameData(
    @JsonProperty(GROUP_ID)
    val groupID: Long,

    @JsonProperty(GROUP_NAME)
    val groupName: String
)

data class SetGroupLeaveData(
    @JsonProperty(GROUP_ID)
    val groupID: Long,

    @JsonProperty(IS_DISMISS)
    val isDismiss: Boolean = false
)

data class SetGroupSpecialTitleData(
    @JsonProperty(GROUP_ID)
    val groupID: Long,

    @JsonProperty(USER_ID)
    val userID: Long,

    @JsonProperty(SPECIAL_TITLE)
    val specialTitle: String? = null,

    // seconds, 0 to release
    @JsonProperty(DURATION)
    val duration: Long = -1
)

data class SetFriendAddRequestData(
    @JsonProperty(FLAG)
    val flag: String,

    @JsonProperty(APPROVE)
    val approve: Boolean = true,

    @JsonProperty(REMARK)
    val remark: String? = null
)

data class SetGroupAddRequestData(
    @JsonProperty(FLAG)
    val flag: String,

    @JsonProperty(SUB_TYPE)
    val subType: String,

    @JsonProperty(APPROVE)
    val approve: Boolean = true,

    @JsonProperty(REASON)
    val reason: String? = null
)

data class GetLoginInfoData(
    @JsonProperty(USER_ID)
    val userID: Long,

    @JsonProperty(NICKNAME)
    val nickname: String,
)

data class GetStrangerInfoData(
    @JsonProperty(USER_ID)
    val userID: Long,

    @JsonProperty(NO_CACHE)
    val noCache: Boolean = false
)

data class FriendListElement(
    @JsonProperty(USER_ID)
    val userID: Long,

    @JsonProperty(NICKNAME)
    val nickname: String,

    @JsonProperty(REMARK)
    val remark: String? = null
)

data class GetGroupInfoData(
    @JsonProperty(GROUP_ID)
    val groupID: Long,

    @JsonProperty(NO_CACHE)
    val noCache: Boolean = false
)

data class GetGroupInfoResponseData(
    @JsonProperty(GROUP_ID)
    val groupID: Long,

    @JsonProperty(GROUP_NAME)
    val groupName: String,

    @JsonProperty(MEMBER_COUNT)
    val memberCount: Int,

    @JsonProperty(MAX_MEMBER_COUNT)
    val maxMemberCount: Int,
)

data class GetGroupMemberInfoData(
    @JsonProperty(GROUP_ID)
    val groupID: Long,

    @JsonProperty(USER_ID)
    val userID: Long,

    @JsonProperty(NO_CACHE)
    val noCache: Boolean = false
)

data class GroupMemberData(
    @JsonProperty(GROUP_ID)
    val groupID: Long,

    @JsonProperty(USER_ID)
    val userID: Long,

    @JsonProperty(NICKNAME)
    val nickname: String,

    @JsonProperty(CARD)
    val card: String?,

    @JsonProperty(SEX)
    val sex: String?,

    @JsonProperty(AGE)
    val age: Int?,

    @JsonProperty(AREA)
    val area: String?,

    @JsonProperty(JOIN_TIME)
    val joinTime: Int,

    @JsonProperty(LAST_SENT_TIME)
    val lastSentTime: Int,

    @JsonProperty(LEVEL)
    val level: String,

    @JsonProperty(ROLE)
    val role: String,

    @JsonProperty(UNFRIENDLY)
    val unfriendly: Boolean,

    @JsonProperty(TITLE)
    val title: String?,

    @JsonProperty(TITLE_EXPIRE_TIME)
    val titleExpireTime: Int?,

    @JsonProperty(CARD_CHANGEABLE)
    val cardChangeable: Boolean
)

data class GroupIDData(
    @JsonProperty(GROUP_ID)
    val groupID: Long
)

data class GetGroupHonorInfoData(
    @JsonProperty(GROUP_ID)
    val groupID: Long,

    // "talkative", "performer", "legend", "strong_newbie", "emotion" or "all"
    @JsonProperty(TYPE)
    val type: String,
)

data class GetGroupHonorInfoResponseData(
    @JsonProperty(GROUP_ID)
    val groupID: Long,

    @JsonProperty(CURRENT_TALKATIVE)
    val currentTalkative: GroupCurrentTalkativeData?,

    @JsonProperty(TALKATIVE_LIST)
    val talkativeList: List<GroupHonorOwner>,

    @JsonProperty(PERFORMER_LIST)
    val performerList: List<GroupHonorOwner>,

    @JsonProperty(LEGEND_LIST)
    val legendList: List<GroupHonorOwner>,

    @JsonProperty(STRONG_NEWBIE_LIST)
    val strongNewbieList: List<GroupHonorOwner>,

    @JsonProperty(EMOTION_LIST)
    val emotionList: List<GroupHonorOwner>,
)

data class GroupCurrentTalkativeData(
    @JsonProperty(USER_ID)
    val userID: Long,

    @JsonProperty(NICKNAME)
    val nickname: String,

    @JsonProperty(AVATAR)
    val avatar: String,

    @JsonProperty(DAY_COUNT)
    val dayCount: Int,
)

data class GroupHonorOwner(
    @JsonProperty(USER_ID)
    val userID: Long,

    @JsonProperty(HONOR)
    val honor: String,

    @JsonProperty(AVATAR)
    val avatar: Int,

    @JsonProperty(DESCRIPTION)
    val description: String,
)

data class DomainData(
    @JsonProperty(DOMAIN)
    val domain: String
)

data class CookiesData(
    @JsonProperty(COOKIES)
    val cookies: String
)

data class GetCSRFTokenData(
    @JsonProperty(TOKEN)
    val token: String
)

data class GetCredentialsData(
    @JsonProperty(COOKIES)
    val cookies: String,

    @JsonProperty(TOKEN)
    val token: String
)

data class GetRecordData(
    @JsonProperty(FILE)
    val file: String,

    // "mp3", "amr", "wma", "m4a", "spx", "ogg", "wav" or "flac"
    @JsonProperty(OUT_FORMAT)
    val outFormat: String,
)

data class FileData(
    @JsonProperty(FILE)
    val file: String
)

data class YesOrNoData(
    @JsonProperty(YES)
    val yes: Boolean
)

data class DelayData(
    @JsonProperty(DELAY)
    val delay: Int
)

data class HandleQuickOperationData(
    @JsonProperty(CONTEXT)
    val context: Any,

    @JsonProperty(OPERATION)
    val operation: Any,
)