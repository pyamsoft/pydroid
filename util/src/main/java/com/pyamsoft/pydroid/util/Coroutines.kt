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

package com.pyamsoft.pydroid.util

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableSharedFlow

/**
 * A SharedFlow once collected by definition will never complete. This effectively causes our
 * current Coroutine to permanently suspend as it waits forever for a SharedFlow.collect completion
 * event which will never happen
 */
private val neverEndingFlow = MutableSharedFlow<Nothing>()

/**
 * Hold the coroutine "forever" until it is cancelled
 *
 * Useful for start-stop work like such, where start is controlled by launching the Coroutine and
 * stop is controlled by cancelling i
 *
 * ```kotlin
 * launch {
 *   try {
 *     coroutineScope {
 *       doWorkOnStart()
 *       suspendUntilCancel()
 *     }
 *   } finally {
 *     withContext(NonCancellable) {
 *       doWorkOnCancel()
 *     }
 *   }
 * }
 * ```
 */
public suspend fun suspendUntilCancel(): Nothing {
  neverEndingFlow.collect {}
}

/** Rethrow cancellation exceptions to continue Coroutine flow, otherwise we handle the error */
public inline fun <R : Any> Throwable.ifNotCancellation(block: () -> R): R {
  return when (this) {
    is CancellationException -> throw this
    else -> block()
  }
}
