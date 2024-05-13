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

package cn.chuanwise.onebot.v11.io.api

import cn.chuanwise.onebot.v11.io.OneBot11Action
import cn.chuanwise.onebot.v11.io.data.message.MessageData

/**
 * # OneBot 11 API
 *
 * Defined the API of [OneBot 11](https://github.com/botuniverse/onebot-11/blob/master/api/public.md),
 * each one is corresponding to a method.
 *
 * @author Chuanwise
 */
interface OneBot11API : AutoCloseable {
    suspend fun <P : Any, R : Any> request(action: OneBot11Action<P, R>, params: P): R
    suspend fun sendGroupMessage(groupID: Long, message: MessageData): Int
}