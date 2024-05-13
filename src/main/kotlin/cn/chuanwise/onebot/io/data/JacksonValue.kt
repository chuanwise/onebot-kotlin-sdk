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
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.BigIntegerNode
import com.fasterxml.jackson.databind.node.BinaryNode
import com.fasterxml.jackson.databind.node.BooleanNode
import com.fasterxml.jackson.databind.node.DecimalNode
import com.fasterxml.jackson.databind.node.DoubleNode
import com.fasterxml.jackson.databind.node.FloatNode
import com.fasterxml.jackson.databind.node.IntNode
import com.fasterxml.jackson.databind.node.LongNode
import com.fasterxml.jackson.databind.node.NullNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.ShortNode
import com.fasterxml.jackson.databind.node.TextNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

private fun JsonNode.toJacksonValue(mapper: ObjectMapper = jacksonObjectMapper()): Value = when (this) {
    is ArrayNode -> JacksonArray(mapper, this)
    is ObjectNode -> JacksonObject(mapper, this)
    is BigIntegerNode -> PlainPrimitiveNumber(this.bigIntegerValue())
    is BinaryNode -> PlainPrimitiveBoolean.of(this.booleanValue())
    is BooleanNode -> PlainPrimitiveBoolean.of(this.booleanValue())
    is DecimalNode -> PlainPrimitiveNumber(this.decimalValue())
    is DoubleNode -> PlainPrimitiveNumber(this.doubleValue())
    is FloatNode -> PlainPrimitiveNumber(this.floatValue())
    is IntNode -> PlainPrimitiveNumber(this.intValue())
    is LongNode -> PlainPrimitiveNumber(this.longValue())
    is NullNode -> Null
    is ShortNode -> PlainPrimitiveNumber(this.shortValue())
    is TextNode -> PlainPrimitiveString.of(this.textValue())
    else -> throw IllegalArgumentException("Unsupported JsonNode type: ${this.javaClass}")
}

data class JacksonArray(
    private val mapper: ObjectMapper,
    private val node: ArrayNode
) : Array {
    override val size: Int
        get() = node.size()

    override fun get(index: Int): Value = node[index].toJacksonValue(mapper)
    override fun <T> deserializeTo(typeReference: TypeReference<T>): T {
        return mapper.treeToValue(node, typeReference)
    }

    override fun iterator(): Iterator<Value> = object : Iterator<Value> {
        private var index = 0

        override fun hasNext(): Boolean = index < size

        override fun next(): Value {
            if (!hasNext()) {
                throw NoSuchElementException()
            }
            return get(index++)
        }
    }
}

data class JacksonObject(
    private val mapper: ObjectMapper,
    private val node: ObjectNode
) : Object {
    override fun getOptionalNullable(key: Any?): Value? {
        val keyString = key.toString()

        val value = node.get(keyString)
        if (value == null && !node.has(keyString)) {
            return null
        }
        return value.toJacksonValue(mapper)
    }

    override fun contains(key: Any?): Boolean = node.has(key.toString())

    override fun <T> deserializeTo(typeReference: TypeReference<T>): T {
        return mapper.treeToValue(node, typeReference)
    }

    data class Pair(
        override val key: Any?,
        override val value: Value
    ) : Map.Entry<Any?, Value>

    override val entries: Set<Map.Entry<Any?, Value>> = node.fields().asSequence().map {
        Pair(it.key, it.value.toJacksonValue(mapper))
    }.toSet()

    override val keys: Set<Any?> = node.fieldNames().asSequence().toSet()
    override val size: Int = node.size()
    override val values: Collection<Value> = node.fields().asSequence().map {
        it.value.toJacksonValue(mapper)
    }.toList()

    override fun containsKey(key: Any?): Boolean = node.has(key.toString())

    override fun containsValue(value: Value): Boolean = values.contains(value)

    override fun isEmpty(): Boolean = node.isEmpty
}
