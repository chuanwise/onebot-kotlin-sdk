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

@file:JvmName("OneBot11AppIncomingChannels")

package cn.chuanwise.onebot.lib.v11

import cn.chuanwise.onebot.lib.Event
import cn.chuanwise.onebot.lib.v11.data.OneBot11ToAppPack
import cn.chuanwise.onebot.lib.v11.data.event.EventData
import cn.chuanwise.onebot.lib.v11.data.event.QuickOperationData

interface OneBot11AppIncomingChannel : OneBot11IncomingChannel<EventData, QuickOperationData>

inline fun <reified T : OneBot11ToAppPack> OneBot11AppIncomingChannel.registerListener(
    event: Event<T, Unit>, crossinline action: suspend (T) -> Unit
) = registerListener {
    if (T::class.isInstance(it)) {
        action(it as T)
    }
    null
}

inline fun <reified T : OneBot11ToAppPack, R : QuickOperationData> OneBot11AppIncomingChannel.registerListenerWithoutQuickOperation(
    event: Event<T, R>, crossinline action: suspend (T) -> Unit
) = registerListener {
    if (T::class.isInstance(it)) {
        action(it as T)
    }
    null
}

inline fun <reified T : OneBot11ToAppPack, R : QuickOperationData> OneBot11AppIncomingChannel.registerListenerWithQuickOperation(
    event: Event<T, R>, crossinline action: suspend (T) -> R?
) = registerListener {
    if (T::class.isInstance(it)) {
        return@registerListener action(it as T)
    } else {
        null
    }
}
