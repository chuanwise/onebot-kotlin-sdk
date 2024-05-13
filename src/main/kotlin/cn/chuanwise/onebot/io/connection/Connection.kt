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

import cn.chuanwise.onebot.io.AuthorizationReceiptType
import cn.chuanwise.onebot.io.data.Serialization
import cn.chuanwise.onebot.io.data.Value
import cn.chuanwise.onebot.io.isValidAccessTokenFromHeader
import cn.chuanwise.onebot.io.isValidAccessTokenFromQuery
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.plugins.origin
import io.ktor.server.response.respondText
import io.ktor.util.pipeline.PipelineContext
import java.util.*
import java.util.concurrent.ConcurrentHashMap

sealed interface ConnectionConfiguration {
    var responseTimeout: Long?
    var heartBeatInterval: Long
    var accessToken: String?
    var serialization: Serialization

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
 * # Connection
 *
 * Connection is a channel that can send and receive data.
 *
 * @author Chuanwise
 */
interface Connection : AutoCloseable {
    val configuration: ConnectionConfiguration
    val established: Boolean
    suspend fun sendValue(value: Value): Value?
    suspend fun await(): Connection
    fun registerListener(action: suspend (Value) -> Unit): UUID
    fun unregisterListener(uuid: UUID): Boolean
}

abstract class AbstractConnection : Connection {
    private val logger = KotlinLogging.logger { }
    private val listeners = ConcurrentHashMap<UUID, suspend (Value) -> Unit>()

    override fun registerListener(action: suspend (Value) -> Unit): UUID {
        val uuid = UUID.randomUUID()
        listeners[uuid] = action
        return uuid
    }

    override fun unregisterListener(uuid: UUID): Boolean {
        return listeners.remove(uuid) != null
    }

    protected suspend fun notifyListeners(value: Value) {
        listeners.values.forEach {
            it.invoke(value)
        }
    }

    protected suspend fun PipelineContext<Unit, ApplicationCall>.authorize(): Boolean {
        val address = call.request.origin.remoteAddress

        // 1. authorization
        val authorizationHeader = call.request.headers[AUTHORIZATION]
        val authorizationQuery = call.request.queryParameters[ACCESS_TOKEN]

        val authorizationHeaderReceipt = isValidAccessTokenFromHeader(configuration, authorizationHeader)
        val authorizationQueryReceipt = isValidAccessTokenFromQuery(configuration, authorizationQuery)

        authorizationHeaderReceipt.warning?.let { logger.warn { it } }
        authorizationQueryReceipt.warning?.let { logger.warn { it } }

        val accepted: Boolean = when (authorizationHeaderReceipt.type) {
            AuthorizationReceiptType.VALID -> when (authorizationQueryReceipt.type) {
                AuthorizationReceiptType.VALID -> {
                    logger.warn {
                        "Multi same valid access token provided from $address, " +
                                "but `allowMultiSameAccessTokenProvided` is ${configuration.allowMultiSameAccessTokenProvided}."
                    }

                    configuration.allowMultiSameAccessTokenProvided
                }

                AuthorizationReceiptType.INVALID -> {
                    logger.warn {
                        "Multi different access token provided from $address, " +
                                "and `allowDifferentAccessTokenIfPresent` is ${configuration.allowDifferentAccessTokenIfPresent}."
                    }
                    configuration.allowDifferentAccessTokenIfPresent
                }

                AuthorizationReceiptType.FORMAT_ERROR -> {
                    logger.warn {
                        "Multi access token provided from $address, " +
                                "but the one from header is valid, and the format of other from query is error, " +
                                "and ${configuration.allowFormatErrorWhenMultiAccessTokenProvided}."
                    }
                    configuration.allowFormatErrorWhenMultiAccessTokenProvided
                }

                AuthorizationReceiptType.PROVIDE_REQUIRED -> {
                    if (configuration.accessToken === null ||
                        (configuration.ignoreConfiguredEmptyAccessToken && configuration.accessToken!!.isEmpty())
                    ) {
                        logger.info { "Authorized by the access token from header." }
                    } else {
                        logger.info { "Authorized because no access token required." }
                    }
                    true
                }
            }

            AuthorizationReceiptType.INVALID -> when (authorizationQueryReceipt.type) {
                AuthorizationReceiptType.VALID -> {
                    logger.warn {
                        "Multi different access token provided from $address, " +
                                "but `allowDifferentAccessTokenIfPresent` is ${configuration.allowDifferentAccessTokenIfPresent}."
                    }
                    configuration.allowDifferentAccessTokenIfPresent
                }

                AuthorizationReceiptType.INVALID -> {
                    logger.warn {
                        "Multi access token provided from $address, " +
                                "but both are invalid."
                    }
                    false
                }

                AuthorizationReceiptType.FORMAT_ERROR -> {
                    logger.warn {
                        "Multi access token provided from $address, " +
                                "but the one from header is invalid, and the format of other from query is error."
                    }
                    false
                }

                AuthorizationReceiptType.PROVIDE_REQUIRED -> {
                    false
                }
            }

            AuthorizationReceiptType.FORMAT_ERROR -> when (authorizationQueryReceipt.type) {
                AuthorizationReceiptType.VALID -> {
                    logger.warn {
                        "Multi different access token provided from $address, " +
                                "but `allowDifferentAccessTokenIfPresent` is ${configuration.allowDifferentAccessTokenIfPresent}."
                    }
                    configuration.allowDifferentAccessTokenIfPresent
                }

                AuthorizationReceiptType.INVALID -> {
                    logger.warn {
                        "Multi access token provided from $address, " +
                                "but the one from query is invalid, and the format of other from header is error."
                    }
                    false
                }

                AuthorizationReceiptType.FORMAT_ERROR -> {
                    logger.warn {
                        "Multi access token provided from $address, " +
                                "but the format of both are error."
                    }
                    false
                }

                AuthorizationReceiptType.PROVIDE_REQUIRED -> false
            }

            AuthorizationReceiptType.PROVIDE_REQUIRED -> when (authorizationQueryReceipt.type) {
                AuthorizationReceiptType.VALID -> {
                    if (configuration.accessToken === null ||
                        (configuration.ignoreConfiguredEmptyAccessToken && configuration.accessToken!!.isEmpty())
                    ) {
                        logger.info { "Authorized by the access token from query." }
                    } else {
                        logger.info { "Authorized because no access token required." }
                    }
                    true
                }

                AuthorizationReceiptType.INVALID -> {
                    logger.warn {
                        "Multi access token provided from $address, " +
                                "but the one from query is invalid, and the format of other from header is error."
                    }
                    false
                }

                AuthorizationReceiptType.FORMAT_ERROR -> {
                    logger.warn {
                        "Multi access token provided from $address, " +
                                "but the format of both are error."
                    }
                    false
                }

                AuthorizationReceiptType.PROVIDE_REQUIRED -> false
            }
        }
        if (!accepted) {
            call.respondText("Unauthorized", status = HttpStatusCode.Unauthorized)
            finish()
            return false
        }

        // 2. check self id.
        val selfIDHeader = call.request.headers[X_SELF_ID]
        if (selfIDHeader === null) {
            logger.warn { "Missing `$X_SELF_ID` header from $address." }
            call.respondText("Missing `$X_SELF_ID` header", status = HttpStatusCode.BadRequest)
            finish()
            return false
        }

        val selfID = selfIDHeader.toLongOrNull()
        if (selfID === null) {
            logger.warn { "Invalid `$X_SELF_ID` header from $address: $selfIDHeader." }
            call.respondText("Invalid `$X_SELF_ID` header", status = HttpStatusCode.BadRequest)
            finish()
            return false
        }
        return true
    }
}