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

package cn.chuanwise.onebot.lib.v11.data.action

import cn.chuanwise.onebot.lib.DATA
import cn.chuanwise.onebot.lib.ECHO
import cn.chuanwise.onebot.lib.RETCODE
import cn.chuanwise.onebot.lib.STATUS
import cn.chuanwise.onebot.lib.getNotNull
import cn.chuanwise.onebot.lib.getOptionalNullable
import cn.chuanwise.onebot.lib.v11.data.OneBot11ToAppPack
import cn.chuanwise.onebot.lib.v11.data.OneBot11ToImplPack
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.node.ObjectNode


data class ActionRequestPack<T>(
    val action: String,
    val params: T? = null,
    val echo: String? = null,
) : OneBot11ToImplPack


data class ResponseData<T>(
    val status: String,

    @JsonProperty(RETCODE)
    val retCode: Int,

    val data: T? = null,
    val echo: String? = null
) : OneBot11ToAppPack


object ResponseDataDeserializer : StdDeserializer<ResponseData<*>>(ResponseData::class.java) {
    private fun readResolve(): Any = ResponseDataDeserializer

    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): ResponseData<*> {
        val node = p.readValueAsTree<ObjectNode>()

        val status = node.getNotNull(STATUS).asText()
        val retCode = node.getNotNull(RETCODE).asInt()
        val echo = node.getOptionalNullable(ECHO)?.asText()
        val data = node.getOptionalNullable(DATA)

        return ResponseData(status, retCode, data, echo)
    }
}