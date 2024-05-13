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

package cn.chuanwise.onebot.v11.io

import cn.chuanwise.onebot.io.Action
import cn.chuanwise.onebot.io.data.Null
import cn.chuanwise.onebot.v11.io.data.action.CookiesData
import cn.chuanwise.onebot.v11.io.data.action.DelayData
import cn.chuanwise.onebot.v11.io.data.action.DomainData
import cn.chuanwise.onebot.v11.io.data.action.FileData
import cn.chuanwise.onebot.v11.io.data.action.FriendListElement
import cn.chuanwise.onebot.v11.io.data.action.GetCSRFTokenData
import cn.chuanwise.onebot.v11.io.data.action.GetCredentialsData
import cn.chuanwise.onebot.v11.io.data.action.GetGroupHonorInfoData
import cn.chuanwise.onebot.v11.io.data.action.GetGroupHonorInfoResponseData
import cn.chuanwise.onebot.v11.io.data.action.GetGroupInfoData
import cn.chuanwise.onebot.v11.io.data.action.GetGroupInfoResponseData
import cn.chuanwise.onebot.v11.io.data.action.GetGroupMemberInfoData
import cn.chuanwise.onebot.v11.io.data.action.GetLoginInfoData
import cn.chuanwise.onebot.v11.io.data.action.GetMessageData
import cn.chuanwise.onebot.v11.io.data.action.GetRecordData
import cn.chuanwise.onebot.v11.io.data.action.GetStrangerInfoData
import cn.chuanwise.onebot.v11.io.data.action.GroupIDData
import cn.chuanwise.onebot.v11.io.data.action.GroupIDEnableData
import cn.chuanwise.onebot.v11.io.data.action.GroupIDUserIDEnableData
import cn.chuanwise.onebot.v11.io.data.action.GroupMemberData
import cn.chuanwise.onebot.v11.io.data.action.HandleQuickOperationData
import cn.chuanwise.onebot.v11.io.data.action.MessageDataWrapper
import cn.chuanwise.onebot.v11.io.data.action.MessageIDData
import cn.chuanwise.onebot.v11.io.data.action.SendGroupKickData
import cn.chuanwise.onebot.v11.io.data.action.SendGroupMessageData
import cn.chuanwise.onebot.v11.io.data.action.SendLikeData
import cn.chuanwise.onebot.v11.io.data.action.SendMessageData
import cn.chuanwise.onebot.v11.io.data.action.SendPrivateMessageData
import cn.chuanwise.onebot.v11.io.data.action.SetFriendAddRequestData
import cn.chuanwise.onebot.v11.io.data.action.SetGroupAddRequestData
import cn.chuanwise.onebot.v11.io.data.action.SetGroupAnonymousBanData
import cn.chuanwise.onebot.v11.io.data.action.SetGroupBanData
import cn.chuanwise.onebot.v11.io.data.action.SetGroupCardData
import cn.chuanwise.onebot.v11.io.data.action.SetGroupLeaveData
import cn.chuanwise.onebot.v11.io.data.action.SetGroupNameData
import cn.chuanwise.onebot.v11.io.data.action.SetGroupSpecialTitleData
import cn.chuanwise.onebot.v11.io.data.action.YesOrNoData
import cn.chuanwise.onebot.v11.io.data.event.PrivateSenderData
import com.fasterxml.jackson.core.type.TypeReference

/**
 * # OneBot 11 Action
 *
 * Defined the action of [OneBot 11](https://github.com/botuniverse/onebot-11/blob/master/api/public.md).
 *
 * Each one is corresponding to an object, which allows to restrict the parameter and response type in
 * compilation time.
 *
 * @author Chuanwise
 */
class OneBot11Action<P, R>(
    override val name: String,
    override val paraTypeReference: TypeReference<P>,
    override val respTypeReference: TypeReference<R>
) : Action<P, R> {
    companion object {
        /**
         * Create a new instance for Kotlin to simply use.
         */
        inline operator fun <reified P, reified R> invoke(name: String) =
            OneBot11Action(name, object : TypeReference<P>() {}, object : TypeReference<R>() {})

        // https://github.com/botuniverse/onebot-11/blob/master/api/public.md#send_private_msg-%E5%8F%91%E9%80%81%E7%A7%81%E8%81%8A%E6%B6%88%E6%81%AF
        val SEND_PRIVATE_MESSAGE = OneBot11Action<SendPrivateMessageData, MessageIDData>("send_private_msg")

        // https://github.com/botuniverse/onebot-11/blob/master/api/public.md#send_group_msg-%E5%8F%91%E9%80%81%E7%BE%A4%E6%B6%88%E6%81%AF
        val SEND_GROUP_MESSAGE = OneBot11Action<SendGroupMessageData, MessageIDData>("send_group_msg")

        // https://github.com/botuniverse/onebot-11/blob/master/api/public.md#send_msg-%E5%8F%91%E9%80%81%E6%B6%88%E6%81%AF
        val SEND_MESSAGE = OneBot11Action<SendMessageData, MessageIDData>("send_msg")

        // https://github.com/botuniverse/onebot-11/blob/master/api/public.md#delete_msg-%E6%92%A4%E5%9B%9E%E6%B6%88%E6%81%AF
        val DELETE_MESSAGE = OneBot11Action<MessageIDData, Null>("delete_msg")

        // https://github.com/botuniverse/onebot-11/blob/master/api/public.md#get_msg-%E8%8E%B7%E5%8F%96%E6%B6%88%E6%81%AF
        val GET_MESSAGE = OneBot11Action<MessageIDData, GetMessageData>("get_msg")

        // https://github.com/botuniverse/onebot-11/blob/master/api/public.md#get_forward_msg-%E8%8E%B7%E5%8F%96%E5%90%88%E5%B9%B6%E8%BD%AC%E5%8F%91%E6%B6%88%E6%81%AF
        val GET_FORWARD_MESSAGE = OneBot11Action<MessageIDData, MessageDataWrapper>("get_forward_msg")

        // https://github.com/botuniverse/onebot-11/blob/master/api/public.md#send_like-%E5%8F%91%E9%80%81%E5%A5%BD%E5%8F%8B%E8%B5%9E
        val SEND_LIKE = OneBot11Action<SendLikeData, Null>("send_like")

        // https://github.com/botuniverse/onebot-11/blob/master/api/public.md#set_group_kick-%E7%BE%A4%E7%BB%84%E8%B8%A2%E4%BA%BA
        val SET_GROUP_KICK = OneBot11Action<SendGroupKickData, Null>("set_group_kick")

        // https://github.com/botuniverse/onebot-11/blob/master/api/public.md#set_group_ban-%E7%BE%A4%E7%BB%84%E5%8D%95%E4%BA%BA%E7%A6%81%E8%A8%80
        val SET_GROUP_BAN = OneBot11Action<SetGroupBanData, Null>("set_group_ban")

        // https://github.com/botuniverse/onebot-11/blob/master/api/public.md#set_group_anonymous_ban-%E7%BE%A4%E7%BB%84%E5%8C%BF%E5%90%8D%E7%94%A8%E6%88%B7%E7%A6%81%E8%A8%80
        val SET_GROUP_ANONYMOUS_BAN = OneBot11Action<SetGroupAnonymousBanData, Null>("set_group_anonymous_ban")

        // https://github.com/botuniverse/onebot-11/blob/master/api/public.md#set_group_whole_ban-%E7%BE%A4%E7%BB%84%E5%85%A8%E5%91%98%E7%A6%81%E8%A8%80
        val SET_GROUP_WHOLE_BAN = OneBot11Action<GroupIDEnableData, Null>("set_group_whole_ban")

        // https://github.com/botuniverse/onebot-11/blob/master/api/public.md#set_group_admin-%E7%BE%A4%E7%BB%84%E8%AE%BE%E7%BD%AE%E7%AE%A1%E7%90%86%E5%91%98
        val SET_GROUP_ADMIN = OneBot11Action<GroupIDUserIDEnableData, Null>("set_group_admin")

        // https://github.com/botuniverse/onebot-11/blob/master/api/public.md#set_group_anonymous-%E7%BE%A4%E7%BB%84%E5%8C%BF%E5%90%8D
        val SET_GROUP_ANONYMOUS = OneBot11Action<GroupIDUserIDEnableData, Null>("set_group_anonymous")

        // https://github.com/botuniverse/onebot-11/blob/master/api/public.md#set_group_card-%E8%AE%BE%E7%BD%AE%E7%BE%A4%E5%90%8D%E7%89%87%E7%BE%A4%E5%A4%87%E6%B3%A8
        val SET_GROUP_CARD = OneBot11Action<SetGroupCardData, Null>("set_group_card")

        // https://github.com/botuniverse/onebot-11/blob/master/api/public.md#set_group_name-%E8%AE%BE%E7%BD%AE%E7%BE%A4%E5%90%8D
        val SET_GROUP_NAME = OneBot11Action<SetGroupNameData, Null>("set_group_name")

        // https://github.com/botuniverse/onebot-11/blob/master/api/public.md#set_group_leave-%E9%80%80%E5%87%BA%E7%BE%A4%E7%BB%84
        val SET_GROUP_LEAVE = OneBot11Action<SetGroupLeaveData, Null>("set_group_leave")

        // https://github.com/botuniverse/onebot-11/blob/master/api/public.md#set_group_special_title-%E8%AE%BE%E7%BD%AE%E7%BE%A4%E7%BB%84%E4%B8%93%E5%B1%9E%E5%A4%B4%E8%A1%94
        val SET_GROUP_SPECIAL_TITLE = OneBot11Action<SetGroupSpecialTitleData, Null>("set_group_special_title")

        // https://github.com/botuniverse/onebot-11/blob/master/api/public.md#set_friend_add_request-%E5%A4%84%E7%90%86%E5%8A%A0%E5%A5%BD%E5%8F%8B%E8%AF%B7%E6%B1%82
        val SET_FRIEND_ADD_REQUEST = OneBot11Action<SetFriendAddRequestData, Null>("set_friend_add_request")

        // https://github.com/botuniverse/onebot-11/blob/master/api/public.md#set_group_add_request-%E5%A4%84%E7%90%86%E5%8A%A0%E7%BE%A4%E8%AF%B7%E6%B1%82%E9%82%80%E8%AF%B7
        val SET_GROUP_ADD_REQUEST = OneBot11Action<SetGroupAddRequestData, Null>("set_group_add_request")

        // https://github.com/botuniverse/onebot-11/blob/master/api/public.md#get_login_info-%E8%8E%B7%E5%8F%96%E7%99%BB%E5%BD%95%E5%8F%B7%E4%BF%A1%E6%81%AF
        val GET_LOGIN_INFO = OneBot11Action<Null, GetLoginInfoData>("get_login_info")

        // https://github.com/botuniverse/onebot-11/blob/master/api/public.md#get_stranger_info-%E8%8E%B7%E5%8F%96%E9%99%8C%E7%94%9F%E4%BA%BA%E4%BF%A1%E6%81%AF
        val GET_STRANGER_INFO = OneBot11Action<GetStrangerInfoData, PrivateSenderData>("get_stranger_info")

        // https://github.com/botuniverse/onebot-11/blob/master/api/public.md#get_friend_list-%E8%8E%B7%E5%8F%96%E5%A5%BD%E5%8F%8B%E5%88%97%E8%A1%A8
        val GET_FRIEND_LIST = OneBot11Action<Null, List<FriendListElement>>("get_friend_list")

        // https://github.com/botuniverse/onebot-11/blob/master/api/public.md#get_group_info-%E8%8E%B7%E5%8F%96%E7%BE%A4%E4%BF%A1%E6%81%AF
        val GET_GROUP_INFO = OneBot11Action<GetGroupInfoData, GetGroupInfoResponseData>("get_group_info")

        // https://github.com/botuniverse/onebot-11/blob/master/api/public.md#get_group_list-%E8%8E%B7%E5%8F%96%E7%BE%A4%E5%88%97%E8%A1%A8
        val GET_GROUP_LIST = OneBot11Action<Null, List<GetGroupInfoResponseData>>("get_group_list")

        // https://github.com/botuniverse/onebot-11/blob/master/api/public.md#get_group_member_info-%E8%8E%B7%E5%8F%96%E7%BE%A4%E6%88%90%E5%91%98%E4%BF%A1%E6%81%AF
        val GET_GROUP_MEMBER_INFO = OneBot11Action<GetGroupMemberInfoData, GroupMemberData>("get_group_member_info")

        // https://github.com/botuniverse/onebot-11/blob/master/api/public.md#get_group_member_list-%E8%8E%B7%E5%8F%96%E7%BE%A4%E6%88%90%E5%91%98%E5%88%97%E8%A1%A8
        val GET_GROUP_MEMBER_LIST = OneBot11Action<GroupIDData, List<GroupMemberData>>("get_group_member_list")

        // https://github.com/botuniverse/onebot-11/blob/master/api/public.md#get_group_honor_info-%E8%8E%B7%E5%8F%96%E7%BE%A4%E8%8D%A3%E8%AA%89%E4%BF%A1%E6%81%AF
        val GET_GROUP_HONOR_INFO =
            OneBot11Action<GetGroupHonorInfoData, GetGroupHonorInfoResponseData>("get_group_honor_info")

        // https://github.com/botuniverse/onebot-11/blob/master/api/public.md#get_cookies-%E8%8E%B7%E5%8F%96-cookies
        val GET_COOKIES = OneBot11Action<DomainData, CookiesData>("get_cookies")

        // https://github.com/botuniverse/onebot-11/blob/master/api/public.md#get_csrf_token-%E8%8E%B7%E5%8F%96-csrf-token
        val GET_CSRF_TOKEN = OneBot11Action<DomainData, GetCSRFTokenData>("get_csrf_token")

        // https://github.com/botuniverse/onebot-11/blob/master/api/public.md#get_credentials-%E8%8E%B7%E5%8F%96-qq-%E7%9B%B8%E5%85%B3%E6%8E%A5%E5%8F%A3%E5%87%AD%E8%AF%81
        val GET_CREDENTIALS = OneBot11Action<DomainData, GetCredentialsData>("get_credentials")

        // https://github.com/botuniverse/onebot-11/blob/master/api/public.md#get_record-%E8%8E%B7%E5%8F%96%E8%AF%AD%E9%9F%B3
        val GET_RECORD = OneBot11Action<GetRecordData, FileData>("get_record")

        // https://github.com/botuniverse/onebot-11/blob/master/api/public.md#get_image-%E8%8E%B7%E5%8F%96%E5%9B%BE%E7%89%87
        val GET_IMAGE = OneBot11Action<FileData, FileData>("get_image")

        // https://github.com/botuniverse/onebot-11/blob/master/api/public.md#can_send_image-%E6%A3%80%E6%9F%A5%E6%98%AF%E5%90%A6%E5%8F%AF%E4%BB%A5%E5%8F%91%E9%80%81%E5%9B%BE%E7%89%87
        val CAN_SEND_IMAGE = OneBot11Action<Null, YesOrNoData>("can_send_image")

        // https://github.com/botuniverse/onebot-11/blob/master/api/public.md#can_send_record-%E6%A3%80%E6%9F%A5%E6%98%AF%E5%90%A6%E5%8F%AF%E4%BB%A5%E5%8F%91%E9%80%81%E8%AF%AD%E9%9F%B3
        val CAN_SEND_RECORD = OneBot11Action<Null, YesOrNoData>("can_send_record")

        // "online", "good", ...
        // https://github.com/botuniverse/onebot-11/blob/master/api/public.md#get_status-%E8%8E%B7%E5%8F%96%E8%BF%90%E8%A1%8C%E7%8A%B6%E6%80%81
        val GET_STATUS = OneBot11Action<Null, Map<*, *>>("get_status")

        // "app_name", "app_version", "protocol_version", ...
        // https://github.com/botuniverse/onebot-11/blob/master/api/public.md#get_version_info-%E8%8E%B7%E5%8F%96%E7%89%88%E6%9C%AC%E4%BF%A1%E6%81%AF
        val GET_VERSION_INFO = OneBot11Action<Null, Map<*, *>>("get_version_info")

        // https://github.com/botuniverse/onebot-11/blob/master/api/public.md#set_restart-%E9%87%8D%E5%90%AF-onebot-%E5%AE%9E%E7%8E%B0
        val SET_RESTART = OneBot11Action<DelayData, Null>("set_restart")

        // https://github.com/botuniverse/onebot-11/blob/master/api/public.md#clean_cache-%E6%B8%85%E7%90%86%E7%BC%93%E5%AD%98
        val CLEAN_CACHE = OneBot11Action<Null, Null>("clean_cache")

        // https://github.com/botuniverse/onebot-11/blob/master/api/hidden.md#handle_quick_operation-%E5%AF%B9%E4%BA%8B%E4%BB%B6%E6%89%A7%E8%A1%8C%E5%BF%AB%E9%80%9F%E6%93%8D%E4%BD%9C
        val HIDDEN_HANDLE_QUICK_OPERATION = OneBot11Action<HandleQuickOperationData, Null>("handle_quick_operation")
    }
}
