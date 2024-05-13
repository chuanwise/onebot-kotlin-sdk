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

package cn.chuanwise.onebot.v11

import cn.chuanwise.onebot.OneBot
import cn.chuanwise.onebot.v11.io.api.OneBot11API
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class OneBot11(
    override val configuration: OneBot11Configuration,
) : OneBot {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    override val coroutineContext: CoroutineContext = scope.coroutineContext

    private lateinit var api: OneBot11API

    init {
        TODO("Init api")
    }
}