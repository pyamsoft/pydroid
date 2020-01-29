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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import timber.log.Timber
import kotlin.LazyThreadSafetyMode.NONE
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

abstract class UiViewModel<S : UiViewState, V : UiViewEvent, C : UiControllerEvent> protected constructor(
    private val initialState: S
) : ViewModel(), SaveableState {

    private var isInitialized = false

    private val onInitEventDelegate = lazy(NONE) { mutableSetOf<(UiBundleReader) -> Unit>() }
    private val onInitEvents by onInitEventDelegate

    private val onTeardownEventDelegate = lazy(NONE) { mutableSetOf<() -> Unit>() }
    private val onTeardownEvents by onTeardownEventDelegate

    private val onSaveStateEventDelegate =
        lazy(NONE) { mutableSetOf<UiBundleWriter.(state: S) -> Unit>() }
    private val onSaveStateEvents by onSaveStateEventDelegate

    private val controllerEventBus = EventBus.create<C>()
    private val stateBus = EventBus.create<StateChange<S>>()
    private val flushQueueBus = EventBus.create<FlushQueueEvent>()

    private val mutex = Mutex()
    private val setStateQueue = mutableListOf<S.() -> S>()
    private val withStateQueue = mutableListOf<S.() -> Unit>()

    private var state: S? = null

    init {
        flushQueueBus.scopedEvent(Dispatchers.Default) { flushQueues() }
    }

    protected abstract fun handleViewEvent(event: V)

    /**
     * Used for saving state in persistent lifecycle
     *
     * NOTE: While not deprecated, do your best to use StateSaver.saveState to bundle state
     * saving of entire components in a safe way
     */
    override fun saveState(outState: UiBundleWriter) {
        // Only run the save state hooks if they exist, otherwise we don't need to init the memory
        if (onSaveStateEventDelegate.isInitialized()) {

            // Call save state hooks in random order
            val s = latestState()
            for (saveState in onSaveStateEvents) {
                outState.saveState(s)
            }

            // Don't clear the event list since this lifecycle method can be called many times.
        }
    }

    @CheckResult
    @PublishedApi
    internal fun render(
        savedInstanceState: UiBundleReader,
        vararg views: IView<S, V>,
        onControllerEvent: (event: C) -> Unit
    ): Job = viewModelScope.launch(context = Dispatchers.Main) {
        // Listen for changes
        launch(context = Dispatchers.Default) {
            stateBus.onEvent { stateChange ->
                withContext(context = Dispatchers.Main) {
                    handleStateChange(views, stateChange)
                }
            }
        }

        // Bind ViewModel
        bindControllerEvents(onControllerEvent)
        bindViewEvents(views)

        // Initialize before first render
        initialize(savedInstanceState)

        // Inflate the views
        views.forEach { it.inflate(savedInstanceState) }

        // Flush the queue before we begin
        flushQueues()

        // Render the latest or initial state
        handleStateChange(views, StateChange(newState = latestState(), oldState = null))
    }

    final override fun onCleared() {
        if (onTeardownEventDelegate.isInitialized()) {

            // Call teardown hooks in random order
            for (teardownEvent in onTeardownEvents) {
                teardownEvent()
            }

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

        // Clear queues and state
        setStateQueue.clear()
        withStateQueue.clear()
        state = null
    }

    @JvmOverloads
    protected fun <E : Any> EventConsumer<E>.scopedEvent(
        context: CoroutineContext = EmptyCoroutineContext,
        func: suspend (event: E) -> Unit
    ): Job {
        val bus = this
        return viewModelScope.launch(context = context) {
            bus.onEvent(func)
        }
    }

    protected fun publish(event: C) {
        viewModelScope.launch(context = Dispatchers.Default) {
            controllerEventBus.send(event)
        }
    }

    @CheckResult
    private fun latestState(): S {
        return state ?: initialState
    }

    /**
     * Modify the state from the previous
     *
     * Note that, like calling this.setState() in React, this operation does not happen immediately.
     */
    protected fun setState(func: S.() -> S) {
        viewModelScope.launch(context = Dispatchers.Default) {
            mutex.withLock {
                setStateQueue.add(func)
            }

            flushQueueBus.send(FlushQueueEvent)
        }
    }

    /**
     * Act upon the current state
     *
     * Note that like accessing state in React using this.state.<var>, this is immediate and
     * may not be up to date with the latest setState() call.
     */
    protected fun withState(func: S.() -> Unit) {
        viewModelScope.launch(context = Dispatchers.Default) {
            mutex.withLock {
                withStateQueue.add(func)
            }

            flushQueueBus.send(FlushQueueEvent)
        }
    }

    @CheckResult
    private suspend fun dequeueAllPendingStateChanges(): List<S.() -> S> {
        return mutex.withLock {
            if (setStateQueue.isEmpty()) {
                return@withLock emptyList()
            }

            val queue = setStateQueue.toList()
            setStateQueue.clear()
            return@withLock queue
        }
    }

    private suspend fun dequeueAllPendingSetStateChanges() {
        val stateChanges = dequeueAllPendingStateChanges()
        if (stateChanges.isEmpty()) {
            Timber.w("State queue is empty, ignore flush.")
            return
        }

        mutex.withLock {
            // Capture the state before modifications take place
            val currentState = latestState()
            var newState = currentState

            // Loop over all state changes first, perform but do not actually fire a render to views
            for (stateChange in stateChanges) {
                newState = newState.stateChange()
                if (newState != state) {
                    state = newState
                }
            }

            // Only send the new state at the end of the state change loop
            if (newState != currentState) {
                // Replace the old state with this new state
                stateBus.send(StateChange(newState = newState, oldState = currentState))
            }
        }
    }

    // Pull a page from the MvRx repo's RealMvRxStateStore :)
    // Mark this function as tailrec to see if the compiler can optimize it
    private tailrec suspend fun flushQueues() {
        // Run all pending setStates first
        dequeueAllPendingSetStateChanges()

        mutex.withLock {
            // Queue up one withState, or exit the tailrec if there are no more events
            val stateQueue = withStateQueue
            if (stateQueue.size <= 0) {
                return
            }

            // Run the operation
            val withStateOperation = stateQueue.removeAt(0)
            withStateOperation(latestState())
        }

        // We must call ourselves as the final operation to be tailrec compatible
        // Recur until we return out by having no more withState operations
        flushQueues()
    }

    private fun handleStateChange(
        views: Array<out Renderable<S>>,
        state: StateChange<S>
    ) {
        Timber.d("Render with state: $state")
        views.forEach { it.render(state.newState) }
    }

    private suspend fun initialize(savedInstanceState: UiBundleReader) {
        mutex.withLock {
            if (isInitialized) {
                Timber.w("Initialization is already complete.")
                return
            }

            isInitialized = true
            // Only run the init hooks if they exist, otherwise we don't need to init the memory
            if (onInitEventDelegate.isInitialized()) {

                // Call init hooks in random order
                for (initEvent in onInitEvents) {
                    initEvent(savedInstanceState)
                }

                // Clear the init hooks list to free up memory
                onInitEvents.clear()
            }
        }
    }

    @CheckResult
    private fun CoroutineScope.bindViewEvents(views: Array<out IView<S, V>>): Job =
        launch(context = Dispatchers.Default) {
            for (view in views) {
                if (view is UiView<S, V>) {

                    // Launch another coroutine here for handling view events
                    launch(context = Dispatchers.Default) {
                        view.onViewEvent { event ->
                            withContext(context = Dispatchers.Main) { handleViewEvent(event) }
                        }
                    }

                    if (view is BaseUiView<S, V>) {
                        val nestedViews = view.nestedViews()
                        if (nestedViews.isNotEmpty()) {
                            bindViewEvents(nestedViews)
                        }
                    }
                }
            }
        }

    @CheckResult
    private inline fun CoroutineScope.bindControllerEvents(crossinline onControllerEvent: (event: C) -> Unit): Job =
        launch(context = Dispatchers.Default) {
            controllerEventBus.onEvent { event ->
                withContext(context = Dispatchers.Main) { onControllerEvent(event) }
            }
        }

    protected inline fun Throwable.onActualError(func: (throwable: Throwable) -> Unit) {
        if (this !is CancellationException) {
            func(this)
        }
    }

    /**
     * Use this to run an event after UiViewModel initialization has successfully finished.
     *
     * This is generally used in something like the constructor
     *
     * init {
     *     doOnInit {
     *         ...
     *     }
     * }
     *
     */
    protected fun doOnInit(onInit: (savedInstanceState: UiBundleReader) -> Unit) {
        onInitEvents.add(onInit)
    }

    /**
     * Use this to run an event when lifecycle is saving state
     *
     * This is generally used in something like the constructor
     *
     * init {
     *     doOnSaveState { state ->
     *          putInt(...)
     *          putString(...)
     *     }
     * }
     *
     */
    protected fun doOnSaveState(onSaveState: UiBundleWriter.(state: S) -> Unit) {
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
     */
    protected fun doOnTeardown(onTeardown: () -> Unit) {
        onTeardownEvents.add(onTeardown)
    }

    private object FlushQueueEvent

    private data class StateChange<S : UiViewState>(val newState: S, val oldState: S?)
}
