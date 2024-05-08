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

@file:JvmName("RequestEventDataSerializations")
package cn.chuanwise.onebot.v11.io.serialization

import cn.chuanwise.onebot.serialization.Tree
import cn.chuanwise.onebot.serialization.primitive
import cn.chuanwise.onebot.v11.io.data.FriendAddRequestEventData
import cn.chuanwise.onebot.v11.io.data.GroupAddRequestEventData
import cn.chuanwise.onebot.v11.io.data.RequestEventData
fun Tree.toRequestEvent(): RequestEventData {
    val time = get(TIME).primitive.long
    val selfID = get(SELF_ID).primitive.long

    return when (val requestType = get(REQUEST_TYPE).primitive.string) {
        FRIEND -> FriendAddRequestEventData(
            time = time,
            selfID = selfID,
            postType = REQUEST,
            requestType = requestType,
            userID = get(USER_ID).primitive.long,
            comment = get(COMMENT).primitive.string,
            flag = get(FLAG).primitive.string
        )
        GROUP -> GroupAddRequestEventData(
            time = time,
            selfID = selfID,
            postType = REQUEST,
            requestType = requestType,
            groupID = get(GROUP_ID).primitive.long,
            userID = get(USER_ID).primitive.long,
            comment = get(COMMENT).primitive.string,
            flag = get(FLAG).primitive.string,
            subType = get(SUB_TYPE).primitive.string
        )
        else -> throw IllegalArgumentException("Unexpected request type: $requestType")
    }
}