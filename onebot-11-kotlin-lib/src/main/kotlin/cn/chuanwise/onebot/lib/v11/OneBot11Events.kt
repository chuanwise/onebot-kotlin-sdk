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

/**
 * Defined the events of [OneBot 11](https://github.com/botuniverse/onebot-11/blob/master/event).
 *
 * Each one is corresponding to an object, which allows to restrict the parameter and response type in
 * compilation time.
 *
 * @author Chuanwise
 */
@file:JvmName("OneBot11Events")

package cn.chuanwise.onebot.lib.v11

import cn.chuanwise.onebot.lib.Event
import cn.chuanwise.onebot.lib.v11.data.event.EventData
import cn.chuanwise.onebot.lib.v11.data.event.FriendAddNoticeEventData
import cn.chuanwise.onebot.lib.v11.data.event.FriendAddRequestEventData
import cn.chuanwise.onebot.lib.v11.data.event.FriendAddRequestQuickOperationData
import cn.chuanwise.onebot.lib.v11.data.event.FriendMessageRecallNoticeEventData
import cn.chuanwise.onebot.lib.v11.data.event.GroupAddRequestEventData
import cn.chuanwise.onebot.lib.v11.data.event.GroupAddRequestQuickOperationData
import cn.chuanwise.onebot.lib.v11.data.event.GroupAdminChangedNoticeEventData
import cn.chuanwise.onebot.lib.v11.data.event.GroupFileUploadNoticeEventData
import cn.chuanwise.onebot.lib.v11.data.event.GroupMemberChangedNoticeEventData
import cn.chuanwise.onebot.lib.v11.data.event.GroupMemberHonorChangedNoticeEventData
import cn.chuanwise.onebot.lib.v11.data.event.GroupMessageEventData
import cn.chuanwise.onebot.lib.v11.data.event.GroupMessageMessageMessageQuickOperationData
import cn.chuanwise.onebot.lib.v11.data.event.GroupMessageRecallNoticeEventData
import cn.chuanwise.onebot.lib.v11.data.event.GroupMuteNoticeEventData
import cn.chuanwise.onebot.lib.v11.data.event.GroupPokeNoticeEventData
import cn.chuanwise.onebot.lib.v11.data.event.GroupRedPacketLuckyKingNoticeEventData
import cn.chuanwise.onebot.lib.v11.data.event.HeartbeatEventData
import cn.chuanwise.onebot.lib.v11.data.event.LifecycleMetaEventData
import cn.chuanwise.onebot.lib.v11.data.event.MessageEventData
import cn.chuanwise.onebot.lib.v11.data.event.MessageQuickOperationData
import cn.chuanwise.onebot.lib.v11.data.event.MetaEventData
import cn.chuanwise.onebot.lib.v11.data.event.NoticeEventData
import cn.chuanwise.onebot.lib.v11.data.event.PrivateMessageEventData
import cn.chuanwise.onebot.lib.v11.data.event.PrivateMessageEventMessageQuickOperationData
import cn.chuanwise.onebot.lib.v11.data.event.QuickOperationData
import cn.chuanwise.onebot.lib.v11.data.event.RequestEventData
import com.fasterxml.jackson.core.type.TypeReference

inline fun <reified P : EventData, reified R : QuickOperationData> EventWithQuickOperation() = Event(
    object : TypeReference<P>() {}, object : TypeReference<R>() {}
)

// https://github.com/botuniverse/onebot-11/blob/master/event/meta.md
val META_EVENT = Event<MetaEventData>()

// https://github.com/botuniverse/onebot-11/blob/master/event/meta.md
val HEARTBEAT_META_EVENT = Event<HeartbeatEventData>()

val LIFECYCLE_META_EVENT = Event<LifecycleMetaEventData>()

// https://github.com/botuniverse/onebot-11/blob/master/event/message.md
val MESSAGE_EVENT = EventWithQuickOperation<MessageEventData, MessageQuickOperationData>()

// https://github.com/botuniverse/onebot-11/blob/master/event/message.md#%E7%A7%81%E8%81%8A%E6%B6%88%E6%81%AF
val PRIVATE_MESSAGE_EVENT =
    EventWithQuickOperation<PrivateMessageEventData, PrivateMessageEventMessageQuickOperationData>()

// https://github.com/botuniverse/onebot-11/blob/master/event/message.md#%E7%BE%A4%E6%B6%88%E6%81%AF
val GROUP_MESSAGE_EVENT = EventWithQuickOperation<GroupMessageEventData, GroupMessageMessageMessageQuickOperationData>()

// https://github.com/botuniverse/onebot-11/blob/master/event/notice.md
val NOTICE_EVENT = Event<NoticeEventData>()

// https://github.com/botuniverse/onebot-11/blob/master/event/notice.md#%E7%BE%A4%E6%96%87%E4%BB%B6%E4%B8%8A%E4%BC%A0
val GROUP_FILE_UPLOAD_NOTICE_EVENT = Event<GroupFileUploadNoticeEventData>()

// https://github.com/botuniverse/onebot-11/blob/master/event/notice.md#%E7%BE%A4%E7%AE%A1%E7%90%86%E5%91%98%E5%8F%98%E5%8A%A8
val GROUP_ADMIN_CHANGED_NOTICE_EVENT = Event<GroupAdminChangedNoticeEventData>()

// https://github.com/botuniverse/onebot-11/blob/master/event/notice.md#%E7%BE%A4%E6%88%90%E5%91%98%E5%87%8F%E5%B0%91
// https://github.com/botuniverse/onebot-11/blob/master/event/notice.md#%E7%BE%A4%E6%88%90%E5%91%98%E5%A2%9E%E5%8A%A0
val GROUP_MEMBER_CHANGED_NOTICE_EVENT = Event<GroupMemberChangedNoticeEventData>()

// https://github.com/botuniverse/onebot-11/blob/master/event/notice.md#%E7%BE%A4%E7%A6%81%E8%A8%80
val GROUP_MUTE_NOTICE_EVENT = Event<GroupMuteNoticeEventData>()

// https://github.com/botuniverse/onebot-11/blob/master/event/notice.md#%E5%A5%BD%E5%8F%8B%E6%B7%BB%E5%8A%A0
val FRIEND_ADD_NOTICE_EVENT = Event<FriendAddNoticeEventData>()

// https://github.com/botuniverse/onebot-11/blob/master/event/notice.md#%E7%BE%A4%E6%B6%88%E6%81%AF%E6%92%A4%E5%9B%9E
val GROUP_MESSAGE_RECALL_NOTICE_EVENT = Event<GroupMessageRecallNoticeEventData>()

// https://github.com/botuniverse/onebot-11/blob/master/event/notice.md#%E5%A5%BD%E5%8F%8B%E6%B6%88%E6%81%AF%E6%92%A4%E5%9B%9E
val FRIEND_MESSAGE_RECALL_NOTICE_EVENT = Event<FriendMessageRecallNoticeEventData>()

// https://github.com/botuniverse/onebot-11/blob/master/event/notice.md#%E7%BE%A4%E5%86%85%E6%88%B3%E4%B8%80%E6%88%B3
val GROUP_POKE_NOTICE_EVENT = Event<GroupPokeNoticeEventData>()

// https://github.com/botuniverse/onebot-11/blob/master/event/notice.md#%E7%BE%A4%E7%BA%A2%E5%8C%85%E8%BF%90%E6%B0%94%E7%8E%8B
val GROUP_RED_PACK_LUCKY_KING_NOTICE_EVENT = Event<GroupRedPacketLuckyKingNoticeEventData>()

// https://github.com/botuniverse/onebot-11/blob/master/event/notice.md#%E7%BE%A4%E6%88%90%E5%91%98%E8%8D%A3%E8%AA%89%E5%8F%98%E6%9B%B4
val GROUP_MEMBER_HONOR_CHANGED_NOTICE_EVENT = Event<GroupMemberHonorChangedNoticeEventData>()

// https://github.com/botuniverse/onebot-11/blob/master/event/request.md
val REQUEST_EVENT = Event<RequestEventData>()

// https://github.com/botuniverse/onebot-11/blob/master/event/request.md#%E5%8A%A0%E5%A5%BD%E5%8F%8B%E8%AF%B7%E6%B1%82
val FRIEND_ADD_REQUEST_EVENT =
    EventWithQuickOperation<FriendAddRequestEventData, FriendAddRequestQuickOperationData>()

// https://github.com/botuniverse/onebot-11/blob/master/event/request.md#%E5%8A%A0%E7%BE%A4%E8%AF%B7%E6%B1%82%E9%82%80%E8%AF%B7
val GROUP_ADD_REQUEST_EVENT = EventWithQuickOperation<GroupAddRequestEventData, GroupAddRequestQuickOperationData>()