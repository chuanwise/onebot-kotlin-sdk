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

import cn.chuanwise.onebot.lib.AT
import cn.chuanwise.onebot.lib.FACE
import cn.chuanwise.onebot.lib.GROUP
import cn.chuanwise.onebot.lib.IMAGE
import cn.chuanwise.onebot.lib.PRIVATE
import cn.chuanwise.onebot.lib.RECORD
import cn.chuanwise.onebot.lib.TEXT
import cn.chuanwise.onebot.lib.awaitUtilConnected
import cn.chuanwise.onebot.lib.v11.data.message.ArrayMessageData
import cn.chuanwise.onebot.lib.v11.data.message.AtData
import cn.chuanwise.onebot.lib.v11.data.message.CQCodeMessageData
import cn.chuanwise.onebot.lib.v11.data.message.IDTag
import cn.chuanwise.onebot.lib.v11.data.message.ImageData
import cn.chuanwise.onebot.lib.v11.data.message.RecordData
import cn.chuanwise.onebot.lib.v11.data.message.SingleMessageData
import cn.chuanwise.onebot.lib.v11.data.message.TextData
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.github.oshai.kotlinlogging.KotlinLogging
import java.net.URL
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

class OneBot11LibTest {
    companion object {
        private lateinit var configurations: OneBot11LibTestConfiguration
        private val logger = KotlinLogging.logger { }

        private lateinit var appWebSocketConnection: OneBot11AppWebSocketConnection
        private lateinit var appReverseWebSocketConnection: OneBot11AppReverseWebSocketConnection

        @JvmStatic
        fun getResourceURL(path: String): URL {
            val resourceURL = Companion::class.java.classLoader.getResource(path)
            if (resourceURL === null) {
                val examplePath = path + ".example"
                throw IllegalStateException(
                    "Cannot find resource: $path, edit and rename `$examplePath` to `$path` " +
                            "in the test resources directory `onebot-11-kotlin-lib-app/src/test/resources` and try again."
                )
            }
            return resourceURL
        }

        @JvmStatic
        @BeforeAll
        fun beforeAll() {
            val objectMapper = jacksonObjectMapper()

            configurations = getResourceURL("configurations.json").let {
                objectMapper.readValue(it, OneBot11LibTestConfiguration::class.java)
            } ?: throw IllegalStateException(
                "Cannot find configurations.json, " +
                        "edit and rename `configurations.json.example` to `configurations.json` " +
                        "."
            )

            appWebSocketConnection =
                OneBot11AppWebSocketConnection(configurations.appWebSocketConnection).awaitUtilConnected()
            appReverseWebSocketConnection = OneBot11AppReverseWebSocketConnection(
                configurations.appReverseWebSocketConnection
            ).awaitUtilConnected()
        }

        @JvmStatic
        @AfterAll
        fun afterAll() {
            appWebSocketConnection.close()
            appReverseWebSocketConnection.close()
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
                AtData(configurations.friendUserID.toString()),
            ),
        )
    )
    private val catImageData = SingleMessageData(
        type = IMAGE,
        ImageData(
            file = getResourceURL("messages/cat.jpg").toString(),
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
            file = getResourceURL("messages/big-bang-laughs.mp3").toString(),
            magic = null,
            url = null,
            cache = 1,
            proxy = 0,
            timeout = null
        )
    )

    @Test
    fun testSendPrivateMessage(): Unit = runBlocking {
        if (!configurations.testSendPrivateMessage) {
            return@runBlocking
        }

        listOf(singleTextMessageData, textMessageInCQFormat, catImageData).forEach {
            appWebSocketConnection.sendPrivateMessage(
                userID = configurations.friendUserID,
                message = it,
            )
        }
    }

    @Test
    fun testSendGroupMessage(): Unit = runBlocking {
        listOf(shakingData, recordData).forEach {
            appWebSocketConnection.sendGroupMessage(
                groupID = configurations.botIsAdminGroupID,
                message = it,
            )
        }
    }

    @Test
    fun testSendAndRecallMessage(): Unit = runBlocking {
        val groupMessageID = appWebSocketConnection.sendMessage(
            messageType = GROUP,
            groupID = configurations.botIsAdminGroupID,
            userID = null,
            message = textMessageWithAtFriend,
        )
        delay(5000)
        appWebSocketConnection.deleteMessage(groupMessageID)

        val privateMessageID = appWebSocketConnection.sendMessage(
            messageType = PRIVATE,
            groupID = null,
            userID = configurations.friendUserID,
            message = shakingData,
        )
        delay(5000)
        appWebSocketConnection.deleteMessage(privateMessageID)
    }
}