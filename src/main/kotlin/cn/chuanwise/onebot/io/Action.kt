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

package cn.chuanwise.onebot.io

import cn.chuanwise.onebot.v11.io.OneBot11Action
import com.fasterxml.jackson.core.type.TypeReference

/**
 * # Action
 *
 * Action represents an action that can be performed by the OneBot implementation.
 *
 * @author Chuanwise
 * @see OneBot11Action
 */
interface Action<P, R> {
    val name: String
    val paraTypeReference: TypeReference<P>
    val respTypeReference: TypeReference<R>
}