package cn.chuanwise.onebot.v11.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * # Meta Event Data
 *
 * Meta event low-level representation based on
 * [OneBot 11](https://github.com/botuniverse/onebot-11/blob/master/event/meta.md).
 */
@Serializable
sealed class MetaEventData: EventData() {
    @SerialName("meta_event_type")
    abstract val metaEventType: String
}

@Serializable
data class LifecycleMetaEventData(
    override val time: Long,
    override val selfID: Long,

    // "meta_event"
    override val postType: String,

    // "lifecycle"
    override val metaEventType: String,

    // "enable", "disable" or "connect"
    @SerialName("sub_type")
    val subType: String
): MetaEventData()

@Serializable
class StatusData(
)

@Serializable
data class HeartbeatEventData(
    override val time: Long,
    override val selfID: Long,

    // "meta_event"
    override val postType: String,

    // "heartbeat"
    override val metaEventType: String,

    @SerialName("status")
    val status: StatusData,

    @SerialName("interval")
    val interval: Long
): MetaEventData()
