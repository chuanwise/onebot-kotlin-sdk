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

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue

interface Serialization {
    fun serialize(value: Value): String
    fun deserialize(string: String): Value
}

object JSON : Serialization {
    private val mapper = jacksonObjectMapper()

    override fun serialize(value: Value): String {
        if (value is Literal) {
            return mapper.writeValueAsString(value)
        } else {
            return mapper.writeValueAsString(value.toNotNullPlainValue())
        }
    }

    override fun deserialize(string: String): Value {
        return mapper.readValue<Map<String, *>>(string).toNullablePlainValue()
    }
}