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

@file:JvmName("MessageEventSerializations")
package cn.chuanwise.onebot.v11.io.serialization

import cn.chuanwise.onebot.serialization.Tree
import cn.chuanwise.onebot.serialization.primitive
import cn.chuanwise.onebot.serialization.tree
import cn.chuanwise.onebot.v11.io.data.AnonymousSenderData
import cn.chuanwise.onebot.v11.io.data.GroupMessageEventData
import cn.chuanwise.onebot.v11.io.data.GroupSenderData
import cn.chuanwise.onebot.v11.io.data.MessageEventData
import cn.chuanwise.onebot.v11.io.data.PrivateMessageEventData
import cn.chuanwise.onebot.v11.io.data.PrivateSenderData


private fun Tree.toPrivateSender() = PrivateSenderData(
    userID = get(USER_ID).primitive.long,
    nickname = get(NICKNAME).primitive.string,
    sex = getOptionalNullableButIgnoreNull(SEX)?.primitive?.string,
    age = getOptionalNullableButIgnoreNull(AGE)?.primitive?.int
)

private fun Tree.toGroupSender() = GroupSenderData(
    userID = get(USER_ID).primitive.long,
    nickname = get(NICKNAME).primitive.string,
    sex = getOptionalNullableButIgnoreNull(SEX)?.primitive?.string,
    age = getOptionalNullableButIgnoreNull(AGE)?.primitive?.int,
    card = getOptionalNullableButIgnoreNull(CARD)?.primitive?.string,
    level = getOptionalNullableButIgnoreNull(LEVEL)?.primitive?.string,
    role = getOptionalNullableButIgnoreNull(ROLE)?.primitive?.string,
    title = getOptionalNullableButIgnoreNull(TITLE)?.primitive?.string,
    area = getOptionalNullableButIgnoreNull(AREA)?.primitive?.string
)

private fun Tree.toAnonymous(): AnonymousSenderData = AnonymousSenderData(
    id = get(ID).primitive.int,
    name = get(NAME).primitive.string,
    flag = get(FLAG).primitive.string
)

fun Tree.toMessageEvent(): MessageEventData {
    val time = get(TIME).primitive.long
    val selfID = get(SELF_ID).primitive.long

    return when (val messageType = get(MESSAGE_TYPE).primitive.string) {
        PRIVATE -> PrivateMessageEventData(
            time = time,
            selfID = selfID,
            postType = MESSAGE,
            messageType = messageType,
            subType = get(SUB_TYPE).primitive.string,
            messageID = get(MESSAGE_ID).primitive.long,
            userID = get(USER_ID).primitive.long,
            message = get(MESSAGE).toMessage(),
            rawMessage = get(RAW_MESSAGE).primitive.string,
            font = get(FONT).primitive.int,
            sender = get(SENDER).tree.toPrivateSender()
        )
        GROUP -> GroupMessageEventData(
            time = time,
            selfID = selfID,
            postType = MESSAGE,
            messageType = messageType,
            subType = get(SUB_TYPE).primitive.string,
            messageID = get(MESSAGE_ID).primitive.long,
            groupID = get(GROUP_ID).primitive.long,
            userID = get(USER_ID).primitive.long,
            message = get(MESSAGE).toMessage(),
            rawMessage = get(RAW_MESSAGE).primitive.string,
            font = get(FONT).primitive.int,
            sender = get(SENDER).tree.toGroupSender(),
            anonymous = getOptionalNullableButIgnoreNull(ANONYMOUS)?.tree?.toAnonymous()
        )
        else -> throw IllegalArgumentException("Unexpected message type: $messageType")
    }
}