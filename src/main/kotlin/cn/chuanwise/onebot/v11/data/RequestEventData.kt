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

package cn.chuanwise.onebot.v11.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// https://github.com/botuniverse/onebot-11/blob/master/event/request.md
@Serializable
sealed class RequestEventData: EventData() {
    @SerialName("request_type")
    abstract val requestType: String
}

@Serializable
class FriendAddRequestEventData(
    override val time: Long,
    override val selfID: Long,

    // "request"
    override val postType: String,

    // "friend"
    override val requestType: String,

    @SerialName("user_id")
    val userID: Long,

    @SerialName("comment")
    val comment: String,

    @SerialName("flag")
    val flag: String,
): RequestEventData()

@Serializable
class FriendAddRequestReceiptData(
    @SerialName("approve")
    val approve: Boolean?,

    @SerialName("remark")
    val remark: String?
)

@Serializable
class GroupAddRequestEventData(
    override val time: Long,
    override val selfID: Long,

    // "request"
    override val postType: String,

    // "group"
    override val requestType: String,

    // "add" or "invite"
    @SerialName("sub_type")
    val subType: String,

    @SerialName("group_id")
    val groupID: Long,

    @SerialName("user_id")
    val userID: Long,

    @SerialName("comment")
    val comment: String,

    @SerialName("flag")
    val flag: String,
): RequestEventData()

@Serializable
class GroupAddRequestReceiptData(
    @SerialName("approve")
    val approve: Boolean?,

    @SerialName("reason")
    val reason: String?
)

