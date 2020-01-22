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
import androidx.annotation.IdRes
import kotlin.LazyThreadSafetyMode.NONE

abstract class UiView<S : UiViewState, V : UiViewEvent> protected constructor() : IView<S, V> {

    private val viewEventBus by lazy(NONE) { EventBus.create<V>() }

    private val onInflateEventDelegate = lazy(NONE) { mutableListOf<(UiBundleReader) -> Unit>() }
    private val onInflateEvents by onInflateEventDelegate

    private val onTeardownEventDelegate = lazy(NONE) { mutableListOf<() -> Unit>() }
    private val onTeardownEvents by onTeardownEventDelegate

    private val onSaveEventDelegate = lazy(NONE) { mutableSetOf<(UiBundleWriter) -> Unit>() }
    private val onSaveEvents by onSaveEventDelegate

    @IdRes
    @CheckResult
    abstract fun id(): Int

    /**
     * This is really only used as a hack, so we can inflate the actual Layout before running init hooks.
     *
     * This way a UiViewModel can bind event handlers and receive events, and the view can publish()
     * inside of doOnInflate hooks.
     */
    final override fun init(savedInstanceState: UiBundleReader) {
        onInit(savedInstanceState)
    }

    final override fun inflate(savedInstanceState: UiBundleReader) {
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

    final override fun teardown() {
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
     * Save state
     *
     * NOTE: While not deprecated, do your best to use StateSaver.saveState to bundle state
     * saving of entire components in a safe way
     */
    final override fun saveState(outState: UiBundleWriter) {
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

    internal suspend fun onViewEvent(func: suspend (event: V) -> Unit) {
        viewEventBus.onEvent(func)
    }

    protected fun publish(event: V) {
        viewEventBus.publish(event)
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
    protected fun doOnInflate(onInflate: (savedInstanceState: UiBundleReader) -> Unit) {
        onInflateEvents.add(onInflate)
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
    protected fun doOnSaveState(onSaveState: (outState: UiBundleWriter) -> Unit) {
        onSaveEvents.add(onSaveState)
    }

    protected abstract fun onInit(savedInstanceState: UiBundleReader)
}
