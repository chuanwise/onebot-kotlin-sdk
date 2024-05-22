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

package cn.chuanwise.onebot.lib

import io.github.oshai.kotlinlogging.KLogger

enum class AuthReceipt {
    SUCCESS,
    FORMAT_ERROR,
    REQUIRED,
    TOKEN_ERROR
}

fun auth(accessToken: String?, logger: KLogger, header: String?, query: String?, address: String): AuthReceipt {
    if (accessToken.isNullOrEmpty()) {
        logger.warn {
            "Connection from $address is accepted, " +
                    "notice that access token is not required, any connections will be accepted."
        }
        return AuthReceipt.SUCCESS
    }

    if (!header.isNullOrEmpty()) {
        if (header.startsWith(BEARER)) {
            if (header.length > BEARER.length) {
                if (header[BEARER.length] == ' ') {
                    // header starts with BEARER_WITH_SPACE
                    val accessTokenFromHeader = header.substring(BEARER_WITH_SPACE.length)
                    if (accessTokenFromHeader.isEmpty()) {
                        logger.warn { "Connection from $address is rejected. Although header `$AUTHORIZATION` is set, it doesn't provide an access token." }
                        return AuthReceipt.REQUIRED
                    } else if (accessTokenFromHeader == accessToken) {
                        logger.info { "Connection from $address is accepted, because access token from header `$AUTHORIZATION` is correct." }
                        return AuthReceipt.SUCCESS
                    } else {
                        logger.warn { "Connection from $address is rejected, because access token from header `$AUTHORIZATION` is incorrect." }
                        return AuthReceipt.TOKEN_ERROR
                    }
                } else {
                    // header doesn't start with BEARER_WITH_SPACE
                    logger.warn {
                        "Connection from $address is rejected, " +
                                "because the format of header `$AUTHORIZATION` is wrong: `$header`, which should starts with `$BEARER_WITH_SPACE`."
                    }
                    return AuthReceipt.FORMAT_ERROR
                }
            } else {
                // header is BEARER
                logger.warn { "Connection from $address is rejected. Although header `$AUTHORIZATION` is set, it doesn't provide an access token." }
                return AuthReceipt.REQUIRED
            }
        } else {
            // header doesn't start with BEARER
            logger.warn {
                "Connection from $address is rejected, " +
                        "because the format of header `$AUTHORIZATION` is wrong: `$header`, which should starts with `$BEARER_WITH_SPACE`."
            }
            return AuthReceipt.FORMAT_ERROR
        }
    }
    if (!query.isNullOrEmpty()) {
        if (query == accessToken) {
            logger.info { "Connection from $address is accepted, because access token from query parameter `$ACCESS_TOKEN` is correct." }
            return AuthReceipt.SUCCESS
        } else {
            logger.warn { "Connection from $address is rejected, because access token from query parameter `$ACCESS_TOKEN` is incorrect." }
            return AuthReceipt.TOKEN_ERROR
        }
    }

    logger.warn {
        "Connection from $address is rejected, because access token is required, " +
                "but both header `$AUTHORIZATION` and query parameter `$ACCESS_TOKEN` are not set or set to empty string."
    }
    return AuthReceipt.REQUIRED
}