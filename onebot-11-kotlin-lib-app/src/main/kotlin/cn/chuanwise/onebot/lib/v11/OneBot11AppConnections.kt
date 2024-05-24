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
suspend fun OneBot11AppConnection.sendPrivateMessage(userID: Long, message: MessageData): Int = call(
    SEND_PRIVATE_MESSAGE, SendPrivateMessageData(
        userID = userID,
        message = message,
        autoEscape = (message as? CQCodeMessageData)?.autoEscape ?: false
    )
).messageID

suspend fun OneBot11AppConnection.sendPrivateMessageAsync(userID: Long, message: MessageData) = callAsync(
    SEND_PRIVATE_MESSAGE, SendPrivateMessageData(
        userID = userID,
        message = message,
        autoEscape = (message as? CQCodeMessageData)?.autoEscape ?: false
    )
)

suspend fun OneBot11AppConnection.sendPrivateMessageRateLimited(userID: Long, message: MessageData) = callRateLimited(
    SEND_PRIVATE_MESSAGE, SendPrivateMessageData(
        userID = userID,
        message = message,
        autoEscape = (message as? CQCodeMessageData)?.autoEscape ?: false
    )
)

/**
 * @see [SEND_GROUP_MESSAGE]
 */
suspend fun OneBot11AppConnection.sendGroupMessage(groupID: Long, message: MessageData): Int = call(
    SEND_GROUP_MESSAGE, SendGroupMessageData(
        groupID = groupID,
        message = message,
        autoEscape = (message as? CQCodeMessageData)?.autoEscape ?: false
    )
).messageID

suspend fun OneBot11AppConnection.sendGroupMessageAsync(groupID: Long, message: MessageData) = callAsync(
    SEND_GROUP_MESSAGE, SendGroupMessageData(
        groupID = groupID,
        message = message,
        autoEscape = (message as? CQCodeMessageData)?.autoEscape ?: false
    )
)

suspend fun OneBot11AppConnection.sendGroupMessageRateLimited(groupID: Long, message: MessageData) = callRateLimited(
    SEND_GROUP_MESSAGE, SendGroupMessageData(
        groupID = groupID,
        message = message,
        autoEscape = (message as? CQCodeMessageData)?.autoEscape ?: false
    )
)

/**
 * @see [SEND_MESSAGE]
 */
suspend fun OneBot11AppConnection.sendMessage(
    messageType: String,
    userID: Long?,
    groupID: Long?,
    message: MessageData
): Int =
    call(
        SEND_MESSAGE, SendMessageData(
            messageType = messageType,
            userID = userID,
            groupID = groupID,
            message = message,
            autoEscape = (message as? CQCodeMessageData)?.autoEscape ?: false
        )
    ).messageID

suspend fun OneBot11AppConnection.sendMessageAsync(
    messageType: String,
    userID: Long?,
    groupID: Long?,
    message: MessageData
) = callAsync(
    SEND_MESSAGE, SendMessageData(
        messageType = messageType,
        userID = userID,
        groupID = groupID,
        message = message,
        autoEscape = (message as? CQCodeMessageData)?.autoEscape ?: false
    )
)

suspend fun OneBot11AppConnection.sendMessageRateLimited(
    messageType: String,
    userID: Long?,
    groupID: Long?,
    message: MessageData
) = callRateLimited(
    SEND_MESSAGE, SendMessageData(
        messageType = messageType,
        userID = userID,
        groupID = groupID,
        message = message,
        autoEscape = (message as? CQCodeMessageData)?.autoEscape ?: false
    )
)

/**
 * @see [DELETE_MESSAGE]
 */
suspend fun OneBot11AppConnection.deleteMessage(messageID: Int) = call(
    DELETE_MESSAGE, MessageIDData(messageID)
)

suspend fun OneBot11AppConnection.deleteMessageAsync(messageID: Int) = callAsync(
    DELETE_MESSAGE, MessageIDData(messageID)
)

suspend fun OneBot11AppConnection.deleteMessageRateLimited(messageID: Int) = callRateLimited(
    DELETE_MESSAGE, MessageIDData(messageID)
)

/**
 * @see [GET_MESSAGE]
 */
suspend fun OneBot11AppConnection.getMessage(messageID: Int) = call(
    GET_MESSAGE, MessageIDData(messageID)
)

/**
 * @see [GET_FORWARD_MESSAGE]
 */
suspend fun OneBot11AppConnection.getForwardMessage(id: String) = call(
    GET_FORWARD_MESSAGE, IDData(id)
).message

/**
 * @see [SEND_LIKE]
 */
suspend fun OneBot11AppConnection.sendLike(userID: Long, times: Int) = call(
    SEND_LIKE, SendLikeData(
        userID = userID,
        times = times
    )
)

suspend fun OneBot11AppConnection.sendLikeAsync(userID: Long, times: Int) = callAsync(
    SEND_LIKE, SendLikeData(
        userID = userID,
        times = times
    )
)

suspend fun OneBot11AppConnection.sendLikeRateLimited(userID: Long, times: Int) = callRateLimited(
    SEND_LIKE, SendLikeData(
        userID = userID,
        times = times
    )
)

/**
 * @see [SET_GROUP_KICK]
 */
suspend fun OneBot11AppConnection.setGroupKick(groupID: Long, userID: Long, rejectAddRequest: Boolean) = call(
    SET_GROUP_KICK, SetGroupKickData(
        groupID = groupID,
        userID = userID,
        rejectAddRequest = rejectAddRequest
    )
)

suspend fun OneBot11AppConnection.setGroupKickAsync(groupID: Long, userID: Long, rejectAddRequest: Boolean) = callAsync(
    SET_GROUP_KICK, SetGroupKickData(
        groupID = groupID,
        userID = userID,
        rejectAddRequest = rejectAddRequest
    )
)

suspend fun OneBot11AppConnection.setGroupKickRateLimited(groupID: Long, userID: Long, rejectAddRequest: Boolean) =
    callRateLimited(
        SET_GROUP_KICK, SetGroupKickData(
            groupID = groupID,
            userID = userID,
            rejectAddRequest = rejectAddRequest
        )
    )

/**
 * @see [SET_GROUP_BAN]
 */
suspend fun OneBot11AppConnection.setGroupBan(groupID: Long, userID: Long, duration: Long) = call(
    SET_GROUP_BAN, SetGroupBanData(
        groupID = groupID,
        userID = userID,
        duration = duration
    )
)

suspend fun OneBot11AppConnection.setGroupBanAsync(groupID: Long, userID: Long, duration: Long) = callAsync(
    SET_GROUP_BAN, SetGroupBanData(
        groupID = groupID,
        userID = userID,
        duration = duration
    )
)

suspend fun OneBot11AppConnection.setGroupBanRateLimited(groupID: Long, userID: Long, duration: Long) = callRateLimited(
    SET_GROUP_BAN, SetGroupBanData(
        groupID = groupID,
        userID = userID,
        duration = duration
    )
)

/**
 * @see [SET_GROUP_ANONYMOUS_BAN]
 */
suspend fun OneBot11AppConnection.setGroupAnonymousBan(groupID: Long, flag: String, duration: Long) = call(
    SET_GROUP_ANONYMOUS_BAN, SetGroupAnonymousBanData(
        groupID = groupID,
        anonymous = null,
        flag = flag,
        duration = duration
    )
)

suspend fun OneBot11AppConnection.setGroupAnonymousBanAsync(groupID: Long, flag: String, duration: Long) = callAsync(
    SET_GROUP_ANONYMOUS_BAN, SetGroupAnonymousBanData(
        groupID = groupID,
        anonymous = null,
        flag = flag,
        duration = duration
    )
)

suspend fun OneBot11AppConnection.setGroupAnonymousBanRateLimited(groupID: Long, flag: String, duration: Long) =
    callRateLimited(
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
suspend fun OneBot11AppConnection.setGroupAnonymousBan(groupID: Long, sender: AnonymousSenderData, duration: Long) =
    call(
    SET_GROUP_ANONYMOUS_BAN, SetGroupAnonymousBanData(
        groupID = groupID,
        anonymous = sender,
        flag = null,
        duration = duration
    )
)

suspend fun OneBot11AppConnection.setGroupAnonymousBanAsync(
    groupID: Long,
    sender: AnonymousSenderData,
    duration: Long
) = callAsync(
    SET_GROUP_ANONYMOUS_BAN, SetGroupAnonymousBanData(
        groupID = groupID,
        anonymous = sender,
        flag = null,
        duration = duration
    )
)

suspend fun OneBot11AppConnection.setGroupAnonymousBanRateLimited(
    groupID: Long,
    sender: AnonymousSenderData,
    duration: Long
) = callRateLimited(
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
suspend fun OneBot11AppConnection.setGroupWholeBan(groupID: Long, enable: Boolean) = call(
    SET_GROUP_WHOLE_BAN, GroupIDEnableData(
        groupID = groupID,
        enable = enable
    )
)

suspend fun OneBot11AppConnection.setGroupWholeBanAsync(groupID: Long, enable: Boolean) = callAsync(
    SET_GROUP_WHOLE_BAN, GroupIDEnableData(
        groupID = groupID,
        enable = enable
    )
)

suspend fun OneBot11AppConnection.setGroupWholeBanRateLimited(groupID: Long, enable: Boolean) = callRateLimited(
    SET_GROUP_WHOLE_BAN, GroupIDEnableData(
        groupID = groupID,
        enable = enable
    )
)

/**
 * @see [SET_GROUP_ANONYMOUS]
 */
suspend fun OneBot11AppConnection.setGroupAdmin(groupID: Long, userID: Long, enable: Boolean) = call(
    SET_GROUP_ADMIN, GroupIDUserIDEnableData(
        groupID = groupID,
        userID = userID,
        enable = enable
    )
)

suspend fun OneBot11AppConnection.setGroupAdminAsync(groupID: Long, userID: Long, enable: Boolean) = callAsync(
    SET_GROUP_ADMIN, GroupIDUserIDEnableData(
        groupID = groupID,
        userID = userID,
        enable = enable
    )
)

suspend fun OneBot11AppConnection.setGroupAdminRateLimited(groupID: Long, userID: Long, enable: Boolean) =
    callRateLimited(
        SET_GROUP_ADMIN, GroupIDUserIDEnableData(
            groupID = groupID,
            userID = userID,
            enable = enable
        )
    )

/**
 * @see [SET_GROUP_ANONYMOUS]
 */
suspend fun OneBot11AppConnection.setGroupAnonymous(groupID: Long, enable: Boolean) = call(
    SET_GROUP_ANONYMOUS, GroupIDEnableData(
        groupID = groupID,
        enable = enable
    )
)

suspend fun OneBot11AppConnection.setGroupAnonymousAsync(groupID: Long, enable: Boolean) = callAsync(
    SET_GROUP_ANONYMOUS, GroupIDEnableData(
        groupID = groupID,
        enable = enable
    )
)

suspend fun OneBot11AppConnection.setGroupAnonymousRateLimited(groupID: Long, enable: Boolean) = callRateLimited(
    SET_GROUP_ANONYMOUS, GroupIDEnableData(
        groupID = groupID,
        enable = enable
    )
)

/**
 * @see [SET_GROUP_CARD]
 */
suspend fun OneBot11AppConnection.setGroupCard(groupID: Long, userID: Long, card: String) = call(
    SET_GROUP_CARD, SetGroupCardData(
        groupID = groupID,
        userID = userID,
        card = card
    )
)

suspend fun OneBot11AppConnection.setGroupCardAsync(groupID: Long, userID: Long, card: String) = callAsync(
    SET_GROUP_CARD, SetGroupCardData(
        groupID = groupID,
        userID = userID,
        card = card
    )
)

suspend fun OneBot11AppConnection.setGroupCardRateLimited(groupID: Long, userID: Long, card: String) = callRateLimited(
    SET_GROUP_CARD, SetGroupCardData(
        groupID = groupID,
        userID = userID,
        card = card
    )
)

/**
 * @see [SET_GROUP_NAME]
 */
suspend fun OneBot11AppConnection.setGroupName(groupID: Long, groupName: String) = call(
    SET_GROUP_NAME, SetGroupNameData(
        groupID = groupID,
        groupName = groupName
    )
)

suspend fun OneBot11AppConnection.setGroupNameAsync(groupID: Long, groupName: String) = callAsync(
    SET_GROUP_NAME, SetGroupNameData(
        groupID = groupID,
        groupName = groupName
    )
)

suspend fun OneBot11AppConnection.setGroupNameRateLimited(groupID: Long, groupName: String) = callRateLimited(
    SET_GROUP_NAME, SetGroupNameData(
        groupID = groupID,
        groupName = groupName
    )
)

/**
 * @see [SET_GROUP_LEAVE]
 */
suspend fun OneBot11AppConnection.setGroupLeave(groupID: Long, isDismiss: Boolean) = call(
    SET_GROUP_LEAVE, SetGroupLeaveData(
        groupID = groupID,
        isDismiss = isDismiss
    )
)

suspend fun OneBot11AppConnection.setGroupLeaveAsync(groupID: Long, isDismiss: Boolean) = callAsync(
    SET_GROUP_LEAVE, SetGroupLeaveData(
        groupID = groupID,
        isDismiss = isDismiss
    )
)

suspend fun OneBot11AppConnection.setGroupLeaveRateLimited(groupID: Long, isDismiss: Boolean) = callRateLimited(
    SET_GROUP_LEAVE, SetGroupLeaveData(
        groupID = groupID,
        isDismiss = isDismiss
    )
)

/**
 * @see [SET_GROUP_SPECIAL_TITLE]
 */
suspend fun OneBot11AppConnection.setGroupSpecialTitle(
    groupID: Long,
    userID: Long,
    specialTitle: String,
    duration: Long
) =
    call(
        SET_GROUP_SPECIAL_TITLE, SetGroupSpecialTitleData(
            groupID = groupID,
            userID = userID,
            specialTitle = specialTitle,
            duration = duration
        )
    )

suspend fun OneBot11AppConnection.setGroupSpecialTitleAsync(
    groupID: Long,
    userID: Long,
    specialTitle: String,
    duration: Long
) = callAsync(
    SET_GROUP_SPECIAL_TITLE, SetGroupSpecialTitleData(
        groupID = groupID,
        userID = userID,
        specialTitle = specialTitle,
        duration = duration
    )
)

suspend fun OneBot11AppConnection.setGroupSpecialTitleRateLimited(
    groupID: Long,
    userID: Long,
    specialTitle: String,
    duration: Long
) = callRateLimited(
    SET_GROUP_SPECIAL_TITLE, SetGroupSpecialTitleData(
        groupID = groupID,
        userID = userID,
        specialTitle = specialTitle,
        duration = duration
    )
)

/**
 * @see [SET_FRIEND_ADD_REQUEST]
 */
suspend fun OneBot11AppConnection.setFriendAddRequest(flag: String, approve: Boolean, remark: String) = call(
    SET_FRIEND_ADD_REQUEST, SetFriendAddRequestData(
        flag = flag,
        approve = approve,
        remark = remark
    )
)

suspend fun OneBot11AppConnection.setFriendAddRequestAsync(flag: String, approve: Boolean, remark: String) = callAsync(
    SET_FRIEND_ADD_REQUEST, SetFriendAddRequestData(
        flag = flag,
        approve = approve,
        remark = remark
    )
)

suspend fun OneBot11AppConnection.setFriendAddRequestRateLimited(flag: String, approve: Boolean, remark: String) =
    callRateLimited(
        SET_FRIEND_ADD_REQUEST, SetFriendAddRequestData(
            flag = flag,
            approve = approve,
            remark = remark
        )
    )

/**
 * @see [SET_GROUP_ADD_REQUEST]
 */
suspend fun OneBot11AppConnection.setGroupAddRequest(flag: String, subType: String, approve: Boolean, reason: String) =
    call(
    SET_GROUP_ADD_REQUEST, SetGroupAddRequestData(
        flag = flag,
        approve = approve,
        subType = subType,
        reason = reason
    )
)

suspend fun OneBot11AppConnection.setGroupAddRequestAsync(
    flag: String,
    subType: String,
    approve: Boolean,
    reason: String
) = callAsync(
    SET_GROUP_ADD_REQUEST, SetGroupAddRequestData(
        flag = flag,
        approve = approve,
        subType = subType,
        reason = reason
    )
)

suspend fun OneBot11AppConnection.setGroupAddRequestRateLimited(
    flag: String,
    subType: String,
    approve: Boolean,
    reason: String
) = callRateLimited(
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
suspend fun OneBot11AppConnection.getLoginInfo() = call(
    GET_LOGIN_INFO, Unit
)

/**
 * @see [GET_STRANGER_INFO]
 */
suspend fun OneBot11AppConnection.getStrangerInfo(userID: Long, noCache: Boolean) = call(
    GET_STRANGER_INFO, GetStrangerInfoData(
        userID = userID,
        noCache = noCache
    )
)

/**
 * @see [GET_FRIEND_LIST]
 */
suspend fun OneBot11AppConnection.getGroupInfo(groupID: Long, noCache: Boolean) = call(
    GET_GROUP_INFO, GetGroupInfoData(
        groupID = groupID,
        noCache = noCache
    )
)

/**
 * @see [GET_FRIEND_LIST]
 */
suspend fun OneBot11AppConnection.getFriendList() = call(
    GET_FRIEND_LIST, Unit
)

/**
 * @see [GET_GROUP_LIST]
 */
suspend fun OneBot11AppConnection.getGroupList() = call(
    GET_GROUP_LIST, Unit
)

/**
 * @see [GET_GROUP_MEMBER_INFO]
 */
suspend fun OneBot11AppConnection.getGroupMemberList(groupID: Long) = call(
    GET_GROUP_MEMBER_LIST, GroupIDData(groupID)
)

/**
 * @see [GET_GROUP_HONOR_INFO]
 */
suspend fun OneBot11AppConnection.getGroupHonorInfo(groupID: Long, type: String) = call(
    GET_GROUP_HONOR_INFO, GetGroupHonorInfoData(
        groupID = groupID,
        type = type
    )
)

/**
 * @see [GET_COOKIES]
 */
suspend fun OneBot11AppConnection.getCookies(domain: String) = call(
    GET_COOKIES, DomainData(domain)
)

/**
 * @see [GET_CSRF_TOKEN]
 */
suspend fun OneBot11AppConnection.getCSRFToken() = call(
    GET_CSRF_TOKEN, Unit
)

/**
 * @see [GET_CREDENTIALS]
 */
suspend fun OneBot11AppConnection.getCredentials(domain: String) = call(
    GET_CREDENTIALS, DomainData(domain)
)

/**
 * @see [GET_RECORD]
 */
suspend fun OneBot11AppConnection.getRecord(file: String, outFormat: String) = call(
    GET_RECORD, GetRecordData(
        file = file,
        outFormat = outFormat
    )
)

/**
 * @see [GET_IMAGE]
 */
suspend fun OneBot11AppConnection.getImage(file: String) = call(
    GET_IMAGE, FileData(
        file = file
    )
)

/**
 * @see [CAN_SEND_IMAGE]
 */
suspend fun OneBot11AppConnection.canSendImage() = call(
    CAN_SEND_IMAGE, Unit
)

/**
 * @see [CAN_SEND_RECORD]
 */
suspend fun OneBot11AppConnection.canSendRecord() = call(
    CAN_SEND_RECORD, Unit
)

/**
 * @see [GET_STATUS]
 */
suspend fun OneBot11AppConnection.getStatus() = call(
    GET_STATUS, Unit
)

/**
 * @see [GET_VERSION_INFO]
 */
suspend fun OneBot11AppConnection.getVersionInfo() = call(
    GET_VERSION_INFO, Unit
)

/**
 * @see [SET_RESTART]
 */
suspend fun OneBot11AppConnection.setRestart(delay: Int) = call(
    SET_RESTART, DelayData(delay)
)

/**
 * @see [CLEAN_CACHE]
 */
suspend fun OneBot11AppConnection.cleanCache() = call(
    CLEAN_CACHE, Unit
)

suspend fun OneBot11AppConnection.cleanCacheAsync() = callAsync(
    CLEAN_CACHE, Unit
)