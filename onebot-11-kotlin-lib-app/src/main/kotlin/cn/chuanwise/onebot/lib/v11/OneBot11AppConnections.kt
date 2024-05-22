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

@file:JvmName("OneBot11AppConnections")

package cn.chuanwise.onebot.lib.v11

import cn.chuanwise.onebot.lib.AppConnection
import cn.chuanwise.onebot.lib.request
import cn.chuanwise.onebot.lib.v11.data.action.DelayData
import cn.chuanwise.onebot.lib.v11.data.action.DomainData
import cn.chuanwise.onebot.lib.v11.data.action.FileData
import cn.chuanwise.onebot.lib.v11.data.action.GetGroupHonorInfoData
import cn.chuanwise.onebot.lib.v11.data.action.GetGroupInfoData
import cn.chuanwise.onebot.lib.v11.data.action.GetRecordData
import cn.chuanwise.onebot.lib.v11.data.action.GetStrangerInfoData
import cn.chuanwise.onebot.lib.v11.data.action.GroupIDData
import cn.chuanwise.onebot.lib.v11.data.action.GroupIDEnableData
import cn.chuanwise.onebot.lib.v11.data.action.GroupIDUserIDEnableData
import cn.chuanwise.onebot.lib.v11.data.action.IDData
import cn.chuanwise.onebot.lib.v11.data.action.MessageIDData
import cn.chuanwise.onebot.lib.v11.data.action.SendGroupMessageData
import cn.chuanwise.onebot.lib.v11.data.action.SendLikeData
import cn.chuanwise.onebot.lib.v11.data.action.SendMessageData
import cn.chuanwise.onebot.lib.v11.data.action.SendPrivateMessageData
import cn.chuanwise.onebot.lib.v11.data.action.SetFriendAddRequestData
import cn.chuanwise.onebot.lib.v11.data.action.SetGroupAddRequestData
import cn.chuanwise.onebot.lib.v11.data.action.SetGroupAnonymousBanData
import cn.chuanwise.onebot.lib.v11.data.action.SetGroupBanData
import cn.chuanwise.onebot.lib.v11.data.action.SetGroupCardData
import cn.chuanwise.onebot.lib.v11.data.action.SetGroupKickData
import cn.chuanwise.onebot.lib.v11.data.action.SetGroupLeaveData
import cn.chuanwise.onebot.lib.v11.data.action.SetGroupNameData
import cn.chuanwise.onebot.lib.v11.data.action.SetGroupSpecialTitleData
import cn.chuanwise.onebot.lib.v11.data.event.AnonymousSenderData
import cn.chuanwise.onebot.lib.v11.data.message.CQCodeMessageData
import cn.chuanwise.onebot.lib.v11.data.message.MessageData

/**
 * @see [SEND_PRIVATE_MESSAGE]
 */
suspend fun AppConnection.sendPrivateMessage(userID: Long, message: MessageData): Int = request(
    SEND_PRIVATE_MESSAGE, SendPrivateMessageData(
        userID = userID,
        message = message,
        autoEscape = (message as? CQCodeMessageData)?.autoEscape ?: false
    )
).messageID

/**
 * @see [SEND_GROUP_MESSAGE]
 */
suspend fun AppConnection.sendGroupMessage(groupID: Long, message: MessageData): Int = request(
    SEND_GROUP_MESSAGE, SendGroupMessageData(
        groupID = groupID,
        message = message,
        autoEscape = (message as? CQCodeMessageData)?.autoEscape ?: false
    )
).messageID

/**
 * @see [SEND_MESSAGE]
 */
suspend fun AppConnection.sendMessage(messageType: String, userID: Long?, groupID: Long?, message: MessageData): Int =
    request(
        SEND_MESSAGE, SendMessageData(
            messageType = messageType,
            userID = userID,
            groupID = groupID,
            message = message,
            autoEscape = (message as? CQCodeMessageData)?.autoEscape ?: false
        )
    ).messageID

/**
 * @see [DELETE_MESSAGE]
 */
suspend fun AppConnection.deleteMessage(messageID: Int) = request(
    DELETE_MESSAGE, MessageIDData(messageID)
)

/**
 * @see [GET_MESSAGE]
 */
suspend fun AppConnection.getMessage(messageID: Int) = request(
    GET_MESSAGE, MessageIDData(messageID)
)

/**
 * @see [GET_FORWARD_MESSAGE]
 */
suspend fun AppConnection.getForwardMessage(id: String) = request(
    GET_FORWARD_MESSAGE, IDData(id)
).message

/**
 * @see [SEND_LIKE]
 */
suspend fun AppConnection.sendLike(userID: Long, times: Int) = request(
    SEND_LIKE, SendLikeData(
        userID = userID,
        times = times
    )
)

/**
 * @see [SET_GROUP_KICK]
 */
suspend fun AppConnection.setGroupKick(groupID: Long, userID: Long, rejectAddsend: Boolean) = request(
    SET_GROUP_KICK, SetGroupKickData(
        groupID = groupID,
        userID = userID,
        rejectAddRequest = rejectAddsend
    )
)

/**
 * @see [SET_GROUP_BAN]
 */
suspend fun AppConnection.setGroupBan(groupID: Long, userID: Long, duration: Long) = request(
    SET_GROUP_BAN, SetGroupBanData(
        groupID = groupID,
        userID = userID,
        duration = duration
    )
)

/**
 * @see [SET_GROUP_ANONYMOUS_BAN]
 */
suspend fun AppConnection.setGroupAnonymousBan(groupID: Long, flag: String, duration: Long) = request(
    SET_GROUP_ANONYMOUS_BAN, SetGroupAnonymousBanData(
        groupID = groupID,
        anonymous = null,
        flag = flag,
        duration = duration
    )
)

/**
 * @see [SET_GROUP_ANONYMOUS_BAN]
 */
suspend fun AppConnection.setGroupAnonymousBan(groupID: Long, sender: AnonymousSenderData, duration: Long) = request(
    SET_GROUP_ANONYMOUS_BAN, SetGroupAnonymousBanData(
        groupID = groupID,
        anonymous = sender,
        flag = null,
        duration = duration
    )
)

/**
 * @see [SET_GROUP_WHOLE_BAN]
 */
suspend fun AppConnection.setGroupWholeBan(groupID: Long, enable: Boolean) = request(
    SET_GROUP_WHOLE_BAN, GroupIDEnableData(
        groupID = groupID,
        enable = enable
    )
)

/**
 * @see [SET_GROUP_ANONYMOUS]
 */
suspend fun AppConnection.setGroupAdmin(groupID: Long, userID: Long, enable: Boolean) = request(
    SET_GROUP_ADMIN, GroupIDUserIDEnableData(
        groupID = groupID,
        userID = userID,
        enable = enable
    )
)

/**
 * @see [SET_GROUP_ANONYMOUS]
 */
suspend fun AppConnection.setGroupAnonymous(groupID: Long, enable: Boolean) = request(
    SET_GROUP_ANONYMOUS, GroupIDEnableData(
        groupID = groupID,
        enable = enable
    )
)

/**
 * @see [SET_GROUP_CARD]
 */
suspend fun AppConnection.setGroupCard(groupID: Long, userID: Long, card: String) = request(
    SET_GROUP_CARD, SetGroupCardData(
        groupID = groupID,
        userID = userID,
        card = card
    )
)

/**
 * @see [SET_GROUP_NAME]
 */
suspend fun AppConnection.setGroupName(groupID: Long, groupName: String) = request(
    SET_GROUP_NAME, SetGroupNameData(
        groupID = groupID,
        groupName = groupName
    )
)

/**
 * @see [SET_GROUP_LEAVE]
 */
suspend fun AppConnection.setGroupLeave(groupID: Long, isDismiss: Boolean) = request(
    SET_GROUP_LEAVE, SetGroupLeaveData(
        groupID = groupID,
        isDismiss = isDismiss
    )
)

/**
 * @see [SET_GROUP_SPECIAL_TITLE]
 */
suspend fun AppConnection.setGroupSpecialTitle(groupID: Long, userID: Long, specialTitle: String, duration: Long) =
    request(
        SET_GROUP_SPECIAL_TITLE, SetGroupSpecialTitleData(
            groupID = groupID,
            userID = userID,
            specialTitle = specialTitle,
            duration = duration
        )
    )

/**
 * @see [SET_FRIEND_ADD_send]
 */
suspend fun AppConnection.setFriendAddRequest(flag: String, approve: Boolean, remark: String) = request(
    SET_FRIEND_ADD_REQUEST, SetFriendAddRequestData(
        flag = flag,
        approve = approve,
        remark = remark
    )
)

/**
 * @see [SET_GROUP_ADD_send]
 */
suspend fun AppConnection.setGroupAddRequest(flag: String, subType: String, approve: Boolean, reason: String) = request(
    SET_GROUP_ADD_REQUEST, SetGroupAddRequestData(
        flag = flag,
        approve = approve,
        subType = subType,
        reason = reason
    )
)

/**
 * @see [GET_LOGIN_INFO]
 */
suspend fun AppConnection.getLoginInfo() = request(
    GET_LOGIN_INFO, Unit
)

/**
 * @see [GET_STRANGER_INFO]
 */
suspend fun AppConnection.getStrangerInfo(userID: Long, noCache: Boolean) = request(
    GET_STRANGER_INFO, GetStrangerInfoData(
        userID = userID,
        noCache = noCache
    )
)

/**
 * @see [GET_FRIEND_LIST]
 */
suspend fun AppConnection.getGroupInfo(groupID: Long, noCache: Boolean) = request(
    GET_GROUP_INFO, GetGroupInfoData(
        groupID = groupID,
        noCache = noCache
    )
)

/**
 * @see [GET_FRIEND_LIST]
 */
suspend fun AppConnection.getFriendList() = request(
    GET_FRIEND_LIST, Unit
)

/**
 * @see [GET_GROUP_LIST]
 */
suspend fun AppConnection.getGroupList() = request(
    GET_GROUP_LIST, Unit
)

/**
 * @see [GET_GROUP_MEMBER_INFO]
 */
suspend fun AppConnection.getGroupMemberList(groupID: Long) = request(
    GET_GROUP_MEMBER_LIST, GroupIDData(groupID)
)

/**
 * @see [GET_GROUP_HONOR_INFO]
 */
suspend fun AppConnection.getGroupHonorInfo(groupID: Long, type: String) = request(
    GET_GROUP_HONOR_INFO, GetGroupHonorInfoData(
        groupID = groupID,
        type = type
    )
)

/**
 * @see [GET_COOKIES]
 */
suspend fun AppConnection.getCookies(domain: String) = request(
    GET_COOKIES, DomainData(domain)
)

/**
 * @see [GET_CSRF_TOKEN]
 */
suspend fun AppConnection.getCSRFToken() = request(
    GET_CSRF_TOKEN, Unit
)

/**
 * @see [GET_CREDENTIALS]
 */
suspend fun AppConnection.getCredentials(domain: String) = request(
    GET_CREDENTIALS, DomainData(domain)
)

/**
 * @see [GET_RECORD]
 */
suspend fun AppConnection.getRecord(file: String, outFormat: String) = request(
    GET_RECORD, GetRecordData(
        file = file,
        outFormat = outFormat
    )
)

/**
 * @see [GET_IMAGE]
 */
suspend fun AppConnection.getImage(file: String) = request(
    GET_IMAGE, FileData(
        file = file
    )
)

/**
 * @see [CAN_SEND_IMAGE]
 */
suspend fun AppConnection.canSendImage() = request(
    CAN_SEND_IMAGE, Unit
)

/**
 * @see [CAN_SEND_RECORD]
 */
suspend fun AppConnection.canSendRecord() = request(
    CAN_SEND_RECORD, Unit
)

/**
 * @see [GET_STATUS]
 */
suspend fun AppConnection.getStatus() = request(
    GET_STATUS, Unit
)

/**
 * @see [GET_VERSION_INFO]
 */
suspend fun AppConnection.getVersionInfo() = request(
    GET_VERSION_INFO, Unit
)

/**
 * @see [SET_RESTART]
 */
suspend fun AppConnection.setRestart(delay: Int) = request(
    SET_RESTART, DelayData(delay)
)

/**
 * @see [CLEAN_CACHE]
 */
suspend fun AppConnection.cleanCache() = request(
    CLEAN_CACHE, Unit
)