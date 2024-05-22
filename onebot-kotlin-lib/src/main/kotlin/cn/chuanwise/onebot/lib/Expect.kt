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

import com.fasterxml.jackson.core.type.TypeReference

/**
 * Type expect, used to check sending and receiving type in compilation time.
 *
 * @author Chuanwise
 */
interface Expect<P, R> {
    val paraType: TypeReference<P>
    val respType: TypeReference<R>
}

data class Action<P, R>(
    val name: String,
    override val paraType: TypeReference<P>,
    override val respType: TypeReference<R>
) : Expect<P, R>

/**
 * Kotlin-friendly API to construct an instance of [Action].
 */
inline fun <reified P, reified R> Action(name: String) = Action(
    name, object : TypeReference<P>() {}, object : TypeReference<R>() {}
)

data class Push<P, R>(
    override val paraType: TypeReference<P>,
    override val respType: TypeReference<R>
) : Expect<P, R>

/**
 * Kotlin-friendly API to construct an instance of [Push].
 */
inline fun <reified P, reified R> Push() = Push(
    object : TypeReference<P>() {}, object : TypeReference<R>() {}
)

