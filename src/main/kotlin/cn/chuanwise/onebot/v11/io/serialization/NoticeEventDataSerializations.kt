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

@file:JvmName("NoticeEventDataSerializations")
package cn.chuanwise.onebot.v11.io.serialization

import cn.chuanwise.onebot.serialization.Tree
import cn.chuanwise.onebot.serialization.primitive
import cn.chuanwise.onebot.serialization.tree
import cn.chuanwise.onebot.v11.io.data.FileData
import cn.chuanwise.onebot.v11.io.data.FriendMessageRecallEventData
import cn.chuanwise.onebot.v11.io.data.GroupAdminChangedEventData
import cn.chuanwise.onebot.v11.io.data.GroupFileUploadEventData
import cn.chuanwise.onebot.v11.io.data.GroupMemberChangedEventData
import cn.chuanwise.onebot.v11.io.data.GroupMemberHonorChangedEventData
import cn.chuanwise.onebot.v11.io.data.GroupMessageRecallEventData
import cn.chuanwise.onebot.v11.io.data.GroupMuteEventData
import cn.chuanwise.onebot.v11.io.data.GroupPokeEventData
import cn.chuanwise.onebot.v11.io.data.GroupRedPacketLuckyKingEventData
import cn.chuanwise.onebot.v11.io.data.NewFriendEventData
import cn.chuanwise.onebot.v11.io.data.NoticeEventData

private fun Tree.toFileData(): FileData = FileData(
    id = get(ID).primitive.string,
    name = get(NAME).primitive.string,
    size = get(SIZE).primitive.long,
    busid = get(BUSID).primitive.long
)

fun Tree.toNoticeEvent(): NoticeEventData {
    val time = get(TIME).primitive.long
    val selfID = get(SELF_ID).primitive.long

    return when (val noticeType = get(NOTICE_TYPE).primitive.string) {
        GROUP_UPLOAD -> GroupFileUploadEventData(
            time = time,
            selfID = selfID,
            postType = NOTICE,
            noticeType = noticeType,
            groupID = get(GROUP_ID).primitive.long,
            userID = get(USER_ID).primitive.long,
            file = get(FILE).tree.toFileData(),
        )
        GROUP_ADMIN -> GroupAdminChangedEventData(
            time = time,
            selfID = selfID,
            postType = NOTICE,
            noticeType = noticeType,
            groupID = get(GROUP_ID).primitive.long,
            userID = get(USER_ID).primitive.long,
            subType = get(SUB_TYPE).primitive.string
        )
        GROUP_DECREASE, GROUP_INCREASE -> GroupMemberChangedEventData(
            time = time,
            selfID = selfID,
            postType = NOTICE,
            noticeType = noticeType,
            groupID = get(GROUP_ID).primitive.long,
            userID = get(USER_ID).primitive.long,
            operatorID = get(OPERATOR_ID).primitive.long,
            subType = get(SUB_TYPE).primitive.string
        )
        GROUP_BAN -> GroupMuteEventData(
            time = time,
            selfID = selfID,
            postType = NOTICE,
            noticeType = noticeType,
            subType = get(SUB_TYPE).primitive.string,
            groupID = get(GROUP_ID).primitive.long,
            userID = get(USER_ID).primitive.long,
            operatorID = get(OPERATOR_ID).primitive.long,
            duration = get(DURATION).primitive.long
        )
        FRIEND_ADD -> NewFriendEventData(
            time = time,
            selfID = selfID,
            postType = NOTICE,
            noticeType = noticeType,
            userID = get(USER_ID).primitive.long
        )
        GROUP_RECALL -> GroupMessageRecallEventData(
            time = time,
            selfID = selfID,
            postType = NOTICE,
            noticeType = noticeType,
            groupID = get(GROUP_ID).primitive.long,
            userID = get(USER_ID).primitive.long,
            operatorID = get(OPERATOR_ID).primitive.long,
            messageID = get(MESSAGE_ID).primitive.long
        )
        FRIEND_RECALL -> FriendMessageRecallEventData(
            time = time,
            selfID = selfID,
            postType = NOTICE,
            noticeType = noticeType,
            userID = get(USER_ID).primitive.long,
            messageID = get(MESSAGE_ID).primitive.long
        )
        GROUP_POKE -> GroupPokeEventData(
            time = time,
            selfID = selfID,
            postType = NOTICE,
            noticeType = noticeType,
            groupID = get(GROUP_ID).primitive.long,
            userID = get(USER_ID).primitive.long,
            targetID = get(TARGET_ID).primitive.long,
            subType = get(SUB_TYPE).primitive.string
        )
        LUCKY_KING -> GroupRedPacketLuckyKingEventData(
            time = time,
            selfID = selfID,
            postType = NOTICE,
            noticeType = noticeType,
            groupID = get(GROUP_ID).primitive.long,
            userID = get(USER_ID).primitive.long,
            targetID = get(TARGET_ID).primitive.long,
        )
        HONOR -> GroupMemberHonorChangedEventData(
            time = time,
            selfID = selfID,
            postType = NOTICE,
            noticeType = noticeType,
            groupID = get(GROUP_ID).primitive.long,
            userID = get(USER_ID).primitive.long,
            subType = get(SUB_TYPE).primitive.string,
            honorType = get(HONOR_TYPE).primitive.string
        )
        else -> throw IllegalArgumentException("Unexpected notice type: $noticeType")
    }
}