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

package cn.chuanwise.onebot.lib.v11

import cn.chuanwise.onebot.lib.AppWebSocketConnection
import cn.chuanwise.onebot.lib.DEFAULT_ACCESS_TOKEN
import cn.chuanwise.onebot.lib.DEFAULT_HEARTBEAT_INTERVAL_MILLISECONDS
import cn.chuanwise.onebot.lib.DEFAULT_MAX_RECONNECT_ATTEMPTS
import cn.chuanwise.onebot.lib.DEFAULT_PATH
import cn.chuanwise.onebot.lib.DEFAULT_RECONNECT_INTERVAL_MILLISECONDS
import cn.chuanwise.onebot.lib.Expect
import cn.chuanwise.onebot.lib.OutgoingChannel
import cn.chuanwise.onebot.lib.Pack
import cn.chuanwise.onebot.lib.WatchDog
import cn.chuanwise.onebot.lib.WebSocketConnectionConfiguration
import cn.chuanwise.onebot.lib.deserializeTo
import cn.chuanwise.onebot.lib.requireConnected
import cn.chuanwise.onebot.lib.v11.data.ASYNC
import cn.chuanwise.onebot.lib.v11.data.FAILED
import cn.chuanwise.onebot.lib.v11.data.OK
import cn.chuanwise.onebot.lib.v11.data.OneBot11ToImplPack
import cn.chuanwise.onebot.lib.v11.data.action.HandleQuickOperationData
import cn.chuanwise.onebot.lib.v11.data.event.EventData
import cn.chuanwise.onebot.lib.v11.utils.getObjectMapper
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.websocket.CloseReason
import io.ktor.websocket.Frame
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


data class OneBot11AppWebSocketConnectionConfiguration(
    override val host: String,
    override val port: Int,
    override val path: String = DEFAULT_PATH,
    override val accessToken: String? = DEFAULT_ACCESS_TOKEN,
    override val maxConnectAttempts: Int? = DEFAULT_MAX_RECONNECT_ATTEMPTS,
    override val reconnectInterval: Duration = DEFAULT_RECONNECT_INTERVAL_MILLISECONDS.milliseconds,
    override val heartbeatInterval: Duration = DEFAULT_HEARTBEAT_INTERVAL_MILLISECONDS.milliseconds
) : WebSocketConnectionConfiguration {

    // Java-friendly API
    constructor(
        host: String,
        port: Int,
        path: String,
        accessToken: String?
    ) : this(
        host,
        port,
        path,
        accessToken,
        DEFAULT_MAX_RECONNECT_ATTEMPTS,
        DEFAULT_RECONNECT_INTERVAL_MILLISECONDS.milliseconds,
        DEFAULT_HEARTBEAT_INTERVAL_MILLISECONDS.milliseconds
    )
}

class OneBot11AppWebSocketConnection private constructor(
    private val objectMapper: ObjectMapper,
    private val logger: KLogger,
    configuration: WebSocketConnectionConfiguration,
) : AppWebSocketConnection(objectMapper, logger, configuration), OneBot11AppConnection {

    override val incomingChannel: OneBot11AppWebSocketIncomingChannel = OneBot11AppWebSocketIncomingChannel(logger)

    private inner class OutgoingChannelImpl : OutgoingChannel<OneBot11ToImplPack, Unit> {
        override suspend fun send(t: OneBot11ToImplPack) {
            val text = objectMapper.writeValueAsString(t)
            val currentSession = session.requireConnected()

            currentSession.send(Frame.Text(text))
        }

        override fun close() = Unit
    }

    override val outgoingChannel: OutgoingChannel<out Pack, *> = OutgoingChannelImpl()

    // enable watch dog when connected and heartbeat interval is set.
    // disable watch dog when disconnected.
    private val watchDogJobs = launch {
        while (state != State.DISCONNECTED) {
            // wait util connected
            while (state != State.CONNECTED) {
                await()
            }

            // check intervals
            val interval = configuration.heartbeatInterval ?: continue

            val watchDog = WatchDog(interval)
            val feederUUID = incomingChannel.registerListener(HEARTBEAT_META_EVENT) {
                watchDog.feed()
            }
            val hungryDetector = launch {
                while (state == State.CONNECTED) {
                    delay(interval)
                    if (watchDog.isHungry) {
                        disconnect(CloseReason(CloseReason.Codes.NORMAL, "Heartbeat timeout."))
                    }
                }
            }

            // wait util disconnected.
            while (state == State.CONNECTED) {
                await()
            }

            incomingChannel.unregisterListener(feederUUID)
            hungryDetector.cancel("Disconnected.")
        }
    }

    @JvmOverloads
    constructor(
        configuration: WebSocketConnectionConfiguration,
        objectMapper: ObjectMapper = getObjectMapper(),
        logger: KLogger = KotlinLogging.logger { },
    ) : this(objectMapper, logger, configuration)

    override suspend fun onReceive(node: JsonNode) {
        val event = objectMapper.treeToValue(node, EventData::class.java)
        incomingChannel.income(event)?.let {
            call(HIDDEN_HANDLE_QUICK_OPERATION, HandleQuickOperationData(event, it))
        }
    }

    @Suppress("UNCHECKED_CAST")
    override suspend fun <P, R> call(expect: Expect<P, R>, params: P): R {
        val resp = doCall(session, receivingLoop, objectMapper, logger, expect, params, CallPolicy.DEFAULT)
        return when (resp.status) {
            OK -> resp.data?.deserializeTo(objectMapper, expect.respType) ?: Unit as R
            ASYNC -> throw IllegalStateException("Async response.")
            FAILED -> throw IllegalStateException("Operation failed in implementation.")
            else -> throw IllegalStateException("Unknown response status: ${resp.status}")
        }
    }

    override suspend fun <P> callAsync(expect: Expect<P, *>, params: P) {
        val resp = doCall(session, receivingLoop, objectMapper, logger, expect, params, CallPolicy.ASYNC)
        return when (resp.status) {
            OK -> throw IllegalStateException("Not async response.")
            ASYNC -> Unit
            FAILED -> throw IllegalStateException("Failed.")
            else -> throw IllegalStateException("Unknown response status: ${resp.status}")
        }
    }

    override suspend fun <P> callRateLimited(expect: Expect<P, *>, params: P) {
        val resp = doCall(session, receivingLoop, objectMapper, logger, expect, params, CallPolicy.RATE_LIMITED)
        return when (resp.status) {
            OK -> throw IllegalStateException("Not async response.")
            ASYNC -> Unit
            FAILED -> throw IllegalStateException("Failed.")
            else -> throw IllegalStateException("Unknown error.")
        }
    }

    override fun await(): OneBot11AppWebSocketConnection = super.await() as OneBot11AppWebSocketConnection
}