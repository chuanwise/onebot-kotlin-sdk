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

import cn.chuanwise.onebot.io.data.JSON
import cn.chuanwise.onebot.io.data.Serialization


class ReverseWebSocketConnectionConfiguration(
    var host: String,
    var port: Int,
    var path: String,
    override var accessToken: String?,
    override var serialization: Serialization = JSON,
    override var heartBeatInterval: Long = DEFAULT_HEARTBEAT_INTERVAL,
    override var responseTimeout: Long? = DEFAULT_RESPONSE_TIMEOUT,
    override var allowNonPrefixAccessTokenHeader: Boolean = DEFAULT_ALLOW_NON_PREFIX_ACCESS_TOKEN_HEADER,
    override var allowDifferentAccessTokenIfAbsent: Boolean = DEFAULT_ALLOW_DIFFERENT_ACCESS_TOKEN_IF_ABSENT,
    override var allowAccessTokenProvidedIfAbsent: Boolean = DEFAULT_ALLOW_ACCESS_TOKEN_PROVIDED_IF_ABSENT,
    override var allowMultiSameAccessTokenProvided: Boolean = DEFAULT_ALLOW_MULTI_SAME_ACCESS_TOKEN_PROVIDED,
    override var ignoreConfiguredEmptyAccessToken: Boolean = DEFAULT_IGNORE_CONFIGURED_EMPTY_ACCESS_TOKEN,
    override var ignoreProvidedEmptyAccessToken: Boolean = DEFAULT_IGNORE_PROVIDED_EMPTY_ACCESS_TOKEN,
    override var allowDifferentAccessTokenIfPresent: Boolean = DEFAULT_ALLOW_DIFFERENT_ACCESS_TOKEN_IF_PRESENT,
    override var allowFormatErrorWhenMultiAccessTokenProvided: Boolean = DEFAULT_ALLOW_FORMAT_ERROR_WHEN_MULTI_ACCESS_TOKEN_PROVIDED
) : ConnectionConfiguration
