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

@file:JvmName("Authorizations")

package cn.chuanwise.onebot.io

import cn.chuanwise.onebot.io.connection.ACCESS_TOKEN
import cn.chuanwise.onebot.io.connection.AUTHORIZATION
import cn.chuanwise.onebot.io.connection.BEARER
import cn.chuanwise.onebot.io.connection.BEARER_WITH_SPACE
import cn.chuanwise.onebot.io.connection.ConnectionConfiguration

enum class AuthorizationReceiptType {
    VALID,
    INVALID,
    FORMAT_ERROR,
    PROVIDE_REQUIRED,
}

data class AuthorizationReceipt(
    val type: AuthorizationReceiptType,
    val warning: String? = null,
    val accessToken: String? = null
)

private fun Boolean.toAcceptOrReject() = if (this) "accepted" else "rejected"
private fun Boolean.toValidOrInvalid() = if (this) AuthorizationReceiptType.VALID else AuthorizationReceiptType.INVALID
private fun String.toEmptyStringOrQuotedValue() = if (isEmpty()) "empty string" else "`$this`"

fun isValidAccessTokenFromHeader(
    configuration: ConnectionConfiguration,
    header: String?
): AuthorizationReceipt {

    val accessTokenOrNull = configuration.accessToken?.let {
        if (configuration.ignoreConfiguredEmptyAccessToken && it.isEmpty()) {
            return@let null
        } else {
            it
        }
    }

    if (accessTokenOrNull === null) {
        if (header === null) {
            // 1. if the access token is not provided, and the configuration allows it, then it is valid.
            return AuthorizationReceipt(AuthorizationReceiptType.VALID, accessToken = null)
        } else if (header.startsWith(BEARER)) {
            // 2. if the header starts with `Bearer`.
            if (header.length >= BEARER.length + 1 && header[BEARER.length] == ' ') {
                // 2.1. if the header starts with `Bearer `.
                val accessTokenFromHeader = header.removePrefix(BEARER_WITH_SPACE)
                return if (accessTokenFromHeader.isEmpty() && configuration.ignoreProvidedEmptyAccessToken) {
                    // 2.1.1. if the header is empty, and can not be ignored.
                    AuthorizationReceipt(AuthorizationReceiptType.VALID, accessToken = null)
                } else {
                    // 2.1.2. if the header is not empty, which is allowed.
                    val accepted = configuration.allowAccessTokenProvidedIfAbsent
                    AuthorizationReceipt(
                        accepted.toValidOrInvalid(),
                        "The access token is not required, " +
                                "but it is set to ${accessTokenFromHeader.toEmptyStringOrQuotedValue()} " +
                                "in header `$AUTHORIZATION`. " +
                                "Notice that because `allowAccessTokenProvidedIfAbsent` " +
                                "is ${configuration.allowAccessTokenProvidedIfAbsent}, " +
                                "connection ${accepted.toAcceptOrReject()}.",
                        accessToken = accessTokenFromHeader,
                    )
                }
            } else if (header == BEARER) {
                // 2.2. if the header is just `Bearer`.
                val accepted = configuration.ignoreProvidedEmptyAccessToken
                return AuthorizationReceipt(
                    accepted.toValidOrInvalid(),
                    "The access token is not required, " +
                            "but it is set to empty string in header `$AUTHORIZATION`. " +
                            "Notice that because `ignoreEmptyAccessToken` is " +
                            "${configuration.ignoreProvidedEmptyAccessToken}, connection ${accepted.toAcceptOrReject()}.",
                    accessToken = if (accepted) null else ""
                )
            }
        }

        return if (configuration.allowNonPrefixAccessTokenHeader) {
            // 3. if the header does not start with `Bearer`, but it is allowed.
            val accepted = configuration.allowAccessTokenProvidedIfAbsent
            AuthorizationReceipt(
                accepted.toValidOrInvalid(),
                "The access token is not required, " +
                        "but header `$AUTHORIZATION` is set to " +
                        "${header.toEmptyStringOrQuotedValue()}, which is not start with `$BEARER_WITH_SPACE` " +
                        "but `allowNonPrefixAccessTokenHeader` is true, so it is the an access token. " +
                        "Notice that because `allowAccessTokenProvidedIfAbsent` is " +
                        "${configuration.allowAccessTokenProvidedIfAbsent}, " +
                        "connection ${accepted.toAcceptOrReject()}.", accessToken = header
            )
        } else {
            // 4. if the header does not start with `Bearer`, and it is not allowed.
            AuthorizationReceipt(
                AuthorizationReceiptType.FORMAT_ERROR,
                "The access token is not required, " +
                        "but header `$AUTHORIZATION` is set to " +
                        "${header.toEmptyStringOrQuotedValue()}, which is not start with `$BEARER_WITH_SPACE`. " +
                        "Notice that because `allowNonPrefixAccessTokenHeader` is false, " +
                        "the format of header is error, " +
                        "connection rejected.", accessToken = header
            )
        }
    } else {
        if (header === null) {
            // 1. if the access token is not provided, and the configuration allows it, then it is valid.
            return AuthorizationReceipt(
                AuthorizationReceiptType.PROVIDE_REQUIRED,
                "The access token is required, " +
                        "but it is not set in header `$AUTHORIZATION`. " +
                        "Notice that because `ignoreConfiguredEmptyAccessToken` is " +
                        "${configuration.ignoreConfiguredEmptyAccessToken}, " +
                        "connection rejected.", accessToken = null
            )
        } else if (header.startsWith(BEARER)) {
            // 2. if the header starts with `Bearer`.
            if (header.length >= BEARER.length + 1 && header[BEARER.length] == ' ') {
                // 2.1. if the header starts with `Bearer `.
                val accessTokenFromHeader = header.removePrefix(BEARER_WITH_SPACE)
                return if (accessTokenFromHeader.isEmpty() && configuration.ignoreProvidedEmptyAccessToken) {
                    // 2.1.1. if the header is empty, and can be ignored.
                    AuthorizationReceipt(
                        AuthorizationReceiptType.PROVIDE_REQUIRED,
                        "The access token is required, " +
                                "but it is set to empty string in header `$AUTHORIZATION` " +
                                "Notice that because `ignoreProvidedEmptyAccessToken` is true, " +
                                "which makes it be ignored, so it is same with no access token provided, " +
                                "connection rejected.", accessToken = null
                    )
                } else if (accessTokenFromHeader == accessTokenOrNull) {
                    // 2.1.2. access token accepted
                    AuthorizationReceipt(AuthorizationReceiptType.VALID, accessToken = accessTokenFromHeader)
                } else {
                    // 2.1.3. access token rejected
                    AuthorizationReceipt(
                        AuthorizationReceiptType.INVALID,
                        "The access token is required, " +
                                "but it is set to ${accessTokenFromHeader.toEmptyStringOrQuotedValue()} " +
                                "in header `$AUTHORIZATION`, which is different from the configured access token. " +
                                "connection rejected.", accessToken = accessTokenFromHeader
                    )
                }
            } else if (header == BEARER) {
                // 2.2. if the header is just `Bearer`
                if (configuration.ignoreProvidedEmptyAccessToken) {
                    // 2.2.1. if the header is empty, and can be ignored.
                    AuthorizationReceipt(
                        AuthorizationReceiptType.PROVIDE_REQUIRED,
                        "The access token is required, " +
                                "but it is set to empty string in header `$AUTHORIZATION` " +
                                "Notice that because `ignoreProvidedEmptyAccessToken` is true, " +
                                "which makes it be ignored, so it is same with no access token provided, " +
                                "connection rejected.", accessToken = null
                    )
                } else if (accessTokenOrNull.isEmpty()) {
                    // 2.2.2. access token accepted.
                    AuthorizationReceipt(
                        AuthorizationReceiptType.VALID, accessToken = ""
                    )
                } else {
                    // 2.2.3. access token rejected.
                    AuthorizationReceipt(
                        AuthorizationReceiptType.INVALID,
                        "The access token is required, " +
                                "but it is set to empty string in header `$AUTHORIZATION`, " +
                                "which is different from the configured access token. " +
                                "connection rejected.", accessToken = ""
                    )
                }
            }
        }

        return if (configuration.allowNonPrefixAccessTokenHeader) {
            // 3. if the header does not start with `Bearer`, but it is allowed.
            if (header == accessTokenOrNull) {
                // 2.1.2. access token accepted
                AuthorizationReceipt(
                    AuthorizationReceiptType.VALID,
                    "The access token is required, " +
                            "and it is set to ${header.toEmptyStringOrQuotedValue()} " +
                            "in header `$AUTHORIZATION`. " +
                            "Notice that because `allowNonPrefixAccessTokenHeader` is true, " +
                            "connection accepted, but it is not standard.", accessToken = header
                )
            } else {
                // 2.1.3. access token rejected
                AuthorizationReceipt(
                    AuthorizationReceiptType.INVALID,
                    "The access token is required, " +
                            "but it is set to ${header.toEmptyStringOrQuotedValue()} " +
                            "in header `$AUTHORIZATION`, which is different from the configured access token. " +
                            "Notice that because `allowNonPrefixAccessTokenHeader` is true, " +
                            "connection rejected, and it is not standard.", accessToken = header
                )
            }
        } else {
            // 4. if the header does not start with `Bearer`, and it is not allowed.
            AuthorizationReceipt(
                AuthorizationReceiptType.FORMAT_ERROR,
                "The access token is not required, " +
                        "but header `$AUTHORIZATION` is set to " +
                        "${header.toEmptyStringOrQuotedValue()}, which is not start with `$BEARER_WITH_SPACE`. " +
                        "Notice that because `allowNonPrefixAccessTokenHeader` is false, " +
                        "the format of header is error, " +
                        "connection rejected."
            )
        }
    }
}

fun isValidAccessTokenFromQuery(
    configuration: ConnectionConfiguration,
    query: String?
): AuthorizationReceipt {
    val accessTokenOrNull = configuration.accessToken?.let {
        if (configuration.ignoreConfiguredEmptyAccessToken && it.isEmpty()) {
            return@let null
        } else {
            it
        }
    }

    if (accessTokenOrNull === null) {
        return if (query === null) {
            // 1. if the access token is not provided, and the configuration allows it, then it is valid.
            AuthorizationReceipt(AuthorizationReceiptType.VALID, accessToken = null)
        } else {
            if (query.isEmpty()) {
                // 2. if the query is empty.
                val accepted = configuration.ignoreProvidedEmptyAccessToken
                AuthorizationReceipt(
                    accepted.toValidOrInvalid(),
                    "The access token is not required, " +
                            "but it is set to empty string in query `$ACCESS_TOKEN`, " +
                            "Notice that because `ignoreProvidedEmptyAccessToken` is " +
                            "${configuration.ignoreProvidedEmptyAccessToken}, " +
                            "connection ${accepted.toAcceptOrReject()}.", accessToken = if (accepted) null else ""
                )
            } else {
                // 3. if the query is not empty.
                val accepted = configuration.allowAccessTokenProvidedIfAbsent
                AuthorizationReceipt(
                    accepted.toValidOrInvalid(),
                    "The access token is not required, " +
                            "but it is set to ${query.toEmptyStringOrQuotedValue()} in query `$ACCESS_TOKEN`. " +
                            "Notice that because `allowAccessTokenProvidedIfAbsent` is " +
                            "${configuration.allowAccessTokenProvidedIfAbsent}, " +
                            "connection ${accepted.toAcceptOrReject()}.", accessToken = query
                )
            }
        }
    } else {
        if (query === null) {
            // 1. if the access token is not provided, and the configuration allows it, then it is valid.
            return AuthorizationReceipt(
                AuthorizationReceiptType.PROVIDE_REQUIRED,
                "The access token is required, " +
                        "but it is not set in query `$ACCESS_TOKEN`. " +
                        "Notice that because `ignoreConfiguredEmptyAccessToken` is " +
                        "${configuration.ignoreConfiguredEmptyAccessToken}, " +
                        "connection rejected.", accessToken = null
            )
        } else {
            if (query.isEmpty() && configuration.ignoreProvidedEmptyAccessToken) {
                // 2. if the query is empty, and can be ignored.
                AuthorizationReceipt(
                    AuthorizationReceiptType.PROVIDE_REQUIRED,
                    "The access token is required, " +
                            "but it is set to empty string in query `$ACCESS_TOKEN`. " +
                            "Notice that because `ignoreProvidedEmptyAccessToken` is true, " +
                            "which makes it be ignored, so it is same with no access token provided, " +
                            "connection rejected.", accessToken = null
                )
            }

            return if (accessTokenOrNull == query) {
                // 2.2. access token accepted.
                AuthorizationReceipt(
                    AuthorizationReceiptType.VALID, accessToken = query
                )
            } else {
                // 2.3. access token rejected.
                AuthorizationReceipt(
                    AuthorizationReceiptType.INVALID,
                    "The access token is required, " +
                            "but it is set to empty string in query `$ACCESS_TOKEN`, " +
                            "which is different from the configured access token. " +
                            "connection rejected.", accessToken = query
                )
            }
        }
    }
}
