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

package cn.chuanwise.onebot.io.data

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.convertValue
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

fun Any?.toNullablePlainValue(): Value = when (this) {
    null -> Null
    else -> toNotNullPlainValue()
}

fun Any.toNotNullPlainValue(): Value = when (this) {
    is Short, is Int, is Long, is Float, is Double, is Char -> PlainPrimitiveNumber(this as Number)
    is Boolean -> PlainPrimitiveBoolean.of(this)
    is String -> PlainPrimitiveString.of(this)
    is Map<*, *> -> PlainObject(this)
    is List<*> -> PlainArray(this)
    is PlainPrimitiveBoolean -> this
    is PlainPrimitiveString -> this
    is PlainPrimitiveNumber -> this
    is PlainArray -> this
    is PlainObject -> this
    else -> PlainObject(jacksonObjectMapper().convertValue<Map<String, Any?>>(this))
}


class PlainPrimitiveBoolean private constructor(override val value: Boolean) : Primitive {
    companion object {
        private val TRUE = PlainPrimitiveBoolean(true)
        private val FALSE = PlainPrimitiveBoolean(false)

        fun of(value: Boolean): PlainPrimitiveBoolean = if (value) TRUE else FALSE
    }

    override fun toNumber(): Number = if (value) 1 else 0
    override fun toNumberOrNull(): Number = toNumber()
    override fun toBoolean(): Boolean = value
    override fun toBooleanOrNull(): Boolean = value
    override fun toString(): String = value.toString()
}

class PlainPrimitiveString private constructor(override val value: String) : Primitive {
    companion object {
        private val EMPTY = PlainPrimitiveString("")

        fun of(value: String): PlainPrimitiveString = if (value.isEmpty()) EMPTY else PlainPrimitiveString(value)
    }

    override fun toNumber(): Number = if ("." in value) value.toDouble() else value.toLong()
    override fun toNumberOrNull(): Number? = try {
        toNumber()
    } catch (exception: NumberFormatException) {
        null
    }

    override fun toBoolean(): Boolean = value.toBoolean()
    override fun toBooleanOrNull(): Boolean? = value.toBooleanStrictOrNull()
    override fun toString(): String = value
}

data class PlainPrimitiveNumber(override val value: Number) : Primitive {
    override fun toNumber(): Number = value
    override fun toNumberOrNull(): Number = value
    override fun toBoolean(): Boolean = value.toInt() != 0
    override fun toBooleanOrNull(): Boolean = value.toInt() != 0

    override fun toString(): String = value.toString()
}

data class PlainArray(private val list: List<*>) : Array {
    override val size: Int
        get() = list.size

    override fun get(index: Int): Value = list[index].toNullablePlainValue()

    override fun iterator(): Iterator<Value> = object : Iterator<Value> {
        private var index = 0

        override fun hasNext(): Boolean = index < size

        override fun next(): Value = get(index++)
    }

    override fun <T> deserializeTo(typeReference: TypeReference<T>): T {
        return jacksonObjectMapper().convertValue(list, typeReference)
    }
}

data class PlainObject(private val value: Map<*, *>) : Object {
    override fun getOptionalNullable(key: Any?): Value? = value[key]?.toNotNullPlainValue()

    override fun contains(key: Any?): Boolean = key in value
    override fun <T> deserializeTo(typeReference: TypeReference<T>): T {
        return jacksonObjectMapper().convertValue(value, typeReference)
    }

    data class Pair(
        override val key: Any?,
        override val value: Value
    ) : Map.Entry<Any?, Value>

    override val entries: Set<Map.Entry<Any?, Value>> = value.entries.map {
        Pair(it.key, it.value.toNullablePlainValue())
    }.toSet()

    override val keys: Set<Any?> = value.keys
    override val size: Int = value.size
    override val values: Collection<Value> = value.values.map { it.toNullablePlainValue() }
    override fun containsKey(key: Any?): Boolean = key in value
    override fun containsValue(value: Value): Boolean = value in values

    override fun isEmpty(): Boolean = value.isEmpty()
}
