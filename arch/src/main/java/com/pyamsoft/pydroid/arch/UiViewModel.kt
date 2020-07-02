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

    private val onSaveStateEventDelegate = lazy(NONE) { mutableSetOf<UiBundleWriter.(S) -> Unit>() }
    private val onSaveStateEvents by onSaveStateEventDelegate

    private val controllerEventBus = EventBus.create<C>()
    private val processOperationBus = EventBus.create<ProcessOperationsRequest>()

    private val jobQueue = JobQueue<S>()

    // This useless interface exists just so I don't have to mark everything as experimental
    private var state: UiVMState<S> = UiVMStateImpl(initialState)

    init {
        doOnInit {
            viewModelScope.launch(context = Dispatchers.IO) {
                processOperationBus.onEvent {
                    yield()
                    processStateOperations()
                }
            }
        }
    }

    protected abstract fun handleViewEvent(event: V)

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
        bindControllerEvents(onControllerEvent)
        bindViewEvents(views.asIterable())

        // Initialize before first render
        // Generally, since you will add your doOnInit hooks in the ViewModel init {} block,
        // they will only run once - which is when the object is created.
        //
        // If you wanna do some strange kind of stuff though, you do you.
        initialize(savedInstanceState)

        // Inflate the views
        views.forEach { it.inflate(savedInstanceState) }

        // Flush the queue before we begin
        // This will make sure that the first render uses the most up to date state
        // This will also avoid firing render events to the views since it occurs all before the view
        // layer is bound
        processStateOperations()

        // Listen for any further state changes at this point
        bindStateEvents(views)

        // Render the latest or initial state
        handleStateChange(views, state.get())
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

        jobQueue.clear()
    }

    /**
     * Fire a controller event
     */
    protected fun publish(event: C) {
        viewModelScope.launch(context = Dispatchers.IO) {
            controllerEventBus.send(event)
        }
    }

    /**
     * Modify the state from the previous
     *
     * Note that, like calling this.setState() in React, this operation does not happen immediately.
     */
    protected fun setState(func: SetStateBlock<S>) {
        viewModelScope.launch(context = Dispatchers.IO) {
            jobQueue.enqueueSetState(func)

            yield()
            processOperationBus.send(ProcessOperationsRequest)
        }
    }

    /**
     * Act upon the current state
     *
     * Note that like accessing state in React using this.state.<var>, this is immediate and
     * may not be up to date with the latest setState() call.
     */
    protected fun withState(func: WithStateBlock<S>) {
        viewModelScope.launch(context = Dispatchers.IO) {
            jobQueue.enqueueWithState(func)

            yield()
            processOperationBus.send(ProcessOperationsRequest)
        }
    }

    @CheckResult
    private fun processAllSetStateChanges(): Boolean {
        val stateChanges = jobQueue.dequeueAllSetStateBlocks()
        if (stateChanges.isEmpty()) {
            return false
        }

        // Capture the state before modifications take place
        val oldState = state.get()
        var newState = oldState

        // Loop over all state changes first, perform but do not actually fire a render to views
        for (stateChange in stateChanges) {
            val currentState = newState
            newState = currentState.stateChange()

            // If we are in debug mode, perform the state change twice and make sure that it produces
            // the same state both times.
            if (debug) {
                val copyNewState = currentState.stateChange()
                checkStateEquality(newState, copyNewState)
            }
        }

        // Only send the new state at the end of the state change loop
        if (newState != oldState) {
            // Replace the old state with this new state
            state.set(newState)
        }

        return true
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

    @CheckResult
    private fun processNextWithStateChange(): Boolean {
        val operation = jobQueue.dequeueWithStateBlock() ?: return false
        operation(state.get())
        return true
    }

    // Pull a page from the MvRx repo's RealMvRxStateStore :)
    // Mark this function as tailrec to see if the compiler can optimize it
    private tailrec suspend fun processStateOperations() {
        // Wait for anything else first
        yield()

        var setStateCompleted = false
        var withStateCompleted = false

        // Run all pending setStates first
        if (!processAllSetStateChanges()) {
            setStateCompleted = true
        }

        // Run the next withState change
        if (!processNextWithStateChange()) {
            withStateCompleted = true
        }

        // exit out of the loop if no more work
        if (setStateCompleted && withStateCompleted) {
            return
        }

        // We must call ourselves as the final operation to be tailrec compatible
        // Recur until we return out by having no more withState operations
        processStateOperations()
    }

    private fun handleStateChange(
        views: Array<out UiView<S, V>>,
        state: S
    ) {
        Timber.d("Render with state: $state")
        views.forEach { it.render(state) }
    }

    private fun initialize(savedInstanceState: UiBundleReader) {
        // Only run the init hooks if they exist, otherwise we don't need to init the memory
        if (onInitEventDelegate.isInitialized()) {

            // Call init hooks in random order
            onInitEvents.forEach { it(savedInstanceState) }

            // Clear the init hooks list to free up memory
            onInitEvents.clear()
        }
    }

    // This must be an extension on the CoroutineScope or it will not cancel when the scope cancels
    private fun CoroutineScope.bindStateEvents(views: Array<out UiView<S, V>>) {
        launch(context = Dispatchers.IO) {
            state.onChange { state ->
                launch(context = Dispatchers.Main) { handleStateChange(views, state) }
            }
        }
    }

    // This must be an extension on the CoroutineScope or it will not cancel when the scope cancels
    private fun CoroutineScope.bindViewEvents(views: Iterable<UiView<S, V>>) {
        launch(context = Dispatchers.IO) {
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

    // This must be an extension on the CoroutineScope or it will not cancel when the scope cancels
    private inline fun CoroutineScope.bindControllerEvents(crossinline onControllerEvent: (event: C) -> Unit) {
        launch(context = Dispatchers.IO) {
            controllerEventBus.onEvent {
                // Controller events must fire onto the main thread
                launch(context = Dispatchers.Main) {
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
     *     doOnInit {
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
     *     doOnSaveState { state ->
     *          putInt(...)
     *          putString(...)
     *     }
     * }
     *
     * NOTE: Not thread safe. Main thread only for the time being
     */
    @UiThread
    protected fun doOnSaveState(onSaveState: UiBundleWriter.(state: S) -> Unit) {
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

    private object ProcessOperationsRequest

    private class JobQueue<S : UiViewState> {

        private val setStateQueue = mutableListOf<SetStateBlock<S>>()
        private val withStateQueue = mutableListOf<WithStateBlock<S>>()

        fun clear() {
            synchronized(this) {
                setStateQueue.clear()
                withStateQueue.clear()
            }
        }

        @CheckResult
        fun dequeueWithStateBlock(): WithStateBlock<S>? {
            return synchronized(this) {
                val stateQueue = withStateQueue
                if (stateQueue.size <= 0) {
                    return@synchronized null
                }

                // Remove the most recent withState operation
                return@synchronized stateQueue.removeAt(0)
            }
        }

        @CheckResult
        fun dequeueAllSetStateBlocks(): List<SetStateBlock<S>> {
            return synchronized(this) {
                if (setStateQueue.isEmpty()) {
                    return@synchronized emptyList()
                }

                val queue = setStateQueue.toList()
                setStateQueue.clear()
                return@synchronized queue
            }
        }

        fun enqueueWithState(block: WithStateBlock<S>) {
            return synchronized(this) {
                withStateQueue.add(block)
            }
        }

        fun enqueueSetState(block: SetStateBlock<S>) {
            return synchronized(this) {
                setStateQueue.add(block)
            }
        }
    }
}

typealias SetStateBlock<S> = S.() -> S
typealias WithStateBlock<S> = S.() -> Unit

