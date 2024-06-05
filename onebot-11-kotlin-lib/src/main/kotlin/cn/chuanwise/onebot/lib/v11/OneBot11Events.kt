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

/**
 * Defined the events of [OneBot 11](https://github.com/botuniverse/onebot-11/blob/master/event).
 *
 * Each one is corresponding to an object, which allows to restrict the parameter and response type in
 * compilation time.
 *
 * @author Chuanwise
 */
@file:JvmName("OneBot11Events")

package cn.chuanwise.onebot.lib.v11

import cn.chuanwise.onebot.lib.Event
import cn.chuanwise.onebot.lib.v11.data.event.HeartbeatEventData
import cn.chuanwise.onebot.lib.v11.data.event.PrivateMessageEventData

// https://github.com/botuniverse/onebot-11/blob/master/event/meta.md
val HEARTBEAT_EVENT = Event<HeartbeatEventData>()

// https://github.com/botuniverse/onebot-11/blob/master/event/message.md#%E7%A7%81%E8%81%8A%E6%B6%88%E6%81%AF
val PRIVATE_MESSAGE_EVENT = Event<PrivateMessageEventData>()
