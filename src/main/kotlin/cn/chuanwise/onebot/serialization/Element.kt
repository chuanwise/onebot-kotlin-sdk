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

package cn.chuanwise.onebot.serialization

interface Element {
}

object Null: Element {
}

interface Array: Element, Iterable<Element> {
    val size: Int
    operator fun get(index: Int): Element
}

interface Primitive: Element {
    val bool: Boolean
    val boolOrNull: Boolean?
    val int: Int
    val intOrNull: Int?
    val long: Long
    val longOrNull: Long?
    val string: String
    val float: Float
    val floatOrNull: Float?
    val double: Double
    val doubleOrNull: Double?
    val intToBool: Boolean
    val intToBoolOrNull: Boolean?
}

interface Tree: Element {
    fun getNullable(key: String): Element
    fun getNullableButIgnoreNull(key: String): Element?
    fun getOptionalNullable(key: String): Element?
    fun getNonNull(key: String): Element
    fun getOptionalNonNull(key: String): Element?
    fun getOptionalNullableButIgnoreNull(key: String): Element?
    operator fun contains(key: String): Boolean
    operator fun get(key: String): Element = getNonNull(key)
}

val Element.primitive: Primitive
    get() = this as Primitive

val Element.array: Array
    get() = this as Array

val Element.tree: Tree
    get() = this as Tree
