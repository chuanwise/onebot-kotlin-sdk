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

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.double
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.float
import kotlinx.serialization.json.floatOrNull
import kotlinx.serialization.json.int
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.long
import kotlinx.serialization.json.longOrNull

fun JsonElement.adapter(): Element = when (this) {
    is JsonNull -> Null
    is JsonPrimitive -> JSONPrimitive(this)
    is JsonObject -> JSONObject(this)
    is JsonArray -> JSONArray(this)
    else -> throw IllegalStateException("Unexpected JSON element: $this")
}

class JSONPrimitive(private val json: JsonPrimitive): Primitive {
    override val string: String
        get() = json.content

    override val intOrNull: Int?
        get() = json.intOrNull

    override val longOrNull: Long?
        get() = json.longOrNull

    override val floatOrNull: Float?
        get() = json.floatOrNull

    override val doubleOrNull: Double?
        get() = json.doubleOrNull

    override val boolOrNull: Boolean?
        get() = json.booleanOrNull

    override val bool: Boolean
        get() = json.boolean

    override val int: Int
        get() = json.int

    override val long: Long
        get() = json.long

    override val float: Float
        get() = json.float

    override val double: Double
        get() = json.double

    override val intToBool: Boolean
        get() = int == 1

    override val intToBoolOrNull: Boolean?
        get() = intOrNull?.let { it != 0 }
}

class JSONArray(private val json: JsonArray): Array {
    override val size: Int
        get() = json.size

    override fun get(index: Int): Element = when (val element = json[index]) {
        is JsonNull -> Null
        is JsonPrimitive -> JSONPrimitive(element)
        is JsonObject -> JSONObject(element)
        is JsonArray -> JSONArray(element)
        else -> throw IllegalStateException("Unexpected JSON element: $element")
    }

    override fun iterator(): Iterator<Element> = object : Iterator<Element> {
        private var index = 0

        override fun hasNext(): Boolean = index < size

        override fun next(): Element = get(index++)
    }
}

class JSONObject(private val json: JsonObject): Tree {
    override fun getNullable(key: String): Element {
        val element = json[key]
        requireNotNull(element) { "Element with key `$key` not found" }
        return when (element) {
            is JsonNull -> Null
            is JsonPrimitive -> JSONPrimitive(element)
            is JsonObject -> JSONObject(element)
            is JsonArray -> JSONArray(element)
            else -> throw IllegalStateException("Unexpected JSON element: $element")
        }
    }

    override fun getNullableButIgnoreNull(key: String): Element? = getNullable(key).let { if (it is Null) return null else it }

    override fun getOptionalNullable(key: String): Element? = json[key]?.let {
        when (it) {
            is JsonNull -> Null
            is JsonPrimitive -> JSONPrimitive(it)
            is JsonObject -> JSONObject(it)
            is JsonArray -> JSONArray(it)
            else -> throw IllegalStateException("Unexpected JSON element: $it")
        }
    }

    override fun getNonNull(key: String): Element = getNullable(key).apply {
        require(this !is Null) { "Element with key `$key` present but it's `null`!" }
    }

    override fun getOptionalNonNull(key: String): Element? = getOptionalNullable(key)?.apply {
        require(this !is Null) { "Element with key `$key` present but it's `null`!" }
    }

    override fun getOptionalNullableButIgnoreNull(key: String): Element? = getOptionalNullable(key)?.let { if (it is Null) return null else it }

    override fun contains(key: String): Boolean = key in json
}