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

@file:JvmName("MetaEventDataSerializations")
package cn.chuanwise.onebot.v11.io.serialization

import cn.chuanwise.onebot.serialization.Tree
import cn.chuanwise.onebot.serialization.primitive
import cn.chuanwise.onebot.serialization.tree
import cn.chuanwise.onebot.v11.io.data.HeartbeatEventData
import cn.chuanwise.onebot.v11.io.data.LifecycleMetaEventData
import cn.chuanwise.onebot.v11.io.data.MetaEventData

fun Tree.toMetaEvent(): MetaEventData {
    val time = get(TIME).primitive.long
    val selfID = get(SELF_ID).primitive.long

    return when (val subType = get(META_EVENT_TYPE).primitive.string) {
        LIFECYCLE -> LifecycleMetaEventData(
            time = time,
            selfID = selfID,
            postType = META_EVENT,
            metaEventType = LIFECYCLE,
            subType = subType,
        )
        HEARTBEAT -> HeartbeatEventData(
            time = time,
            selfID = selfID,
            postType = META_EVENT,
            metaEventType = HEARTBEAT,
            status = getOptionalNullableButIgnoreNull(STATUS)?.tree,
            interval = get(INTERVAL).primitive.long
        )
        else -> throw IllegalArgumentException("Unexpected sub type: $subType")
    }
}