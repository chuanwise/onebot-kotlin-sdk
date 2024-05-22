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

class IncreasingInts(
    private val bound: Int? = null
) : Iterable<Int> {

    inner class IncreasingIntsIterator(
        private var current: Int = 0
    ) : Iterator<Int> {

        override fun hasNext(): Boolean = hasNext(current)

        override fun next(): Int = current++
    }

    override fun iterator(): Iterator<Int> = IncreasingIntsIterator()

    fun hasNext(current: Int) = if (bound !== null) current <= bound else true
}