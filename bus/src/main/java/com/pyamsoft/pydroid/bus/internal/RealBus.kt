/*
 * Copyright 2023 pyamsoft
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

package com.pyamsoft.pydroid.bus.internal

import androidx.annotation.CheckResult
import com.pyamsoft.pydroid.bus.EventBus
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.withContext

/** Real implementation of the EventBus */
@Deprecated("Use SharedFlow directly instead")
internal class RealBus<T : Any>
@Deprecated("Use SharedFlow directly instead")
internal constructor(
    private val replayCount: Int,
    private val context: CoroutineContext,
) : EventBus<T> {

  // Backing bus
  private val bus by lazy { MutableSharedFlow<T>(replay = replayCount) }

  override suspend fun send(event: T) = withContext(context = context) { bus.emit(event) }

  @CheckResult
  override suspend fun onEvent(emitter: suspend (event: T) -> Unit) =
      withContext(context = context) { bus.collect(emitter) }
}
