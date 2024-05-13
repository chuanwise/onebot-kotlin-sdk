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

package cn.chuanwise.onebot.v11

import cn.chuanwise.onebot.io.connection.ReverseWebSocketConnectionConfiguration
import cn.chuanwise.onebot.v11.io.api.WebSocketConnectionOneBot11API
import cn.chuanwise.onebot.v11.io.connection.ReverseWebSocketConnection
import cn.chuanwise.onebot.v11.io.data.message.CQCodeMessageData
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

internal class OneBot11Test {
    companion object {
        private lateinit var connectionConfiguration: ReverseWebSocketConnectionConfiguration
        private lateinit var oneBot11TestConfiguration: OneBot11TestConfiguration

        @JvmStatic
        @BeforeAll
        fun beforeAll() {
            connectionConfiguration = OneBot11Test::class.java.classLoader.getResourceAsStream("bot.json")?.use {
                jacksonObjectMapper().readValue(it, ReverseWebSocketConnectionConfiguration::class.java)
            } ?: throw IllegalStateException(
                "Cannot find `bot.json` in the test resource directory! " +
                        "Please copy the `bot.json.example` in the same directory and change its contents!"
            )

            oneBot11TestConfiguration =
                OneBot11Test::class.java.classLoader.getResourceAsStream("onebot-11-test-configuration.json")?.use {
                    jacksonObjectMapper().readValue(it, OneBot11TestConfiguration::class.java)
                } ?: throw IllegalStateException(
                    "Cannot find `onebot11.json` in the test resource directory! " +
                            "Please copy the `onebot11.json.example` in the same directory and change its contents!"
                )
        }
    }

    @Test
    fun testSendGroupMessage(): Unit = runBlocking {
        ReverseWebSocketConnection(
            configuration = connectionConfiguration
        ).use {
            val result = WebSocketConnectionOneBot11API(
                connection = it
            ).sendGroupMessage(
                groupID = oneBot11TestConfiguration.groupID,
                message = CQCodeMessageData("Hello World!"),
            )
            println(result)
        }
    }
}