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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pyamsoft.pydroid.core.Enforcer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import timber.log.Timber

abstract class UiStateViewModel<S : UiViewState, V : UiViewEvent, C : UiControllerEvent> protected constructor(
    initialState: S,
    private val debug: Boolean
) : ViewModel() {

    // NOTE(Peter): Since state events run on their own single threaded dispatcher, we may not
    // need a mutex since there will only ever be one thread at a time.
    private val mutex = Mutex()

    // This useless interface exists just so I don't have to mark everything as experimental
    private var state: UiVMState<S> = UiVMStateImpl(initialState)

    @UiThread
    @CheckResult
    fun bind(onRender: (S) -> Unit) = viewModelScope.launch(context = Dispatchers.Main) {
        bindState { onRender(it) }
    }

    protected fun CoroutineScope.bindState(onRender: (S) -> Unit) {
        // Listen for any further state changes at this point
        queueInOrder { bindStateEvents { onRender(it) } }

        // Render the latest or initial state
        queueInOrder {
            handleStateChange(getCurrentState()) { onRender(it) }
        }
    }

    /**
     * Get the current state
     */
    @UiThread
    @CheckResult
    protected fun getCurrentState(): S {
        Enforcer.assertOnMainThread()
        return state.get()
    }

    /**
     * Launches this coroutine on the single threaded main context
     * This ensures that the operation queued will run in order before any of the other operations after it
     *
     * Must be CoroutineScope extension to cancel correctly
     */
    protected inline fun CoroutineScope.queueInOrder(crossinline func: suspend CoroutineScope.() -> Unit) {
        launch(context = Dispatchers.Main) { func() }
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

    private inline fun handleStateChange(
        state: S,
        onRender: (S) -> Unit
    ) {
        Timber.d("Render with state: $state")
        onRender(state)
    }

    private suspend inline fun bindStateEvents(crossinline onRender: (S) -> Unit) {
        withContext(context = Dispatchers.IO) {
            state.onChange { state ->
                withContext(context = Dispatchers.Main) {
                    handleStateChange(state) { onRender(it) }
                }
            }
        }
    }

    private class DeterministicStateError(
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

    private class UiVMStateImpl<S : UiViewState>(
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
