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
 *
 */

package com.pyamsoft.pydroid.arch

import androidx.annotation.CheckResult
import androidx.annotation.UiThread
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pyamsoft.pydroid.core.Enforcer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import timber.log.Timber
import kotlin.LazyThreadSafetyMode.NONE

abstract class UiViewModel<S : UiViewState, V : UiViewEvent, C : UiControllerEvent> protected constructor(
    initialState: S,
    private val debug: Boolean
) : ViewModel(), SaveableState {

    private val onInitEventDelegate = lazy(NONE) { mutableSetOf<(UiBundleReader) -> Unit>() }
    private val onInitEvents by onInitEventDelegate

    private val onTeardownEventDelegate = lazy(NONE) { mutableSetOf<() -> Unit>() }
    private val onTeardownEvents by onTeardownEventDelegate

    private val onSaveStateEventDelegate =
        lazy(NONE) { mutableSetOf<(UiBundleWriter, S) -> Unit>() }
    private val onSaveStateEvents by onSaveStateEventDelegate

    private val controllerEventBus = EventBus.create<C>()

    // NOTE(Peter): Since state events run on their own single threaded dispatcher, we may not
    // need a mutex since there will only ever be one thread at a time.
    private val mutex = Mutex()

    // This useless interface exists just so I don't have to mark everything as experimental
    private var state: UiVMState<S> = UiVMStateImpl(initialState)

    protected abstract fun handleViewEvent(event: V)

    // Need PublishedApi so createComponent can be inline
    @UiThread
    @CheckResult
    @PublishedApi
    internal fun render(
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

            // Listen for any further state changes at this point
            queueInOrder { bindStateEvents(views) }

            // Render the latest or initial state
            queueInOrder {
                handleStateChange(views, state.get())
            }
        }
    }

    /**
     * Launches this coroutine on the single threaded main context
     * This ensures that the operation queued will run in order before any of the other operations after it
     *
     * Must be CoroutineScope extension to cancel correctly
     */
    private inline fun CoroutineScope.queueInOrder(crossinline func: suspend CoroutineScope.() -> Unit) {
        launch(context = Dispatchers.Main) { func() }
    }

    @UiThread
    private fun initialize(savedInstanceState: UiBundleReader) {
        // Only run the init hooks if they exist, otherwise we don't need to init the memory
        if (onInitEventDelegate.isInitialized()) {

            // Call init hooks in random order
            onInitEvents.forEach { it(savedInstanceState) }

            // Clear the init hooks list to free up memory
            onInitEvents.clear()
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
            val s = state.get()
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
        if (onInitEventDelegate.isInitialized()) {
            onInitEvents.clear()
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

    /**
     * Modify the state from the previous
     *
     * Note that, like calling this.setState() in React, this operation does not happen immediately.
     */
    protected fun setState(func: S.() -> S) {
        viewModelScope.queueInOrder {
            withContext(context = Dispatchers.Default) {
                processStateChange(isDebuggable = true) { func() }

            }
        }
    }

    /**
     * Act upon the current state
     *
     * Note that like accessing state in React using this.state.<var>, this is immediate and
     * may not be up to date with the latest setState() call.
     */
    protected fun withState(func: S.() -> Unit) {
        viewModelScope.queueInOrder {
            // Yield to any setState calls happening at this point
            yield()

            withContext(context = Dispatchers.Default) {
                processStateChange(isDebuggable = false) { this.apply(func) }
            }
        }
    }

    private suspend inline fun processStateChange(isDebuggable: Boolean, stateChange: S.() -> S) {
        mutex.withLock {
            val oldState = state.get()
            val newState = oldState.stateChange()

            // If we are in debug mode, perform the state change twice and make sure that it produces
            // the same state both times.
            if (debug && isDebuggable) {
                val copyNewState = oldState.stateChange()
                checkStateEquality(newState, copyNewState)
            }

            state.set(newState)
        }
    }

    /**
     * If we are in debug mode, perform the state change twice and make sure that it produces
     * the same state both times.
     */
    private fun checkStateEquality(state1: S, state2: S) {
        if (state1 != state2) {
            // Pull a page from the MvRx repo's BaseMvRxViewModel :)
            val changedProp = state1::class.java.declaredFields.asSequence()
                .onEach { it.isAccessible = true }
                .firstOrNull { property ->
                    try {
                        val prop1 = property.get(state1)
                        val prop2 = property.get(state2)
                        prop1 != prop2
                    } catch (e: Throwable) {
                        // Failed but we don't care
                        false
                    }
                }

            if (changedProp == null) {
                throw DeterministicStateError(state1, state2, null)
            } else {
                val prop1 = changedProp.get(state1)
                val prop2 = changedProp.get(state2)
                throw DeterministicStateError(prop1, prop2, changedProp.name)
            }
        }
    }

    private fun handleStateChange(
        views: Array<out UiView<S, V>>,
        state: S
    ) {
        Timber.d("Render with state: $state")
        views.forEach { it.render(state) }
    }

    private suspend fun bindStateEvents(views: Array<out UiView<S, V>>) {
        withContext(context = Dispatchers.IO) {
            state.onChange { state ->
                withContext(context = Dispatchers.Main) { handleStateChange(views, state) }
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
    protected fun doOnInit(onInit: (savedInstanceState: UiBundleReader) -> Unit) {
        Enforcer.assertOnMainThread()

        onInitEvents.add(onInit)
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

    private class DeterministicStateError internal constructor(
        state1: Any?,
        state2: Any?,
        prop: String?
    ) : IllegalStateException(
        """State changes must be deterministic
           ${if (prop != null) "Property '$prop' changed:" else ""}
           $state1
           $state2
           """.trimIndent()
    )

    // Exists as a useless interface just so that when using the implementation in the UiViewModel
    // I don't need to mark everything as experimental
    private interface UiVMState<S : UiViewState> {

        @CheckResult
        fun get(): S

        fun set(value: S)

        suspend fun onChange(withState: suspend (state: S) -> Unit)
    }

    private class UiVMStateImpl<S : UiViewState> internal constructor(
        initialState: S
    ) : UiVMState<S> {

        @ExperimentalCoroutinesApi
        private val flow = MutableStateFlow(initialState)

        @ExperimentalCoroutinesApi
        override fun get(): S {
            return flow.value
        }

        @ExperimentalCoroutinesApi
        override fun set(value: S) {
            flow.value = value
        }

        @ExperimentalCoroutinesApi
        override suspend fun onChange(withState: suspend (state: S) -> Unit) {
            flow.collect(withState)
        }
    }
}
