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

package cn.chuanwise.onebot.io.connection

import cn.chuanwise.onebot.io.data.Codec
import com.fasterxml.jackson.databind.JsonNode

sealed interface ConnectionConfiguration {
    var responseTimeout: Long?
    var heartBeatInterval: Long
    var accessToken: String?
    var codec: Codec

    var ignoreProvidedEmptyAccessToken: Boolean
    var ignoreConfiguredEmptyAccessToken: Boolean
    var allowNonPrefixAccessTokenHeader: Boolean
    var allowAccessTokenProvidedIfAbsent: Boolean
    var allowDifferentAccessTokenIfAbsent: Boolean
    var allowDifferentAccessTokenIfPresent: Boolean
    var allowFormatErrorWhenMultiAccessTokenProvided: Boolean
    var allowMultiSameAccessTokenProvided: Boolean
}

/**
 * Connection is a channel that can send and receive data.
 *
 * @author Chuanwise
 */
interface Connection : AutoCloseable {
    val configuration: ConnectionConfiguration
    val established: Boolean
    suspend fun await(): Connection
}

interface ConnectionAPI : AutoCloseable {
    val connection: Connection
    suspend fun sendAndWait(node: JsonNode): JsonNode
}