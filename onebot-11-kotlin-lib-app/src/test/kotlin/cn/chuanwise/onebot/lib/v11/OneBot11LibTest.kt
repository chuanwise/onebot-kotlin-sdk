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
import cn.chuanwise.onebot.lib.v11.utils.getObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.github.oshai.kotlinlogging.KotlinLogging
import java.net.URL
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

class OneBot11LibTest {
    companion object {
        private val objectMapper = getObjectMapper()
        private val configurations = objectMapper.readValue<OneBot11LibTestConfiguration>(
            getResourceURL("configurations.json")
        )

        private val logger = KotlinLogging.logger { }

        private val appWebSocketConnection: OneBot11AppWebSocketConnection by lazy {
            OneBot11AppWebSocketConnection(configurations.appWebSocketConnection).awaitUtilConnected()
        }
        private val appReverseWebSocketConnection: OneBot11AppReverseWebSocketConnection by lazy {
            OneBot11AppReverseWebSocketConnection(configurations.appReverseWebSocketConnection).awaitUtilConnected()
        }

        private val appConnection = appReverseWebSocketConnection

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
        @AfterAll
        fun afterAll() {
            appConnection.close()
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

//    @Test
//    fun testEventReceiving(): Unit = runBlocking {
//        delay(1145141919810)
//    }

    @Test
    fun testSendPrivateMessage(): Unit = runBlocking {
        if (!configurations.testSendPrivateMessage) {
            return@runBlocking
        }

        listOf(singleTextMessageData, textMessageInCQFormat, catImageData).forEach {
            appConnection.sendPrivateMessage(
                userID = configurations.friendUserID,
                message = it,
            )
        }
    }

    @Test
    fun testSendGroupMessage(): Unit = runBlocking {
        listOf(shakingData, recordData).forEach {
            appConnection.sendGroupMessage(
                groupID = configurations.botIsAdminGroupID,
                message = it,
            )
        }
    }

    @Test
    fun testSendAndRecallMessage(): Unit = runBlocking {
        val groupMessageID = appConnection.sendMessage(
            messageType = GROUP,
            groupID = configurations.botIsAdminGroupID,
            userID = null,
            message = textMessageWithAtFriend,
        )
        delay(5000)
        appConnection.deleteMessage(groupMessageID)

        if (!configurations.testSendPrivateMessage) {
            return@runBlocking
        }
        val privateMessageID = appConnection.sendMessage(
            messageType = PRIVATE,
            groupID = null,
            userID = configurations.friendUserID,
            message = shakingData,
        )
        delay(5000)
        appConnection.deleteMessage(privateMessageID)
    }

    @Test
    fun testGetGroupInfo(): Unit = runBlocking {
        val loginInfo = appConnection.getLoginInfo()
    }


    @Test
    fun testGetForwardMessage(): Unit = runBlocking {
        appConnection.events<MessageEventData> { event ->
            launch {
                if (event.messageType != FORWARD) return@launch
                appConnection.getMessage(event.messageID.toInt())
            }
        }
    }

    @Test
    fun testSendLike(): Unit = runBlocking {
        appConnection.sendLike(
            userID = configurations.friendUserID,
            times = 10
        )
    }

    @Test
    fun testSetGroupKick(): Unit = runBlocking {
        appConnection.setGroupKick(
            groupID = configurations.botIsAdminAndOtherIsMember.groupID,
            userID = configurations.botIsAdminAndOtherIsMember.userID,
            rejectAddRequest = false
        )
    }

    @Test
    fun testSetGroupBan(): Unit = runBlocking {
        appConnection.setGroupBan(
            groupID = configurations.botIsAdminAndOtherIsMember.groupID,
            userID = configurations.botIsAdminAndOtherIsMember.userID,
            duration = 114L
        )
    }

    @Test
    fun testSetGroupWholeBan(): Unit = runBlocking {
        appConnection.setGroupWholeBan(
            groupID = configurations.botIsAdminAndOtherIsMember.userID,
            enable = true
        )
        delay(5000)
        appConnection.setGroupWholeBan(
            groupID = configurations.botIsAdminAndOtherIsMember.groupID,
            enable = true
        )
    }

    @Test
    fun testSetGroupAdmin(): Unit = runBlocking {
        appConnection.setGroupAdmin(
            groupID = configurations.botIsOwnerAndOtherIsMember.groupID,
            userID = configurations.botIsOwnerAndOtherIsMember.userID,
            enable = true
        )

        delay(5000)

        appConnection.setGroupAdmin(
            groupID = configurations.botIsOwnerAndOtherIsMember.groupID,
            userID = configurations.botIsOwnerAndOtherIsMember.userID,
            enable = false
        )
    }

    @Test
    fun testSetGroupAnonymous(): Unit = runBlocking {
        appConnection.setGroupAnonymous(
            groupID = configurations.botIsOwnerAndOtherIsMember.groupID,
            enable = true
        )

        delay(5000)

        appConnection.setGroupAnonymous(
            groupID = configurations.botIsOwnerAndOtherIsMember.groupID,
            enable = false
        )
    }

    @Test
    fun testSetGroupCard(): Unit = runBlocking {
        appConnection.setGroupCard(
            groupID = configurations.botIsAdminAndOtherIsMember.groupID,
            userID = configurations.botIsAdminAndOtherIsMember.userID,
            card = "Test Group Card"
        )

        delay(5000)

        appConnection.setGroupCard(
            groupID = configurations.botIsAdminAndOtherIsMember.groupID,
            userID = configurations.botIsAdminAndOtherIsMember.userID,
            card = ""
        )

    }

    @Test
    fun testSetGroupAnonymousBanByAnonymousSenderData(): Unit = runBlocking {
        appConnection.events<GroupMessageEventData> { event ->

            launch {
                val anonymous = event.anonymous ?: return@launch
                appConnection.setGroupAnonymousBan(
                    groupID = event.groupID,
                    sender = anonymous,
                    duration = 114L
                )
            }

        }
    }

    @Test
    fun testSetGroupAnonymousBanByFlag(): Unit = runBlocking {
        appConnection.events<GroupMessageEventData> { event ->
            launch {
                val anonymous = event.anonymous ?: return@launch
                appConnection.setGroupAnonymousBan(
                    groupID = event.groupID,
                    flag = anonymous.flag,
                    duration = 114L
                )
            }
        }
    }

    @Test
    fun testSetGroupName(): Unit = runBlocking {
        val group = appConnection.getGroupInfo(
            groupID = configurations.botIsOwnerGroupID,
            noCache = true
        )

        appConnection.setGroupName(
            groupID = group.groupID,
            groupName = "Test Group Name"
        )

        delay(5000)

        appConnection.setGroupName(
            groupID = group.groupID,
            groupName = group.groupName
        )

    }

    @Test
    fun testSetGroupLeave(): Unit = runBlocking {
        appConnection.setGroupLeave(
            groupID = configurations.botIsOwnerGroupID,
            isDismiss = true
        )
    }

    @Test
    fun testSetGroupSpecialTitle(): Unit = runBlocking {
        appConnection.setGroupSpecialTitle(
            groupID = configurations.botIsOwnerGroupID,
            userID = configurations.friendUserID,
            specialTitle = "TestTitle",
            duration = 3600L
        )

    }

    @Test
    fun testSetFriendAddRequest(): Unit = runBlocking {
        appConnection.events<FriendAddRequestEventData> { event ->
            launch {
                appConnection.setFriendAddRequest(
                    flag = event.flag,
                    approve = true,
                    remark = ""
                )
            }
        }
    }

    @Test
    fun testSetGroupAddRequest(): Unit = runBlocking {
        appConnection.events<GroupAddRequestEventData> { event ->
            launch {
                appConnection.setFriendAddRequestAsync(
                    flag = event.flag,
                    approve = true,
                    remark = ""
                )
            }
        }
    }

    @Test
    fun testGetLoginInfo(): Unit = runBlocking {
        appConnection.getLoginInfo()
    }

    @Test
    fun testGetFriendList(): Unit = runBlocking {
        listOf(appConnection.getFriendList())
    }

    @Test
    fun testGetStrangerInfo(): Unit = runBlocking {
        appConnection.getStrangerInfo(
            userID = 10000L,
            noCache = true
        )
    }

    @Test
    fun testGetGroupMemberList(): Unit = runBlocking {
        appConnection.getGroupMemberList(
            groupID = configurations.botIsMemberGroupID
        )
    }

    @Test
    fun testGetGroupHonorInfo(): Unit = runBlocking {
        appConnection.getGroupHonorInfo(
            groupID = configurations.botIsMemberGroupID,
            type = "all"
        )
    }

    @Test
    fun testGetCSRFToken(): Unit = runBlocking {
        appConnection.getCSRFToken()
    }

    @Test
    fun testGetCredentials(): Unit = runBlocking {
        logger.warn {
            """ 
                [Warn]
                Method: ${testGetCredentials()}
                We cannot test this case because this method depends on the actual business needs.
            """.trimIndent()
        }
        assert(true)
    }

    @Test
    fun testGetCookies(): Unit = runBlocking {
        logger.warn {
            """ 
                [Warn]
                Method: ${testGetCookies()}
                We cannot test this case because this method depends on the actual business needs.
            """.trimIndent()
        }
        assert(true)

    }

    @Test
    fun testGetImage(): Unit = runBlocking {
        val messageID = appConnection.sendMessage(
            messageType = IMAGE,
            message = SingleMessageData(
                data = ImageData(
                    file = getResourceURL("messages/cat.jpg").file.toString(),
                    cache = null,
                    proxy = null,
                    type = null,
                    url = null,
                    summary = null,
                    timeout = null
                ),
                type = IMAGE
            ),
            userID = null,
            groupID = configurations.botIsAdminGroupID
        )
        val image = when(val data = appConnection.getMessage(messageID).message){
            is ArrayMessageData -> data.data.firstOrNull()?.data as ImageData
            is SingleMessageData -> data.data as ImageData
            else -> throw IllegalStateException()
        }
        appConnection.getImage(
            file = image.file
        )
    }

    @Test
    fun testCanSendImage(): Unit = runBlocking {
        appConnection.canSendImage()
    }

    @Test
    fun testCanSendRecord(): Unit = runBlocking {
        appConnection.canSendRecord()
    }

    @Test
    fun testGetStatus(): Unit = runBlocking {
        appConnection.getStatus()
    }

    @Test
    fun testGetVersionInfo(): Unit = runBlocking {
        appConnection.getVersionInfo()
    }

    @Test
    fun testSetRestart(): Unit = runBlocking {
        appConnection.setRestart(
            delay = 2000
        )
    }

    @Test
    fun testCleanCache(): Unit = runBlocking {
        appConnection.cleanCache()
    }
}