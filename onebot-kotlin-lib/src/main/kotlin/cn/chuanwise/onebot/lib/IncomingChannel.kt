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

import io.github.oshai.kotlinlogging.KLogger
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * Handle incoming packs.
 *
 * @author Chuanwise
 */
interface IncomingChannel<T : Pack, R> : AutoCloseable {
    suspend fun income(t: T): R?
    fun registerListener(listener: suspend (T) -> R?): UUID
    fun unregisterListener(uuid: UUID): Boolean
}

abstract class AbstractIncomingChannel<T : Pack, R>(
    private val logger: KLogger
) : IncomingChannel<T, R> {
    private val listeners = ConcurrentHashMap<UUID, suspend (T) -> R?>()

    override fun registerListener(listener: suspend (T) -> R?): UUID {
        var uuid: UUID
        do {
            uuid = UUID.randomUUID()
        } while (listeners.putIfAbsent(uuid, listener) !== null)
        return uuid
    }

    override fun unregisterListener(uuid: UUID): Boolean {
        return listeners.remove(uuid) != null
    }

    override suspend fun income(t: T): R? {
        val results = arrayListOf<R>()
        listeners.values.forEach {
            try {
                it(t)?.let {
                    results.add(it)
                }
            } catch (exception: Exception) {
                logger.error(exception) { "An exception occurred while handling incoming pack." }
            }
        }
        if (results.size > 1) {
            throw IllegalStateException("Multiple listeners returned a result.")
        }
        return results.firstOrNull()
    }

    override fun close() {
        listeners.clear()
    }
}
