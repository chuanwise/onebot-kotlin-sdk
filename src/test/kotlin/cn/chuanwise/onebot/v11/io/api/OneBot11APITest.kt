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

package cn.chuanwise.onebot.v11.io.api

import cn.chuanwise.onebot.io.connection.BackwardWebSocketConnection
import cn.chuanwise.onebot.io.connection.BackwardWebSocketConnectionConfiguration
import cn.chuanwise.onebot.io.data.objectMapper
import cn.chuanwise.onebot.v11.OneBot11TestConfiguration
import cn.chuanwise.onebot.v11.io.data.AT
import cn.chuanwise.onebot.v11.io.data.FACE
import cn.chuanwise.onebot.v11.io.data.GROUP
import cn.chuanwise.onebot.v11.io.data.IMAGE
import cn.chuanwise.onebot.v11.io.data.PRIVATE
import cn.chuanwise.onebot.v11.io.data.RECORD
import cn.chuanwise.onebot.v11.io.data.TEXT
import cn.chuanwise.onebot.v11.io.data.message.ArrayMessageData
import cn.chuanwise.onebot.v11.io.data.message.AtData
import cn.chuanwise.onebot.v11.io.data.message.CQCodeMessageData
import cn.chuanwise.onebot.v11.io.data.message.IDTag
import cn.chuanwise.onebot.v11.io.data.message.ImageData
import cn.chuanwise.onebot.v11.io.data.message.RecordData
import cn.chuanwise.onebot.v11.io.data.message.SingleMessageData
import cn.chuanwise.onebot.v11.io.data.message.TextData
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

internal class OneBot11APITest {
    companion object {
        private lateinit var connectionConfiguration: BackwardWebSocketConnectionConfiguration
        private lateinit var testConfiguration: OneBot11TestConfiguration
        private lateinit var api: WebSocketConnectionOneBot11API

        @JvmStatic
        @BeforeAll
        fun beforeAll() = runBlocking {
            connectionConfiguration =
                OneBot11APITest::class.java.classLoader.getResourceAsStream("configurations/bot.json")?.use {
                    objectMapper.readValue(it, BackwardWebSocketConnectionConfiguration::class.java)
            } ?: throw IllegalStateException(
                "Cannot find `bot.json` in the test resource directory! " +
                        "Please copy the `bot.json.example` in the same directory and change its contents!"
            )

            testConfiguration =
                OneBot11APITest::class.java.classLoader.getResourceAsStream("configurations/onebot-11-test-configuration.json")
                    ?.use {
                        objectMapper.readValue(it, OneBot11TestConfiguration::class.java)
                } ?: throw IllegalStateException(
                    "Cannot find `onebot11.json` in the test resource directory! " +
                            "Please copy the `onebot11.json.example` in the same directory and change its contents!"
                )

            api = WebSocketConnectionOneBot11API(
                connection = BackwardWebSocketConnection(
                    configuration = connectionConfiguration
                ).await()
            )
        }

        @JvmStatic
        @AfterAll
        fun afterAll() {
            api.close()
            api.connection.close()
        }
    }

    private val singleTextMessageData = SingleMessageData(
        type = TEXT,
        TextData("Hello World!")
    )
    private val textMessageInCQFormat = CQCodeMessageData("CQ Hello World!")
    private val textMessageWithAtFriend = ArrayMessageData(
        data = listOf(
            SingleMessageData(
                type = TEXT,
                TextData("Hello, "),
            ),
            SingleMessageData(
                type = AT,
                AtData(testConfiguration.friendUserID.toString()),
            ),
        )
    )
    private val catImageData = SingleMessageData(
        type = IMAGE,
        ImageData(
            file = OneBot11APITest::class.java.classLoader.getResource("messages/cat.jpg")!!.toURI().toString(),
            type = null,
            url = null,
            cache = 1,
            proxy = 0,
            timeout = null
        )
    )
    private val shakingData = SingleMessageData(
        type = FACE,
        data = IDTag("41")
    )
    private val recordData = SingleMessageData(
        type = RECORD,
        RecordData(
            file = OneBot11APITest::class.java.classLoader.getResource("messages/big-bang-laughs.mp3")!!.toURI()
                .toString(),
            magic = null,
            url = null,
            cache = 1,
            proxy = 0,
            timeout = null
        )
    )

    @Test
    fun testSendPrivateMessage(): Unit = runBlocking {
        if (!testConfiguration.testSendPrivateMessage) {
            return@runBlocking
        }

        listOf(singleTextMessageData, textMessageInCQFormat, catImageData).forEach {
            api.sendPrivateMessage(
                userID = testConfiguration.friendUserID,
                message = it,
            )
        }
    }

    @Test
    fun testSendGroupMessage(): Unit = runBlocking {
        listOf(shakingData, recordData).forEach {
            api.sendGroupMessage(
                groupID = testConfiguration.botIsAdminGroupID,
                message = it,
            )
        }
    }

    @Test
    fun testSendAndRecallMessage(): Unit = runBlocking {
        val groupMessageID = api.sendMessage(
            messageType = GROUP,
            groupID = testConfiguration.botIsAdminGroupID,
            userID = null,
            message = textMessageWithAtFriend,
        )
        delay(5000)
        api.deleteMessage(groupMessageID)

        val privateMessageID = api.sendMessage(
            messageType = PRIVATE,
            groupID = null,
            userID = testConfiguration.friendUserID,
            message = shakingData,
        )
        delay(5000)
        api.deleteMessage(privateMessageID)
    }
}
