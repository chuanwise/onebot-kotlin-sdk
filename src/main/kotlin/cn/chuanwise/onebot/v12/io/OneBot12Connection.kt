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

package cn.chuanwise.onebot.v12.io

import cn.chuanwise.onebot.v12.io.data.SelfData

/**
 * OneBot 12 connection provides a higher level abstraction for the connection between
 * applications and OneBot implementations.
 *
 * @author Chuanwise
 */
sealed interface OneBot12Connection : AutoCloseable {
    /**
     * Request the OneBot implementation to perform an action.
     *
     * @param action Action name.
     * @param params Parameters for the action.
     * @param self Self identifier.
     */
    suspend fun request(action: String, params: Any? = null, self: SelfData? = null): String
}