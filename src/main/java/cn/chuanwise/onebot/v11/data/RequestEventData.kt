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

