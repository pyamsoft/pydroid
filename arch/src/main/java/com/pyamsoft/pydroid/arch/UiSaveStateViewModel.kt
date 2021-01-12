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
import androidx.annotation.UiThread
import androidx.lifecycle.viewModelScope
import com.pyamsoft.pydroid.bus.EventBus
import com.pyamsoft.pydroid.core.Enforcer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.LazyThreadSafetyMode.NONE

/**
 * A default implementation of a UiStateViewModel which knows how to set up along with UiViews and a UiController to become a full UiComponent
 *
 * Knows how to save and restore state from an androidx.SavedStateHandle
 *
 * TODO(Peter): Once the doOnSaveState hook is removed from UiViewModel, we can have this class extend UiViewModel. Check ABI compatibility.
 */
public abstract class UiSaveStateViewModel<S : UiViewState, V : UiViewEvent, C : UiControllerEvent> protected constructor(
    savedState: UiSavedState,
    initialState: S
) : UiStateViewModel<S>(initialState) {

    @PublishedApi
    internal var savedState: UiSavedState? = savedState

    private val onClearEventDelegate = lazy(NONE) { mutableSetOf<() -> Unit>() }
    private val onClearEvents by onClearEventDelegate

    private val controllerEventBus = EventBus.create<C>(emitOnlyWhenActive = true)

    init {
        doOnCleared {
            // Clear out ref to handle
            this.savedState = null
        }
    }

    // Need PublishedApi so createComponent can be inline
    @UiThread
    @CheckResult
    @PublishedApi
    internal fun bindToComponent(
        savedInstanceState: UiBundleReader,
        views: Array<out UiView<S, out V>>,
        onControllerEvent: (event: C) -> Unit
    ): Job {

        // Guarantee views are initialized
        // Run this outside of the view model scope to guarantee that it executes immediately
        views.forEach { it.init(savedInstanceState) }

        return viewModelScope.launch(context = Dispatchers.Main) {

            // Bind ViewModel
            bindControllerEvents(onControllerEvent)
            bindViewEvents(views.asIterable())

            // Inflate the views
            views.forEach { it.inflate(savedInstanceState) }

            // Bind state
            bindState(views)
        }
    }

    /**
     * Called when the UiViewModel is being cleared for good.
     */
    @UiThread
    final override fun onCleared() {
        super.onCleared()
        Enforcer.assertOnMainThread()

        if (onClearEventDelegate.isInitialized()) {

            // Call teardown hooks in random order
            onClearEvents.forEach { it() }

            // Clear the teardown hooks list to free up memory
            onClearEvents.clear()
        }
    }

    /**
     * Fire a controller event
     */
    protected fun publish(event: C) {
        viewModelScope.launch(context = Dispatchers.IO) {
            controllerEventBus.send(event)
        }
    }

    private fun CoroutineScope.bindViewEvents(views: Iterable<UiView<S, out V>>) {
        launch(context = Dispatchers.IO) {
            views.forEach { view ->
                view.onViewEvent { handleViewEvent(it) }
                if (view is BaseUiView<S, out V, *>) {
                    val nestedViews = view.nestedViews()
                    if (nestedViews.isNotEmpty()) {
                        bindViewEvents(nestedViews)
                    }
                }
            }
        }
    }

    private inline fun CoroutineScope.bindControllerEvents(crossinline onControllerEvent: (event: C) -> Unit) {
        launch(context = Dispatchers.IO) {
            controllerEventBus.onEvent {
                // Controller events must fire onto the main thread
                withContext(context = Dispatchers.Main) {
                    onControllerEvent(it)
                }
            }
        }
    }

    /**
     * Use this to run an event after UiViewModel onCleared has successfully finished.
     *
     * This is generally used in something like the constructor
     *
     * init {
     *     doOnCleared {
     *         ...
     *     }
     * }
     *
     * NOTE: Not thread safe. Main thread only for the time being
     */
    @UiThread
    protected fun doOnCleared(onTeardown: () -> Unit) {
        Enforcer.assertOnMainThread()
        onClearEvents.add(onTeardown)
    }

    /**
     * Use this to restore data from a SavedStateHandle
     *
     * This is generally used at a variable declaration site
     *
     * private val userId = restoreState("user_id") { 0 }
     *
     * NOTE: Not thread safe. Main thread only for the time being
     */
    @UiThread
    @CheckResult
    protected inline fun <T : Any> restoreState(key: String, defaultValue: () -> T): T {
        return requireNotNull(savedState).get(key) ?: defaultValue()
    }

    /**
     * Use this to save data to a SavedStateHandle
     *
     * fun doThing() {
     *   val result = doStuff()
     *   saveState("stuff", result)
     * }
     *
     * NOTE: Not thread safe. Main thread only for the time being
     */
    @UiThread
    protected fun <T : Any> saveState(key: String, value: T) {
        return requireNotNull(savedState).put(key, value)
    }

    /**
     * Handle a UiViewEvent
     */
    protected abstract fun handleViewEvent(event: V)
}
