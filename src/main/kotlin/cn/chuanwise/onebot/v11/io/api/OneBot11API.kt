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

package cn.chuanwise.onebot.v11.io.api

import cn.chuanwise.onebot.io.data.Null
import cn.chuanwise.onebot.v11.io.OneBot11Action
import cn.chuanwise.onebot.v11.io.data.action.DelayData
import cn.chuanwise.onebot.v11.io.data.action.DomainData
import cn.chuanwise.onebot.v11.io.data.action.FileData
import cn.chuanwise.onebot.v11.io.data.action.GetGroupHonorInfoData
import cn.chuanwise.onebot.v11.io.data.action.GetGroupInfoData
import cn.chuanwise.onebot.v11.io.data.action.GetRecordData
import cn.chuanwise.onebot.v11.io.data.action.GetStrangerInfoData
import cn.chuanwise.onebot.v11.io.data.action.GroupIDData
import cn.chuanwise.onebot.v11.io.data.action.GroupIDEnableData
import cn.chuanwise.onebot.v11.io.data.action.GroupIDUserIDEnableData
import cn.chuanwise.onebot.v11.io.data.action.IDData
import cn.chuanwise.onebot.v11.io.data.action.MessageIDData
import cn.chuanwise.onebot.v11.io.data.action.SendGroupMessageData
import cn.chuanwise.onebot.v11.io.data.action.SendLikeData
import cn.chuanwise.onebot.v11.io.data.action.SendMessageData
import cn.chuanwise.onebot.v11.io.data.action.SendPrivateMessageData
import cn.chuanwise.onebot.v11.io.data.action.SetFriendAddRequestData
import cn.chuanwise.onebot.v11.io.data.action.SetGroupAddRequestData
import cn.chuanwise.onebot.v11.io.data.action.SetGroupAnonymousBanData
import cn.chuanwise.onebot.v11.io.data.action.SetGroupBanData
import cn.chuanwise.onebot.v11.io.data.action.SetGroupCardData
import cn.chuanwise.onebot.v11.io.data.action.SetGroupKickData
import cn.chuanwise.onebot.v11.io.data.action.SetGroupLeaveData
import cn.chuanwise.onebot.v11.io.data.action.SetGroupNameData
import cn.chuanwise.onebot.v11.io.data.action.SetGroupSpecialTitleData
import cn.chuanwise.onebot.v11.io.data.event.AnonymousSenderData
import cn.chuanwise.onebot.v11.io.data.message.CQCodeMessageData
import cn.chuanwise.onebot.v11.io.data.message.MessageData

/**
 * Defined the API of [OneBot 11](https://github.com/botuniverse/onebot-11/blob/master/api/public.md),
 * each one is corresponding to a method.
 *
 * @author Chuanwise
 */
@FunctionalInterface
interface OneBot11API : AutoCloseable {
    /**
     * Perform a request defined in [OneBot 11 Action](https://github.com/botuniverse/onebot-11/blob/master/api)
     * to the OneBot implementation.
     *
     * @param action The action to perform.
     * @param params The parameters of the action.
     */
    suspend fun <P, R> request(action: OneBot11Action<P, R>, params: P): R
}

/**
 * @see [OneBot11Action.SEND_PRIVATE_MESSAGE]
 */
suspend fun OneBot11API.sendPrivateMessage(userID: Long, message: MessageData): Int = request(
    OneBot11Action.SEND_PRIVATE_MESSAGE, SendPrivateMessageData(
        userID = userID,
        message = message,
        autoEscape = (message as? CQCodeMessageData)?.autoEscape ?: false
    )
).messageID

/**
 * @see [OneBot11Action.SEND_GROUP_MESSAGE]
 */
suspend fun OneBot11API.sendGroupMessage(groupID: Long, message: MessageData): Int = request(
    OneBot11Action.SEND_GROUP_MESSAGE, SendGroupMessageData(
        groupID = groupID,
        message = message,
        autoEscape = (message as? CQCodeMessageData)?.autoEscape ?: false
    )
).messageID

/**
 * @see [OneBot11Action.SEND_MESSAGE]
 */
suspend fun OneBot11API.sendMessage(messageType: String, userID: Long?, groupID: Long?, message: MessageData): Int =
    request(
        OneBot11Action.SEND_MESSAGE, SendMessageData(
            messageType = messageType,
            userID = userID,
            groupID = groupID,
            message = message,
            autoEscape = (message as? CQCodeMessageData)?.autoEscape ?: false
        )
    ).messageID

/**
 * @see [OneBot11Action.DELETE_MESSAGE]
 */
suspend fun OneBot11API.deleteMessage(messageID: Int) = request(
    OneBot11Action.DELETE_MESSAGE, MessageIDData(messageID)
)

/**
 * @see [OneBot11Action.GET_MESSAGE]
 */
suspend fun OneBot11API.getMessage(messageID: Int) = request(
    OneBot11Action.GET_MESSAGE, MessageIDData(messageID)
)

/**
 * @see [OneBot11Action.GET_FORWARD_MESSAGE]
 */
suspend fun OneBot11API.getForwardMessage(id: String) = request(
    OneBot11Action.GET_FORWARD_MESSAGE, IDData(id)
).message

/**
 * @see [OneBot11Action.SEND_LIKE]
 */
suspend fun OneBot11API.sendLike(userID: Long, times: Int) = request(
    OneBot11Action.SEND_LIKE, SendLikeData(
        userID = userID,
        times = times
    )
)

/**
 * @see [OneBot11Action.SET_GROUP_KICK]
 */
suspend fun OneBot11API.setGroupKick(groupID: Long, userID: Long, rejectAddRequest: Boolean) = request(
    OneBot11Action.SET_GROUP_KICK, SetGroupKickData(
        groupID = groupID,
        userID = userID,
        rejectAddRequest = rejectAddRequest
    )
)

/**
 * @see [OneBot11Action.SET_GROUP_BAN]
 */
suspend fun OneBot11API.setGroupBan(groupID: Long, userID: Long, duration: Long) = request(
    OneBot11Action.SET_GROUP_BAN, SetGroupBanData(
        groupID = groupID,
        userID = userID,
        duration = duration
    )
)

/**
 * @see [OneBot11Action.SET_GROUP_ANONYMOUS_BAN]
 */
suspend fun OneBot11API.setGroupAnonymousBan(groupID: Long, flag: String, duration: Long) = request(
    OneBot11Action.SET_GROUP_ANONYMOUS_BAN, SetGroupAnonymousBanData(
        groupID = groupID,
        anonymous = null,
        flag = flag,
        duration = duration
    )
)

/**
 * @see [OneBot11Action.SET_GROUP_ANONYMOUS_BAN]
 */
suspend fun OneBot11API.setGroupAnonymousBan(groupID: Long, sender: AnonymousSenderData, duration: Long) = request(
    OneBot11Action.SET_GROUP_ANONYMOUS_BAN, SetGroupAnonymousBanData(
        groupID = groupID,
        anonymous = sender,
        flag = null,
        duration = duration
    )
)

/**
 * @see [OneBot11Action.SET_GROUP_WHOLE_BAN]
 */
suspend fun OneBot11API.setGroupWholeBan(groupID: Long, enable: Boolean) = request(
    OneBot11Action.SET_GROUP_WHOLE_BAN, GroupIDEnableData(
        groupID = groupID,
        enable = enable
    )
)

/**
 * @see [OneBot11Action.SET_GROUP_ANONYMOUS]
 */
suspend fun OneBot11API.setGroupAdmin(groupID: Long, userID: Long, enable: Boolean) = request(
    OneBot11Action.SET_GROUP_ADMIN, GroupIDUserIDEnableData(
        groupID = groupID,
        userID = userID,
        enable = enable
    )
)

/**
 * @see [OneBot11Action.SET_GROUP_ANONYMOUS]
 */
suspend fun OneBot11API.setGroupAnonymous(groupID: Long, enable: Boolean) = request(
    OneBot11Action.SET_GROUP_ANONYMOUS, GroupIDEnableData(
        groupID = groupID,
        enable = enable
    )
)

/**
 * @see [OneBot11Action.SET_GROUP_CARD]
 */
suspend fun OneBot11API.setGroupCard(groupID: Long, userID: Long, card: String) = request(
    OneBot11Action.SET_GROUP_CARD, SetGroupCardData(
        groupID = groupID,
        userID = userID,
        card = card
    )
)

/**
 * @see [OneBot11Action.SET_GROUP_NAME]
 */
suspend fun OneBot11API.setGroupName(groupID: Long, groupName: String) = request(
    OneBot11Action.SET_GROUP_NAME, SetGroupNameData(
        groupID = groupID,
        groupName = groupName
    )
)

/**
 * @see [OneBot11Action.SET_GROUP_LEAVE]
 */
suspend fun OneBot11API.setGroupLeave(groupID: Long, isDismiss: Boolean) = request(
    OneBot11Action.SET_GROUP_LEAVE, SetGroupLeaveData(
        groupID = groupID,
        isDismiss = isDismiss
    )
)

/**
 * @see [OneBot11Action.SET_GROUP_SPECIAL_TITLE]
 */
suspend fun OneBot11API.setGroupSpecialTitle(groupID: Long, userID: Long, specialTitle: String, duration: Long) =
    request(
        OneBot11Action.SET_GROUP_SPECIAL_TITLE, SetGroupSpecialTitleData(
            groupID = groupID,
            userID = userID,
            specialTitle = specialTitle,
            duration = duration
        )
    )

/**
 * @see [OneBot11Action.SET_FRIEND_ADD_REQUEST]
 */
suspend fun OneBot11API.setFriendAddRequest(flag: String, approve: Boolean, remark: String) = request(
    OneBot11Action.SET_FRIEND_ADD_REQUEST, SetFriendAddRequestData(
        flag = flag,
        approve = approve,
        remark = remark
    )
)

/**
 * @see [OneBot11Action.SET_GROUP_ADD_REQUEST]
 */
suspend fun OneBot11API.setGroupAddRequest(flag: String, subType: String, approve: Boolean, reason: String) = request(
    OneBot11Action.SET_GROUP_ADD_REQUEST, SetGroupAddRequestData(
        flag = flag,
        approve = approve,
        subType = subType,
        reason = reason
    )
)

/**
 * @see [OneBot11Action.GET_LOGIN_INFO]
 */
suspend fun OneBot11API.getLoginInfo() = request(
    OneBot11Action.GET_LOGIN_INFO, Null
)

/**
 * @see [OneBot11Action.GET_STRANGER_INFO]
 */
suspend fun OneBot11API.getStrangerInfo(userID: Long, noCache: Boolean) = request(
    OneBot11Action.GET_STRANGER_INFO, GetStrangerInfoData(
        userID = userID,
        noCache = noCache
    )
)

/**
 * @see [OneBot11Action.GET_FRIEND_LIST]
 */
suspend fun OneBot11API.getGroupInfo(groupID: Long, noCache: Boolean) = request(
    OneBot11Action.GET_GROUP_INFO, GetGroupInfoData(
        groupID = groupID,
        noCache = noCache
    )
)

/**
 * @see [OneBot11Action.GET_FRIEND_LIST]
 */
suspend fun OneBot11API.getFriendList() = request(
    OneBot11Action.GET_FRIEND_LIST, Null
)

/**
 * @see [OneBot11Action.GET_GROUP_LIST]
 */
suspend fun OneBot11API.getGroupList() = request(
    OneBot11Action.GET_GROUP_LIST, Null
)

/**
 * @see [OneBot11Action.GET_GROUP_MEMBER_INFO]
 */
suspend fun OneBot11API.getGroupMemberList(groupID: Long) = request(
    OneBot11Action.GET_GROUP_MEMBER_LIST, GroupIDData(groupID)
)

/**
 * @see [OneBot11Action.GET_GROUP_HONOR_INFO]
 */
suspend fun OneBot11API.getGroupHonorInfo(groupID: Long, type: String) = request(
    OneBot11Action.GET_GROUP_HONOR_INFO, GetGroupHonorInfoData(
        groupID = groupID,
        type = type
    )
)

/**
 * @see [OneBot11Action.GET_COOKIES]
 */
suspend fun OneBot11API.getCookies(domain: String) = request(
    OneBot11Action.GET_COOKIES, DomainData(domain)
)

/**
 * @see [OneBot11Action.GET_CSRF_TOKEN]
 */
suspend fun OneBot11API.getCsrfToken() = request(
    OneBot11Action.GET_CSRF_TOKEN, Null
)

/**
 * @see [OneBot11Action.GET_CREDENTIALS]
 */
suspend fun OneBot11API.getCredentials(domain: String) = request(
    OneBot11Action.GET_CREDENTIALS, DomainData(domain)
)

/**
 * @see [OneBot11Action.GET_RECORD]
 */
suspend fun OneBot11API.getRecord(file: String, outFormat: String) = request(
    OneBot11Action.GET_RECORD, GetRecordData(
        file = file,
        outFormat = outFormat
    )
)

/**
 * @see [OneBot11Action.GET_IMAGE]
 */
suspend fun OneBot11API.getImage(file: String) = request(
    OneBot11Action.GET_IMAGE, FileData(
        file = file
    )
)

/**
 * @see [OneBot11Action.CAN_SEND_IMAGE]
 */
suspend fun OneBot11API.canSendImage() = request(
    OneBot11Action.CAN_SEND_IMAGE, Null
)

/**
 * @see [OneBot11Action.CAN_SEND_RECORD]
 */
suspend fun OneBot11API.canSendRecord() = request(
    OneBot11Action.CAN_SEND_RECORD, Null
)

/**
 * @see [OneBot11Action.GET_STATUS]
 */
suspend fun OneBot11API.getStatus() = request(
    OneBot11Action.GET_STATUS, Null
)

/**
 * @see [OneBot11Action.GET_VERSION_INFO]
 */
suspend fun OneBot11API.getVersionInfo() = request(
    OneBot11Action.GET_VERSION_INFO, Null
)

/**
 * @see [OneBot11Action.SET_RESTART]
 */
suspend fun OneBot11API.setRestart(delay: Int) = request(
    OneBot11Action.SET_RESTART, DelayData(delay)
)

/**
 * @see [OneBot11Action.CLEAN_CACHE]
 */
suspend fun OneBot11API.cleanCache() = request(
    OneBot11Action.CLEAN_CACHE, Null
)