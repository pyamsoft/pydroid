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
import com.pyamsoft.pydroid.core.Enforcer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.LazyThreadSafetyMode.NONE

abstract class UiViewModel<S : UiViewState, V : UiViewEvent, C : UiControllerEvent> protected constructor(
    initialState: S,
    debug: Boolean
) : UiStateViewModel<S, V, C>(initialState, debug), SaveableState {

    private val onBindEventDelegate = lazy(NONE) { mutableSetOf<(UiBundleReader) -> Unit>() }
    private val onBindEvents by onBindEventDelegate

    private val onTeardownEventDelegate = lazy(NONE) { mutableSetOf<() -> Unit>() }
    private val onTeardownEvents by onTeardownEventDelegate

    private val onSaveStateEventDelegate =
        lazy(NONE) { mutableSetOf<(UiBundleWriter, S) -> Unit>() }
    private val onSaveStateEvents by onSaveStateEventDelegate

    private val controllerEventBus = EventBus.create<C>()

    // Need PublishedApi so createComponent can be inline
    @UiThread
    @CheckResult
    @PublishedApi
    internal fun bindToComponent(
        savedInstanceState: UiBundleReader,
        vararg views: UiView<S, V>,
        onControllerEvent: (event: C) -> Unit
    ): Job = viewModelScope.launch(context = Dispatchers.Main) {

        // Bind ViewModel
        queueInOrder { bindControllerEvents(onControllerEvent) }
        queueInOrder { bindViewEvents(views.asIterable()) }

        // Use launch here so that we re-claim the Main context and have these run after the
        // controller and view events are finished binding
        queueInOrder {
            // Initialize before first render
            // Generally, since you will add your doOnInit hooks in the ViewModel init {} block,
            // they will only run once - which is when the object is created.
            //
            // If you wanna do some strange kind of stuff though, you do you.
            initialize(savedInstanceState)

            // Inflate the views
            views.forEach { it.inflate(savedInstanceState) }

            // Bind state
            bindState { state -> views.forEach { it.render(state) } }
        }
    }

    @UiThread
    private fun initialize(savedInstanceState: UiBundleReader) {
        // Only run the init hooks if they exist, otherwise we don't need to init the memory
        if (onBindEventDelegate.isInitialized()) {

            // Call init hooks in random order
            onBindEvents.forEach { it(savedInstanceState) }

            // Clear the init hooks list to free up memory
            onBindEvents.clear()
        }
    }

    /**
     * Used for saving state in persistent lifecycle
     *
     * NOTE: While not deprecated, do your best to use StateSaver.saveState to bundle state
     * saving of entire components in a safe way
     *
     * NOTE: Not thread safe, Main thread only for now
     */
    @UiThread
    override fun saveState(outState: UiBundleWriter) {
        Enforcer.assertOnMainThread()

        // Only run the save state hooks if they exist, otherwise we don't need to init the memory
        if (onSaveStateEventDelegate.isInitialized()) {

            // Call save state hooks in random order
            val s = getCurrentState()
            onSaveStateEvents.forEach { it(outState, s) }

            // Don't clear the event list since this lifecycle method can be called many times.
        }
    }

    @UiThread
    final override fun onCleared() {
        Enforcer.assertOnMainThread()

        if (onTeardownEventDelegate.isInitialized()) {

            // Call teardown hooks in random order
            onTeardownEvents.forEach { it() }

            // Clear the teardown hooks list to free up memory
            onTeardownEvents.clear()
        }

        // If there are any init event hooks hanging around, clear them out too
        if (onBindEventDelegate.isInitialized()) {
            onBindEvents.clear()
        }

        // If there are save state hooks around, clear them out
        if (onSaveStateEventDelegate.isInitialized()) {
            onSaveStateEvents.clear()
        }
    }

    /**
     * Fire a controller event
     */
    protected fun publish(event: C) {
        viewModelScope.queueInOrder {
            withContext(context = Dispatchers.IO) {
                controllerEventBus.send(event)
            }
        }
    }

    private suspend fun bindViewEvents(views: Iterable<UiView<S, V>>) {
        withContext(context = Dispatchers.IO) {
            views.forEach { view ->
                view.onViewEvent { handleViewEvent(it) }
                if (view is BaseUiView<S, V, *>) {
                    val nestedViews = view.nestedViews()
                    if (nestedViews.isNotEmpty()) {
                        bindViewEvents(nestedViews)
                    }
                }
            }
        }
    }

    private suspend inline fun bindControllerEvents(crossinline onControllerEvent: (event: C) -> Unit) {
        withContext(context = Dispatchers.IO) {
            controllerEventBus.onEvent {
                // Controller events must fire onto the main thread
                withContext(context = Dispatchers.Main) {
                    onControllerEvent(it)
                }
            }
        }
    }

    /**
     * Use this to run an event after UiViewModel initialization has successfully finished.
     *
     * This is generally used in something like the constructor
     *
     * init {
     *     doOnInit { savedInstanceState ->
     *         ...
     *     }
     * }
     *
     * NOTE: Not thread safe. Main thread only for the time being
     */
    @UiThread
    @Deprecated(
        message = "Use doOnBind",
        replaceWith = ReplaceWith(expression = "doOnBind(onInit)")
    )
    protected fun doOnInit(onInit: (savedInstanceState: UiBundleReader) -> Unit) {
        doOnBind(onInit)
    }

    /**
     * Use this to run an event after UiViewModel binding has successfully finished.
     *
     * This is generally used in something like the constructor
     *
     * init {
     *     doOnBind { savedInstanceState ->
     *         ...
     *     }
     * }
     *
     * NOTE: Not thread safe. Main thread only for the time being
     */
    @UiThread
    protected fun doOnBind(onInit: (savedInstanceState: UiBundleReader) -> Unit) {
        Enforcer.assertOnMainThread()

        onBindEvents.add(onInit)
    }

    /**
     * Use this to run an event when lifecycle is saving state
     *
     * This is generally used in something like the constructor
     *
     * init {
     *     doOnSaveState { outState, state ->
     *          outState.putInt(...)
     *          outState.putString(...)
     *     }
     * }
     *
     * NOTE: Not thread safe. Main thread only for the time being
     */
    @UiThread
    protected fun doOnSaveState(onSaveState: (outState: UiBundleWriter, state: S) -> Unit) {
        Enforcer.assertOnMainThread()

        onSaveStateEvents.add(onSaveState)
    }

    /**
     * Use this to run an event after UiViewModel teardown has successfully finished.
     *
     * This is generally used in something like the constructor
     *
     * init {
     *     doOnTeardown {
     *         ...
     *     }
     * }
     *
     * NOTE: Not thread safe. Main thread only for the time being
     */
    @UiThread
    protected fun doOnTeardown(onTeardown: () -> Unit) {
        Enforcer.assertOnMainThread()

        onTeardownEvents.add(onTeardown)
    }

    protected abstract fun handleViewEvent(event: V)
}
