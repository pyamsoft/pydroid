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

import android.os.Bundle
import androidx.annotation.CheckResult
import androidx.annotation.IdRes
import kotlin.LazyThreadSafetyMode.NONE

abstract class UiView<S : UiViewState, V : UiViewEvent> protected constructor() {

    private val viewEventBus = EventBus.create<V>()
    private val onInflateEventDelegate = lazy(NONE) {
        mutableListOf<(savedInstanceState: Bundle?) -> Unit>()
    }
    private val onInflateEvents by onInflateEventDelegate

    private val onTeardownEventDelegate = lazy(NONE) { mutableListOf<() -> Unit>() }
    private val onTeardownEvents by onTeardownEventDelegate

    @IdRes
    @CheckResult
    abstract fun id(): Int

    @PublishedApi
    internal fun inflate(savedInstanceState: Bundle?) {
        doInflate(savedInstanceState)

        // Only run the inflation hooks if they exist, otherwise we don't need to init the memory
        if (onInflateEventDelegate.isInitialized()) {
            for (inflateEvent in onInflateEvents) {
                inflateEvent(savedInstanceState)
            }

            // Clear the inflation hooks list to free up memory
            onInflateEvents.clear()
        }
    }

    @Deprecated("Use doOnInflate { savedInstanceState: Bundle? -> } instead.")
    protected open fun doInflate(savedInstanceState: Bundle?) {
        // NOTE: The deprecated function call is kept around for compat purposes.
        // Intentionally blank
    }

    /**
     * Use this to run an event after UiView inflation has successfully finished.
     *
     * This is generally used in something like the constructor
     *
     * init {
     *     doOnInflate { savedInstanceState ->
     *         ...
     *     }
     * }
     *
     */
    protected fun doOnInflate(onInflate: (savedInstanceState: Bundle?) -> Unit) {
        onInflateEvents.add(onInflate)
    }

    abstract fun render(
        state: S,
        savedState: UiSavedState
    )

    @PublishedApi
    internal fun teardown() {
        doTeardown()

        // Only run teardown hooks if they exist, otherwise don't init memory
        if (onTeardownEventDelegate.isInitialized()) {
            for (teardownEvent in onTeardownEvents) {
                teardownEvent()
            }

            // Clear the teardown hooks list to free up memory
            onTeardownEvents.clear()
        }
    }

    /**
     * Use this to run an event after UiView teardown has successfully finished.
     *
     * This is generally used in something like the constructor
     *
     * init {
     *     doOnTeardown {
     *         ...
     *     }
     * }
     *
     */
    protected fun doOnTeardown(onTeardown: () -> Unit) {
        onTeardownEvents.add(onTeardown)
    }

    @Deprecated("Use doOnTeardown { () -> } instead.")
    protected open fun doTeardown() {
        // NOTE: The deprecated function call is kept around for compat purposes.
        // Intentionally blank
    }

    open fun saveState(outState: Bundle) {
    }

    internal suspend fun onViewEvent(func: suspend (event: V) -> Unit) {
        viewEventBus.onEvent(func)
    }

    protected fun publish(event: V) {
        viewEventBus.publish(event)
    }
}
