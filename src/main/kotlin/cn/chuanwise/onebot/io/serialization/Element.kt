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

package cn.chuanwise.onebot.io.serialization

import kotlin.reflect.KClass

interface Element {
}

object Null: Element {
}

interface Array: Element, Iterable<Element> {
    val size: Int
    operator fun get(index: Int): Element
}

interface Primitive: Element {
    val number: Number
    val string: String

    val bool: Boolean
        get() = string.toBoolean()

    val boolOrNull: Boolean?
        get() = string.toBooleanStrictOrNull()

    val byte: Byte
        get() = string.toByte()

    val byteOrNull: Byte?
        get() = string.toByteOrNull()

    val short: Short
        get() = string.toShort()

    val shortOrNull: Short?
        get() = string.toShortOrNull()

    val int: Int
        get() = string.toInt()

    val intOrNull: Int?
        get() = string.toIntOrNull()

    val long: Long
        get() = string.toLong()

    val longOrNull: Long?
        get() = string.toLongOrNull()

    val float: Float
        get() = string.toFloat()

    val floatOrNull: Float?
        get() = string.toFloatOrNull()

    val double: Double
        get() = string.toDouble()

    val doubleOrNull: Double?
        get() = string.toDoubleOrNull()

    val intToBool: Boolean
        get() = int != 0

    val intToBoolOrNull: Boolean?
        get() = intOrNull?.let { it != 0 }
}

interface Tree: Element {
    fun getOptionalNullable(key: Any?): Element?
    fun getNullable(key: Any?): Element = getOptionalNullable(key) ?: throw NoSuchElementException("Element with key `$key` not found")
    fun getNullableButIgnoreNull(key: Any?): Element? = getOptionalNullable(key)?.let { if (it is Null) return null else it }
    fun getNonNull(key: Any?): Element = getNullable(key).let {
        require(it !is Null) { "Element with key `$key` present but it's `null`!" }
        it
    }
    fun getOptionalNonNull(key: Any?): Element? = getOptionalNullable(key)?.let {
        require(it !is Null) { "Element with key `$key` present but it's `null`!" }
        it
    }
    fun getOptionalNullableButIgnoreNull(key: Any?): Element? = getOptionalNullable(key)?.let { if (it is Null) return null else it }
    operator fun contains(key: Any?): Boolean
    operator fun get(key: Any?): Element = getNonNull(key)
    fun <T : Any> decodeTo(decodeClass: KClass<T>): T
}

val Element.primitive: Primitive
    get() = this as Primitive

val Element.array: Array
    get() = this as Array

val Element.tree: Tree
    get() = this as Tree
