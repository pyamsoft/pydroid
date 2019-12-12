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

abstract class UiView<S : UiViewState, V : UiViewEvent> protected constructor() : Renderable<S> {

    private val viewEventBus = EventBus.create<V>()

    private val onInflateEventDelegate =
        lazy(NONE) { mutableListOf<(savedInstanceState: Bundle?) -> Unit>() }
    private val onInflateEvents by onInflateEventDelegate

    private val onTeardownEventDelegate = lazy(NONE) { mutableListOf<() -> Unit>() }
    private val onTeardownEvents by onTeardownEventDelegate

    private val onSaveEventDelegate = lazy(NONE) { mutableSetOf<(outState: Bundle) -> Unit>() }
    private val onSaveEvents by onSaveEventDelegate

    @IdRes
    @CheckResult
    abstract fun id(): Int

    @PublishedApi
    internal fun inflate(savedInstanceState: Bundle?) {
        // Only run the inflation hooks if they exist, otherwise we don't need to init the memory
        if (onInflateEventDelegate.isInitialized()) {

            // Call inflate hooks in FIFO order
            for (inflateEvent in onInflateEvents) {
                inflateEvent(savedInstanceState)
            }

            // Clear the inflation hooks list to free up memory
            onInflateEvents.clear()
        }
    }

    /**
     * Use this to run an event after UiView inflation has successfully finished.
     * Events are guaranteed to be called in FIFO order, one after the other.
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

    @PublishedApi
    internal fun teardown() {
        // Only run teardown hooks if they exist, otherwise don't init memory
        if (onTeardownEventDelegate.isInitialized()) {

            // Reverse the list order so that we teardown in LIFO order
            onTeardownEvents.reverse()

            for (teardownEvent in onTeardownEvents) {
                teardownEvent()
            }

            // Clear the teardown hooks list to free up memory
            onTeardownEvents.clear()
        }

        // If there are any inflate event hooks hanging around, clear them out too
        if (onInflateEventDelegate.isInitialized()) {
            onInflateEvents.clear()
        }

        // If there are any save state event hooks hanging around, clear them out too
        if (onSaveEventDelegate.isInitialized()) {
            onSaveEvents.clear()
        }
    }

    /**
     * Use this to run an event after UiView teardown has successfully finished.
     * Events are guaranteed to be called in LIFO (reverse) order, one after the other.
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

    fun saveState(outState: Bundle) {
        // Only run save state hooks if they exist, otherwise don't init memory
        if (onSaveEventDelegate.isInitialized()) {

            // Call save hooks in any arbitrary order
            for (saveEvent in onSaveEvents) {
                saveEvent(outState)
            }

            // DO NOT clear the onSaveEvents hook list here because onSaveState can happen multiple
            // times before the UiView calls teardown()
        }
    }

    /**
     * Use this to run an event during a UiView lifecycle saveState event
     * Events are not guaranteed to run in any consistent order
     *
     * This is generally used in something like the constructor
     *
     * init {
     *     doOnSaveState { outState ->
     *         ...
     *     }
     * }
     *
     */
    protected fun doOnSaveState(onSaveState: (outState: Bundle) -> Unit) {
        onSaveEvents.add(onSaveState)
    }

    internal suspend fun onViewEvent(func: suspend (event: V) -> Unit) {
        viewEventBus.onEvent(func)
    }

    protected fun publish(event: V) {
        viewEventBus.publish(event)
    }
}
