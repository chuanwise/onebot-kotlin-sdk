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

import cn.chuanwise.onebot.io.Action
import com.fasterxml.jackson.core.type.TypeReference

class OneBot12Action<P, R>(
    override val name: String,
    override val paraTypeReference: TypeReference<P>,
    override val respTypeReference: TypeReference<R>
) : Action<P, R> {
    companion object {
        /**
         * Create a new instance for Kotlin to simply use.
         */
        inline operator fun <reified P, reified R> invoke(name: String) =
            OneBot12Action(name, object : TypeReference<P>() {}, object : TypeReference<R>() {})
    }
}
