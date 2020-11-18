/*
 * Copyright 2020 Peter Kenji Yamanaka
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

package com.pyamsoft.pydroid.arch

import androidx.annotation.CheckResult
import com.pyamsoft.pydroid.arch.internal.RealBus
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

interface EventBus<T : Any> : EventConsumer<T> {

    suspend fun send(event: T)

    companion object {

        @CheckResult
        @JvmStatic
        @JvmOverloads
        @Deprecated(
            "This constructor uses emitOnlyWhenActive = false",
            replaceWith = ReplaceWith("EventBus.create<T>(false, context)")
        )
        fun <T : Any> create(
            context: CoroutineContext = EmptyCoroutineContext
        ): EventBus<T> = RealBus(emitOnlyWhenActive = false, context)

        @CheckResult
        @JvmStatic
        @JvmOverloads
            /**
             * The EventBus will event the event in the following cases
             *
             * If [emitOnlyWhenActive] is false, the event will always emit immediately
             * If [emitOnlyWhenActive] is true, the event will be emitted if/once a subscriber is listening
             * on the bus
             */
        fun <T : Any> create(
            emitOnlyWhenActive: Boolean,
            context: CoroutineContext = EmptyCoroutineContext
        ): EventBus<T> = RealBus(emitOnlyWhenActive, context)
    }
}
