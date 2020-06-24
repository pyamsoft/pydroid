/*
 * Copyright 2019 Peter Kenji Yamanaka
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
 *
 */

package com.pyamsoft.pydroid.arch

import androidx.annotation.CheckResult
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

interface EventBus<T : Any> : EventConsumer<T> {

    suspend fun send(event: T)

    companion object {

        private val EMPTY by lazy { create<Unit>() }

        @CheckResult
        @JvmStatic
        @JvmOverloads
        fun <T : Any> create(
            context: CoroutineContext = EmptyCoroutineContext
        ): EventBus<T> = RealBus(context)

        @CheckResult
        @JvmStatic
        @Deprecated("Do you really need a Global Unit speaking EventBus? Just create your own.")
        fun empty(): EventBus<Unit> = EMPTY
    }
}
