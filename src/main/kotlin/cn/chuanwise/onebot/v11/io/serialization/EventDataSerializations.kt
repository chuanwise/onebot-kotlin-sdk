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

@file:JvmName("EventDataSerializations")
package cn.chuanwise.onebot.v11.io.serialization

import cn.chuanwise.onebot.serialization.Tree
import cn.chuanwise.onebot.serialization.primitive
import cn.chuanwise.onebot.v11.io.data.EventData

fun Tree.toEventData(): EventData = when (val postType = get(POST_TYPE).primitive.string) {
    MESSAGE -> toMessageEvent()
    NOTICE -> toNoticeEvent()
    REQUEST -> toRequestEvent()
    META_EVENT -> toMetaEvent()
    else -> throw IllegalArgumentException("Unknown post type: $postType")
}
