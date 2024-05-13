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

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.ser.std.StdSerializer

/**
 * # Value
 *
 * Value is a tool to read serializable data.
 *
 * @author Chuanwise
 * @see Null
 * @see Array
 * @see Primitive
 * @see Object
 */
@JsonSerialize(using = ValueSerializer::class)
@JsonDeserialize(using = ValueDeserializer::class)
interface Value

interface Literal : Value {
    val value: Any?
}

object Null : Literal {
    override val value: Any? = null
}

interface Array : Value, Iterable<Value> {
    val size: Int
    operator fun get(index: Int): Value
    fun <T> deserializeTo(typeReference: TypeReference<T>): T
}

interface Primitive : Literal {
    fun toNumber(): Number
    fun toNumberOrNull(): Number?
    fun toBoolean(): Boolean
    fun toBooleanOrNull(): Boolean?
    fun toByte(): Byte = toNumber().toByte()
    fun toByteOrNull(): Byte? = toNumberOrNull()?.toByte()
    fun toShort(): Short = toNumber().toShort()
    fun toShortOrNull(): Short? = toNumberOrNull()?.toShort()
    fun toInt(): Int = toNumber().toInt()
    fun toIntOrNull(): Int? = toNumberOrNull()?.toInt()
    fun toLong(): Long = toNumber().toLong()
    fun toLongOrNull(): Long? = toNumberOrNull()?.toLong()
    fun toFloat(): Float = toNumber().toFloat()
    fun toFloatOrNull(): Float? = toNumberOrNull()?.toFloat()
    fun toDouble(): Double = toNumber().toDouble()
    fun toDoubleOrNull(): Double? = toNumberOrNull()?.toDouble()
    fun toChar(): Char = toString().single()
    fun toCharOrNull(): Char? = toString().singleOrNull()
    override fun toString(): String
}

interface Object : Value, Map<Any?, Value> {
    fun getOptionalNullable(key: Any?): Value?
    fun getNullable(key: Any?): Value =
        getOptionalNullable(key) ?: throw NoSuchElementException("Value with key `$key` not found")

    fun getNullableButIgnoreNull(key: Any?): Value? = getOptionalNullable(key)?.takeIf { it !is Null }
    fun getNotNull(key: Any?): Value = getNullable(key).let {
        require(it !is Null) { "Value with key `$key` present but it's `null`!" }
        it
    }

    fun getOptionalNotNull(key: Any?): Value? = getOptionalNullable(key)?.let {
        require(it !is Null) { "Value with key `$key` present but it's `null`!" }
        it
    }

    fun getOptionalNullableButIgnoreNull(key: Any?): Value? = getOptionalNullable(key)?.takeIf { it !is Null }
    operator fun contains(key: Any?): Boolean
    override operator fun get(key: Any?): Value = getNotNull(key)
    fun <T> deserializeTo(typeReference: TypeReference<T>): T
}

fun Value.toPrimitive(): Primitive = this as Primitive

fun Value.toArray(): Array = this as Array

fun Value.toObject(): Object = this as Object

inline fun <reified T> Object.deserializeTo(): T {
    return deserializeTo(object : TypeReference<T>() {})
}

object ValueSerializer : StdSerializer<Value>(Value::class.java) {
    private fun readResolve(): Any = ValueSerializer
    override fun serialize(value: Value, gen: JsonGenerator, provider: SerializerProvider) {
        when (value) {
            is Literal -> gen.writeObject(value.value)
            is Array -> {
                gen.writeStartArray()
                value.forEach {
                    serialize(it, gen, provider)
                }
                gen.writeEndArray()
            }

            is Object -> {
                gen.writeStartObject()
                value.forEach { key, value ->
                    gen.writeFieldName(key.toString())
                    serialize(value, gen, provider)
                }
                gen.writeEndObject()
            }

            else -> throw IllegalArgumentException("Unexpected value: $value")
        }
    }
}

object ValueDeserializer : StdDeserializer<Value>(Value::class.java) {
    private fun readResolve(): Any = ValueDeserializer
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Value {
        return p.codec.readTree<JsonNode>(p).toNullablePlainValue()
    }
}
