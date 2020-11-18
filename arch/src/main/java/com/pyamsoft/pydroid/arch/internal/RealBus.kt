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

package com.pyamsoft.pydroid.arch.internal

import androidx.annotation.CheckResult
import com.pyamsoft.pydroid.arch.EventBus
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

internal class RealBus<T : Any> internal constructor(
    private val emitOnlyWhenActive: Boolean,
    private val context: CoroutineContext
) : EventBus<T> {

    private val bus by lazy { MutableSharedFlow<T>() }

    private suspend fun emit(event: T) {
        bus.emit(event)
    }

    override suspend fun send(event: T) = withContext(context) {
        if (emitOnlyWhenActive) {
            bus.subscriptionCount.map { it > 0 }
                .distinctUntilChanged()
                .filter { it }
                .collect { emit(event) }
        } else {
            emit(event)
        }
    }

    @CheckResult
    override suspend fun onEvent(emitter: suspend (event: T) -> Unit) = withContext(context) {
        bus.collect(emitter)
    }
}
