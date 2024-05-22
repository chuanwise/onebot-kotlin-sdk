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

package cn.chuanwise.onebot.lib

import com.fasterxml.jackson.databind.JsonNode
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * Handle incoming packs.
 *
 * @author Chuanwise
 */
interface PackBus {
    // Kotlin-friendly API
    suspend operator fun invoke(node: JsonNode) = push(node)
    suspend fun push(node: JsonNode)
    fun registerHandler(handler: suspend (Pack) -> Any?): UUID
    fun unregisterHandler(uuid: UUID): Boolean
}

abstract class AbstractPackBus : PackBus {
    private val handlers = ConcurrentHashMap<UUID, suspend (Pack) -> Any?>()

    override fun registerHandler(handler: suspend (Pack) -> Any?): UUID {
        var uuid: UUID
        do {
            uuid = UUID.randomUUID()
        } while (handlers.putIfAbsent(uuid, handler) !== null)
        return uuid
    }

    override fun unregisterHandler(uuid: UUID): Boolean {
        return handlers.remove(uuid) != null
    }

    protected suspend fun handle(pack: Pack): Any? {
        for (handler in handlers.values) {
            return handler(pack) ?: continue
        }
        return null
    }
}
