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
                val examplePath = "$path.example"
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
            logger
            configurations = getResourceURL("configurations.json").let {
                objectMapper.readValue(it, OneBot11LibTestConfiguration::class.java)
            } ?: throw IllegalStateException(
                "Cannot find configurations.json, " +
                        "edit and rename `configurations.json.example` to `configurations.json` " +
                        "."
            )

            appWebSocketConnection = OneBot11AppWebSocketConnection(configurations.appWebSocketConnection)
            logger.info { "Connecting to WebSocket..." }

            appWebSocketConnection.awaitUtilConnected()
            logger.info { "Connected to WebSocket." }

            appReverseWebSocketConnection = OneBot11AppReverseWebSocketConnection(
                configurations.appReverseWebSocketConnection
            )
            logger.info { "Connecting to Reverse WebSocket..." }

            appReverseWebSocketConnection.awaitUtilConnected()
            logger.info { "Connected to Reverse WebSocket." }
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
            timeout = null,
            summary = null
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
    fun testEventReceiving(): Unit = runBlocking {
        delay(1145141919810)
    }

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

    @Test
    fun testGetGroupInfo(): Unit = runBlocking {
        appWebSocketConnection.getLoginInfo()
    }


    @Test
    fun getForwardMessage(): Unit = runBlocking {
        TODO()
    }

    @Test
    fun testSendLike(): Unit = runBlocking {
        appWebSocketConnection.sendLike(
            userID = configurations.friendUserID,
            times = 10
        )
    }

    @Test
    fun testSetGroupKick(): Unit = runBlocking {
        appWebSocketConnection.setGroupKick(
            groupID = configurations.botIsAdminAndOtherIsMember.groupID,
            userID = configurations.botIsAdminAndOtherIsMember.userID,
            rejectAddRequest = false
        )
    }

    @Test
    fun testSetGroupBan(): Unit = runBlocking {
        appWebSocketConnection.setGroupBan(
            groupID = configurations.botIsAdminAndOtherIsMember.groupID,
            userID = configurations.botIsAdminAndOtherIsMember.userID,
            duration = 114L
        )
    }

    @Test
    fun testSetGroupWholeBan(): Unit = runBlocking {
        appWebSocketConnection.setGroupWholeBan(
            groupID = configurations.botIsAdminAndOtherIsMember.userID,
            enable = true
        )
        delay(5000)
        appWebSocketConnection.setGroupWholeBan(
            groupID = configurations.botIsAdminAndOtherIsMember.groupID,
            enable = true
        )
    }

    @Test
    fun testSetGroupAdmin(): Unit = runBlocking {
        appWebSocketConnection.setGroupAdmin(
            groupID = configurations.botIsOwnerAndOtherIsMember.groupID,
            userID = configurations.botIsOwnerAndOtherIsMember.userID,
            enable = true
        )

        delay(5000)

        appWebSocketConnection.setGroupAdmin(
            groupID = configurations.botIsOwnerAndOtherIsMember.groupID,
            userID = configurations.botIsOwnerAndOtherIsMember.userID,
            enable = false
        )
    }

    @Test
    fun testSetGroupAnonymous(): Unit = runBlocking {
        appWebSocketConnection.setGroupAnonymous(
            groupID = configurations.botIsOwnerAndOtherIsMember.groupID,
            enable = true
        )

        delay(5000)

        appWebSocketConnection.setGroupAnonymous(
            groupID = configurations.botIsOwnerAndOtherIsMember.groupID,
            enable = false
        )
    }

    @Test
    fun testSetGroupCard(): Unit = runBlocking {
        appWebSocketConnection.setGroupCard(
            groupID = configurations.botIsAdminAndOtherIsMember.groupID,
            userID = configurations.botIsAdminAndOtherIsMember.userID,
            card = "Test Group Card"
        )

        delay(5000)

        appWebSocketConnection.setGroupCard(
            groupID = configurations.botIsAdminAndOtherIsMember.groupID,
            userID = configurations.botIsAdminAndOtherIsMember.userID,
            card = ""
        )

    }

    @Test
    fun testSetGroupAnonymousBanByAnonymousSenderData(): Unit = runBlocking {
        TODO()
    }

    @Test
    fun testSetGroupAnonymousBanByFlag(): Unit = runBlocking {
        TODO()
    }

    @Test
    fun testSetGroupName(): Unit = runBlocking {
        val group = appWebSocketConnection.getGroupInfo(
            groupID = configurations.botIsOwnerGroupID,
            noCache = true
        )

        appWebSocketConnection.setGroupName(
            groupID = group.groupID,
            groupName = "Test Group Name"
        )

        delay(5000)

        appWebSocketConnection.setGroupName(
            groupID = group.groupID,
            groupName = group.groupName
        )

    }

    @Test
    fun testSetGroupLeave(): Unit = runBlocking {
        appWebSocketConnection.setGroupLeave(
            groupID = configurations.botIsOwnerGroupID,
            isDismiss = true
        )
    }

    @Test
    fun testSetGroupSpecialTitle(): Unit = runBlocking {
        appWebSocketConnection.setGroupSpecialTitle(
            groupID = configurations.botIsOwnerGroupID,
            userID = configurations.friendUserID,
            specialTitle = "TestTitle",
            duration = 3600L
        )

    }

    @Test
    fun testSetFriendAddRequest(): Unit = runBlocking {
        TODO()
    }

    @Test
    fun testSetGroupAddRequest(): Unit = runBlocking {
        TODO()
    }

    @Test
    fun testGetLoginInfo(): Unit = runBlocking {
        appWebSocketConnection.getLoginInfo()
    }

    @Test
    fun testGetFriendList(): Unit = runBlocking {
        listOf(appWebSocketConnection.getFriendList())
    }

    @Test
    fun testGetStrangerInfo(): Unit = runBlocking {
        appWebSocketConnection.getStrangerInfo(
            userID = 10000L,
            noCache = true
        )
    }

    @Test
    fun testGetGroupMemberList(): Unit = runBlocking {
        appWebSocketConnection.getGroupMemberList(
            groupID = configurations.botIsMemberGroupID
        )
    }

    @Test
    fun testGetGroupHonorInfo(): Unit = runBlocking {
        appWebSocketConnection.getGroupHonorInfo(
            groupID = configurations.botIsMemberGroupID,
            type = "all"
        )
    }

    @Test
    fun testGetCSRFToken(): Unit = runBlocking {
        appWebSocketConnection.getCSRFToken()
    }

    @Test
    fun testGetCredentials(): Unit = runBlocking {
        TODO()
    }

    @Test
    fun testGetCookies(): Unit = runBlocking {
        TODO()
    }

    @Test
    fun testGetImage(): Unit = runBlocking {
        TODO()
    }

    @Test
    fun testCanSendImage(): Unit = runBlocking {
        appWebSocketConnection.canSendImage()
    }

    @Test
    fun testCanSendRecord(): Unit = runBlocking {
        appWebSocketConnection.canSendRecord()
    }

    @Test
    fun testGetStatus(): Unit = runBlocking {
        appWebSocketConnection.getStatus()
    }

    @Test
    fun testGetVersionInfo(): Unit = runBlocking {
        appWebSocketConnection.getVersionInfo()
    }

    @Test
    fun testSetRestart(): Unit = runBlocking {
        appWebSocketConnection.setRestart(
            delay = 2000
        )
    }

    @Test
    fun testCleanCache(): Unit = runBlocking {
        appWebSocketConnection.cleanCache()
    }
}