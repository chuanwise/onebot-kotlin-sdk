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

package cn.chuanwise.onebot.v11.io.data.action

import cn.chuanwise.onebot.io.data.JacksonObject
import cn.chuanwise.onebot.io.data.toPrimitive
import cn.chuanwise.onebot.v11.io.data.ACTION
import cn.chuanwise.onebot.v11.io.data.DATA
import cn.chuanwise.onebot.v11.io.data.ECHO
import cn.chuanwise.onebot.v11.io.data.IncomingData
import cn.chuanwise.onebot.v11.io.data.PARAMS
import cn.chuanwise.onebot.v11.io.data.RETCODE
import cn.chuanwise.onebot.v11.io.data.STATUS
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.node.ObjectNode


data class ActionRequestPacket<T>(
    @JsonProperty(ACTION)
    val action: String,

    @JsonProperty(PARAMS)
    val params: T? = null,

    @JsonProperty(ECHO)
    val echo: String? = null,
) : IncomingData

@JsonDeserialize(using = ResponseDataDeserializer::class)
data class ResponseData<T>(
    @JsonProperty(STATUS)
    val status: String,

    @JsonProperty(RETCODE)
    val returnCode: Int,

    @JsonProperty(DATA)
    val data: T? = null,

    @JsonProperty(ECHO)
    val echo: String? = null
) : IncomingData

object ResponseDataDeserializer : StdDeserializer<ResponseData<*>>(ResponseData::class.java) {
    private fun readResolve(): Any = ResponseDataDeserializer

    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): ResponseData<*> {
        val node = p.codec.readTree<ObjectNode>(p)
        val value = JacksonObject(p.codec as ObjectMapper, node)

        val status = value[STATUS].toPrimitive().toString()
        val retCode = value[RETCODE].toPrimitive().toInt()
        val echo = value.getOptionalNullable(ECHO)?.toPrimitive()?.toString()
        val data = value.getOptionalNullable(DATA)

        return ResponseData(status, retCode, data, echo)
    }
}