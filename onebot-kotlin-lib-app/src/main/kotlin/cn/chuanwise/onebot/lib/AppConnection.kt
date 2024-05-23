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

package cn.chuanwise.onebot.lib


interface AppConnection : Connection {
}

// Kotlin-friendly API.
inline fun <reified T : ToAppPack> AppConnection.events(
    crossinline listener: suspend (T) -> Any?
) = packBus.registerHandler {
    return@registerHandler if (it is T) {
        listener(it)
    } else null
}

// Java-friendly API.
@Suppress("UNCHECKED_CAST")
fun <T : ToAppPack> AppConnection.events(
    eventClass: Class<T>,
    listener: suspend (T) -> Any?
) = packBus.registerHandler {
    if (eventClass.isInstance(it)) {
        listener(it as T)
    }
    null
}