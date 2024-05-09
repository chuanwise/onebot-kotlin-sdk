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

import com.fasterxml.jackson.databind.ObjectMapper
import kotlin.reflect.KClass

fun Any?.toPlainElement(): Element = when (this) {
    null -> Null
    is Short, Int, Long, Float, Double, Boolean, Char -> PlainPrimitiveNumber(this as Number)
    is String -> PlainPrimitiveString(this)
    is Map<*, *> -> PlainTree(this)
    is List<*> -> PlainArray(this)
    else -> throw IllegalStateException("Unexpected JSON element: $this")
}

class PlainPrimitiveString(private val value: String): Primitive {
    override val number: Number
        get() = if ("." in value) value.toDouble() else value.toLong()
    override val string: String
        get() = value
}

class PlainPrimitiveNumber(private val value: Number): Primitive {
    override val number: Number
        get() = value
    override val string: String
        get() = value.toString()
}

class PlainArray(private val list: List<*>): Array {
    override val size: Int
        get() = list.size

    override fun get(index: Int): Element = list[index].toPlainElement()

    override fun iterator(): Iterator<Element> = object : Iterator<Element> {
        private var index = 0

        override fun hasNext(): Boolean = index < size

        override fun next(): Element = get(index++)
    }
}

class PlainTree(private val map: Map<*, *>): Tree {
    override fun getOptionalNullable(key: Any?): Element? = map[key]?.toPlainElement()

    override fun contains(key: Any?): Boolean = key in map
    override fun <T : Any> decodeTo(decodeClass: KClass<T>): T {
        return ObjectMapper().findAndRegisterModules().convertValue(map, decodeClass.java)
    }
}