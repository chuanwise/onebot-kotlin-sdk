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

@file:JvmName("ConnectionConstants")

package cn.chuanwise.onebot.io.connection

const val DEFAULT_HEARTBEAT_INTERVAL = 5000L
const val DEFAULT_RESPONSE_TIMEOUT = 5000L

const val DEFAULT_ALLOW_NON_PREFIX_ACCESS_TOKEN_HEADER = false
const val DEFAULT_IGNORE_PROVIDED_EMPTY_ACCESS_TOKEN = true
const val DEFAULT_IGNORE_CONFIGURED_EMPTY_ACCESS_TOKEN = true
const val DEFAULT_ALLOW_ACCESS_TOKEN_PROVIDED_IF_ABSENT = false
const val DEFAULT_ALLOW_DIFFERENT_ACCESS_TOKEN_IF_ABSENT = false
const val DEFAULT_ALLOW_DIFFERENT_ACCESS_TOKEN_IF_PRESENT = false
const val DEFAULT_ALLOW_MULTI_SAME_ACCESS_TOKEN_PROVIDED = true
const val DEFAULT_ALLOW_FORMAT_ERROR_WHEN_MULTI_ACCESS_TOKEN_PROVIDED = false

const val X_SELF_ID = "X-Self-ID"
const val X_CLIENT_ROLE = "Role"
const val API = "API"
const val EVENT = "Event"
const val UNIVERSAL = "Universal"

const val AUTHORIZATION = "Authorization"
const val BEARER = "Bearer"
const val BEARER_WITH_SPACE = "Bearer "
const val ACCESS_TOKEN = "access_token"
