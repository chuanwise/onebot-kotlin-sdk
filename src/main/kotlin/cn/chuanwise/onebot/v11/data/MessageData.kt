package cn.chuanwise.onebot.v11.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// https://github.com/botuniverse/onebot-11/blob/master/message/segment.md
@Serializable
sealed class MessageData {
    @SerialName("type")
    abstract val type: String

    @SerialName("data")
    abstract val data: SegmentData
}

@Serializable
sealed class SegmentData

@Serializable
data class TextData(
    @SerialName("type")
    val text: String
) : SegmentData()

@Serializable
data class FaceData(
    // https://github.com/kyubotics/coolq-http-api/wiki/%E8%A1%A8%E6%83%85-CQ-%E7%A0%81-ID-%E8%A1%A8
    @SerialName("id")
    val id: String
) : SegmentData()

@Serializable
data class ImageData(
    @SerialName("file")
    val file: String,

    // "flash" or none
    @SerialName("type")
    val type: String?,

    // send only
    @SerialName("url")
    val url: String?,

    // receive only
    @SerialName("cache")
    val cache: Boolean?,

    // receive only
    @SerialName("proxy")
    val proxy: Boolean?,

    // receive only
    @SerialName("timeout")
    val timeout: Long?
) : SegmentData()

@Serializable
data class SoundData(
    @SerialName("file")
    val file: String,

    @SerialName("type")
    val magic: Boolean,

    // send only
    @SerialName("url")
    val url: String?,

    // receive only
    @SerialName("cache")
    val cache: Boolean?,

    // receive only
    @SerialName("proxy")
    val proxy: Boolean?,

    // receive only
    @SerialName("timeout")
    val timeout: Long?
) : SegmentData()

@Serializable
data class VideoData(
    @SerialName("file")
    val file: String,

    // send only
    @SerialName("url")
    val url: String?,

    // receive only
    @SerialName("cache")
    val cache: Boolean?,

    // receive only
    @SerialName("proxy")
    val proxy: Boolean?,

    // receive only
    @SerialName("timeout")
    val timeout: Long?
) : SegmentData()

@Serializable
data class AtData(
    // qq code or "all"
    @SerialName("qq")
    val qq: String
) : SegmentData()

// RPSData (Rock, Paper, Scissors)
// DiceData, ShakeData has empty body

@Serializable
data class PokeData(
    // https://github.com/mamoe/mirai/blob/f5eefae7ecee84d18a66afce3f89b89fe1584b78/mirai-core/src/commonMain/kotlin/net.mamoe.mirai/message/data/HummerMessage.kt#L49
    @SerialName("type")
    val type: String,

    @SerialName("id")
    val id: Int,

    @SerialName("name")
    val name: String?
) : SegmentData()

@Serializable
data object AnonymousSendingTag : SegmentData()

@Serializable
data class ShareData(
    @SerialName("url")
    val url: String,

    @SerialName("title")
    val title: String,

    // optional if send
    @SerialName("content")
    val content: String?,

    // optional if send
    // image url
    @SerialName("image")
    val image: String?
) : SegmentData()


@Serializable
data class RecommendationData(
    // "qq", "group"
    // "qq", "163", "xm"
    @SerialName("type")
    val type: String,

    @SerialName("nickname")
    val id: String,
) : SegmentData()

@Serializable
data class LocationData(
    @SerialName("lat")
    val latitude: Double,

    @SerialName("lon")
    val longitude: Double,

    // optional if send
    @SerialName("title")
    val title: String?,

    // optional if send
    @SerialName("content")
    val content: String?
) : SegmentData()


@Serializable
data class CustomMusicRecommendationData(
    // "custom"
    @SerialName("type")
    val type: String?,

    // jump url
    @SerialName("url")
    val url: String?,

    // audio url
    @SerialName("audio")
    val audio: String,

    // title url
    @SerialName("title")
    val title: String,

    @SerialName("content")
    val content: String,

    // cover url
    @SerialName("image")
    val image: String,
) : SegmentData()

@Serializable
data class IDTag(
    @SerialName("id")
    val id: String
) : SegmentData()

@Serializable
data class SingleForwardNodeData(
    @SerialName("user_id")
    val userID: String,

    @SerialName("nickname")
    val nickname: String,

    @SerialName("content")
    val content: String
) : SegmentData()

@Serializable
data class MultiForwardNodeData(
    @SerialName("id")
    val id: String,

    @SerialName("content")
    val content: List<SegmentData>
) : SegmentData()

@Serializable
data class SerializedData(
    // xml or json
    @SerialName("data")
    val data: String
) : SegmentData()