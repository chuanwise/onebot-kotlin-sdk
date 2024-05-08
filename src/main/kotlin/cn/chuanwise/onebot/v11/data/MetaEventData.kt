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
