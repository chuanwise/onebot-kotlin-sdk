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

package cn.chuanwise.onebot.lib.v11

import cn.chuanwise.onebot.lib.AppConnection
import cn.chuanwise.onebot.lib.Expect

interface OneBot11AppConnection : AppConnection {
    // for quick operations.
    suspend fun <P> callAsync(expect: Expect<P, *>, params: P)
    suspend fun <P> callRateLimited(expect: Expect<P, *>, params: P)
}