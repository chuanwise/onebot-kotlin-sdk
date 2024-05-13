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

package cn.chuanwise.onebot.v11.io

import cn.chuanwise.onebot.io.Action
import cn.chuanwise.onebot.v11.io.data.action.MessageIDData
import cn.chuanwise.onebot.v11.io.data.action.SendGroupMessageData
import cn.chuanwise.onebot.v11.io.data.action.SendPrivateMessageData
import com.fasterxml.jackson.core.type.TypeReference

/**
 * # OneBot 11 Action
 *
 * Defined the action of [OneBot 11](https://github.com/botuniverse/onebot-11/blob/master/api/public.md).
 *
 * Each one is corresponding to an object, which allows to restrict the parameter and response type in
 * compilation time.
 *
 * @author Chuanwise
 */
class OneBot11Action<P, R>(
    override val name: String,
    override val paraTypeReference: TypeReference<P>,
    override val respTypeReference: TypeReference<R>
) : Action<P, R> {
    companion object {
        /**
         * Create a new instance for Kotlin to simply use.
         */
        inline operator fun <reified P, reified R> invoke(name: String) =
            OneBot11Action(name, object : TypeReference<P>() {}, object : TypeReference<R>() {})

        val SEND_PRIVATE_MESSAGE = OneBot11Action<SendPrivateMessageData, MessageIDData>("send_private_msg")
        val SEND_GROUP_MESSAGE = OneBot11Action<SendGroupMessageData, MessageIDData>("send_group_msg")
    }
}
