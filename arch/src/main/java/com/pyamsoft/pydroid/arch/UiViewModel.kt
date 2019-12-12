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
import java.util.LinkedList
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.LazyThreadSafetyMode.NONE
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

abstract class UiViewModel<S : UiViewState, V : UiViewEvent, C : UiControllerEvent> protected constructor(
    private val initialState: S
) : ViewModel() {

    private val isInitialized = AtomicBoolean(false)
    private val onInitEventDelegate = lazy(NONE) { mutableListOf<() -> Unit>() }
    private val onInitEvents by onInitEventDelegate
    private val onTeardownEventDelegate = lazy(NONE) { mutableListOf<() -> Unit>() }
    private val onTeardownEvents by onTeardownEventDelegate

    private val mutex = Mutex()
    private val controllerEventBus = EventBus.create<C>()
    private val stateBus = EventBus.create<S>()
    private val flushQueueBus = EventBus.create<FlushQueueEvent>()

    @Volatile
    private var stateQueue = LinkedList<S.() -> S>()
    @Volatile
    private var state: S? = null

    init {
        flushQueueBus.scopedEvent(Dispatchers.Default) { flushQueue() }
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
    protected fun doOnInit(onInit: () -> Unit) {
        onInitEvents.add(onInit)
    }

    protected abstract fun handleViewEvent(event: V)

    @CheckResult
    @PublishedApi
    internal fun render(
        savedInstanceState: Bundle?,
        vararg views: UiView<S, V>,
        onControllerEvent: (event: C) -> Unit
    ): Job = viewModelScope.launch {
        // Init savedState once
        val savedState = UiSavedState(savedInstanceState)

        // Listen for changes
        launch(context = Dispatchers.Default) {
            stateBus.onEvent { state ->
                withContext(context = Dispatchers.Main) {
                    handleStateChange(
                        views,
                        state,
                        savedState
                    )
                }
            }
        }

        // Bind ViewModel
        bindEvents(onControllerEvent)
        views.forEach { bindEvent(it) }

        // Initialize before first render
        initialize()

        val currentState = latestState()
        handleStateChange(views, currentState, savedState)
    }

    final override fun onCleared() {
        if (isInitialized.compareAndSet(true, false)) {
            if (onTeardownEventDelegate.isInitialized()) {

                // Reverse the list order so that we teardown in LIFO order
                onTeardownEvents.reverse()

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
        } else {
            Timber.w("Teardown is already complete.")
        }
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

    @JvmOverloads
    protected fun <E : Any> EventBus<E>.scopedEvent(
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

    protected fun setState(func: S.() -> S) {
        viewModelScope.launch(context = Dispatchers.Default) {
            mutex.withLock {
                stateQueue.add(func)
            }

            flushQueueBus.send(FlushQueueEvent)
        }
    }

    protected fun withState(func: S.() -> Unit) {
        setState {
            func(this)
            return@setState this
        }
    }

    @CheckResult
    private fun dequeueAllPendingStateChanges(): List<S.() -> S> {
        if (stateQueue.isEmpty()) {
            return emptyList()
        }

        val queue = stateQueue
        stateQueue = LinkedList()
        return queue.toList()
    }

    private suspend fun flushQueue() {
        mutex.withLock {
            val stateChanges = dequeueAllPendingStateChanges()
            if (stateChanges.isEmpty()) {
                Timber.w("State queue is empty, ignore flush.")
                return
            }

            for (stateChange in stateChanges) {
                val newState = latestState().stateChange()
                if (newState != state) {
                    state = newState
                    stateBus.send(newState)
                }
            }
        }
    }

    private fun handleStateChange(
        views: Array<out Renderable<S>>,
        state: S,
        savedState: UiSavedState
    ) {
        val combined = combineWithSavedState(state, savedState)
        views.forEach { it.render(combined.state, combined.savedState) }
    }

    private fun initialize() {
        if (isInitialized.compareAndSet(false, true)) {
            // Only run the init hooks if they exist, otherwise we don't need to init the memory
            if (onInitEventDelegate.isInitialized()) {

                // Call init hooks in FIFO order
                for (initEvent in onInitEvents) {
                    initEvent()
                }

                // Clear the init hooks list to free up memory
                onInitEvents.clear()
            }
        } else {
            Timber.w("Initialization is already complete.")
        }
    }

    @CheckResult
    private fun combineWithSavedState(
        state: S,
        savedState: UiSavedState
    ): StateWithSavedState<S> {
        return StateWithSavedState(state, savedState)
    }

    private fun CoroutineScope.bindEvent(view: UiView<S, V>) =
        launch(context = Dispatchers.Default) {
            view.onViewEvent { event ->
                withContext(context = Dispatchers.Main) { handleViewEvent(event) }
            }
        }

    private inline fun CoroutineScope.bindEvents(crossinline onControllerEvent: (event: C) -> Unit) =
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

    private data class StateWithSavedState<S : UiViewState>(
        val state: S,
        val savedState: UiSavedState
    )

    private object FlushQueueEvent
}
