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
 * A default implementation of a UiStateViewModel which knows how to set up along
 * with UiViews and a UiController to become a full UiComponent
 */
public abstract class UiViewModel<S : UiViewState, V : UiViewEvent, C : UiControllerEvent> protected constructor(
    initialState: S
) : UiStateViewModel<S>(initialState) {

    private val onClearEventDelegate = lazy(NONE) { mutableSetOf<() -> Unit>() }
    private val onClearEvents by onClearEventDelegate

    @Deprecated("To be removed in favor of Controller driven architecture")
    private val controllerEventBus = EventBus.create<C>(emitOnlyWhenActive = true)

    // Need PublishedApi so createComponent can be inline
    @UiThread
    @CheckResult
    @PublishedApi
    @Deprecated("Replace with bindViews as we are moving to a Controller driven architecture.")
    internal fun bindToComponent(
        savedInstanceState: UiSavedStateReader,
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
            internalBindState(views)
        }
    }

    /**
     * Bind one or more UiViews to be driven by this UiViewModel
     */
    @UiThread
    @CheckResult
    public fun bindViews(
        savedInstanceState: UiSavedStateReader,
        vararg views: UiView<S, out V>,
        onEvent: suspend (event: V) -> Unit
    ): Job {

        // Guarantee views are initialized
        // Run this outside of the view model scope to guarantee that it executes immediately
        views.forEach { it.init(savedInstanceState) }

        return viewModelScope.launch(context = Dispatchers.Main) {

            // Bind ViewModel
            bindViewEvents(views.asIterable(), onEvent)

            // Inflate the views
            views.forEach { it.inflate(savedInstanceState) }

            // Bind state
            internalBindState(views)
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

    private fun CoroutineScope.bindViewEvents(
        views: Iterable<UiView<S, out V>>,
        onEvent: suspend (event: V) -> Unit
    ) {
        launch(context = Dispatchers.IO) {
            views.forEach { view ->
                view.onViewEvent(onEvent)
                if (view is BaseUiView<S, out V, *>) {
                    val nestedViews = view.nestedViews()
                    if (nestedViews.isNotEmpty()) {
                        bindViewEvents(nestedViews, onEvent)
                    }
                }
            }
        }
    }

    @Deprecated("To be removed in favor of Controller driven architecture")
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

    @Deprecated("To be removed in favor of Controller driven architecture")
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
     * Handle a UiViewEvent
     */
    @Deprecated("To be removed in favor of Controller driven architecture")
    protected open fun handleViewEvent(event: V) {

    }
}
