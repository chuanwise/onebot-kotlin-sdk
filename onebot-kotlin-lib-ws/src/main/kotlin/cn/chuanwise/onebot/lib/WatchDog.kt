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

import java.util.concurrent.atomic.AtomicLong
import kotlin.time.Duration

/**
 * A watchdog that will check if the connection is still alive.
 *
 * @author Chuanwise
 */
class WatchDog(
    private val interval: Duration
) {
    private val lastFeedMilliseconds = AtomicLong(System.currentTimeMillis())

    val isHungry: Boolean
        get() = System.currentTimeMillis() - lastFeedMilliseconds.get() > interval.inWholeMilliseconds

    fun feed() {
        lastFeedMilliseconds.set(System.currentTimeMillis())
    }
}