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

@file:JvmName("JacksonNodes")

package cn.chuanwise.onebot.lib

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.contains
import com.fasterxml.jackson.module.kotlin.convertValue
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

/**
 * Just used to convert objects to map.
 */
private val objectMapper = jacksonObjectMapper()

fun Any.toMap() = objectMapper.convertValue<Map<String, *>>(this)

fun JsonNode.getOptionalNullable(key: String) = get(key)

fun JsonNode.getOptionalNotNull(key: String) = when (this) {
    is ObjectNode -> {
        val result = get(key)
        if (result === null && contains(key)) {
            throw NullPointerException("The value of key '$key' is null.")
        }
        result
    }

    else -> throw IllegalStateException("The node is not an object node.")
}

fun JsonNode.getNullable(key: String) = when (this) {
    is ObjectNode -> {
        val result = get(key)
        if (result === null && !contains(key)) {
            throw NoSuchElementException("The value of key '$key' doesn't present.")
        }
        result
    }

    else -> throw IllegalStateException("The node is not an object node.")
}

fun JsonNode.getNotNull(key: String) =
    getOptionalNotNull(key) ?: throw NullPointerException("The value of key '$key' is null.")

fun <T> JsonNode.deserializeTo(objectMapper: ObjectMapper, type: TypeReference<T>) =
    objectMapper.treeToValue(this, type)

inline fun <reified T> JsonNode.deserializeTo(objectMapper: ObjectMapper) =
    deserializeTo(objectMapper, object : TypeReference<T>() {})
