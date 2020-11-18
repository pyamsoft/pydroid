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
import com.pyamsoft.pydroid.arch.debug.UiViewStateDebug
import com.pyamsoft.pydroid.core.Enforcer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import timber.log.Timber

abstract class UiStateViewModel<S : UiViewState> protected constructor(
    initialState: S,
) : ViewModel() {

    @Suppress("UNUSED_PARAMETER")
    @Deprecated(
        "\"debug\" parameter will be removed soon. Instead of a debug check to determine whether to run extra debug code, the debug check and code are removed via ProGuard rules. Be sure to assemble your release builds using ProGuard minification.",
        replaceWith = ReplaceWith("UiStateViewModel<S>(initialState)")
    )
    protected constructor(initialState: S, debug: Boolean) : this(initialState)

    // NOTE(Peter): Since state events run on their own single threaded dispatcher, we may not
    // need a mutex since there will only ever be one thread at a time.
    private val mutex = Mutex()

    private var state = UiVMState(initialState)

    @UiThread
    @CheckResult
    fun bind(vararg renderables: Renderable<S>): Job {
        return viewModelScope.launch(context = Dispatchers.Main) {
            bindState(renderables)
        }
    }

    @UiThread
    @CheckResult
    inline fun bind(crossinline onRender: (S) -> Unit): Job {
        return bind(Renderable { onRender(it) })
    }

    private fun onRender(renderables: Array<out Renderable<S>>, state: S) {
        renderables.forEach { it.render(state) }
    }

    // internal instead of protected so that only callers in the module can use this
    internal fun CoroutineScope.bindState(renderables: Array<out Renderable<S>>) {
        // Listen for any further state changes at this point
        bindStateEvents { onRender(renderables, it) }

        // Render the latest or initial state
        queueInOrder {
            handleStateChange(state.get()) { onRender(renderables, it) }
        }
    }

    @UiThread
    @CheckResult
    // internal instead of protected so that only callers in the module can use this
    internal fun getCurrentState(): S {
        Enforcer.assertOnMainThread()
        return state.get()
    }

    /**
     * Launches this coroutine on the single threaded main context
     * This ensures that the operation queued will run in order before any of the other operations after it
     *
     * Must be CoroutineScope extension to cancel correctly
     */
    internal inline fun CoroutineScope.queueInOrder(crossinline func: suspend CoroutineScope.() -> Unit) {
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
                processStateChange(isSetState = true) { func() }
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
                processStateChange(isSetState = false) { this.apply(func) }
            }
        }
    }

    private suspend inline fun processStateChange(isSetState: Boolean, stateChange: S.() -> S) {
        Enforcer.assertOffMainThread()

        mutex.withLock {
            val oldState = state.get()
            val newState = oldState.stateChange()

            // If we are in debug mode, perform the state change twice and make sure that it produces
            // the same state both times.
            if (isSetState) {
                UiViewStateDebug.checkStateEquality(newState, oldState.stateChange())
            }

            state.set(newState)
        }
    }

    private inline fun handleStateChange(
        state: S,
        onRender: (S) -> Unit
    ) {
        Timber.d("Render with state: $state")
        onRender(state)
    }

    private inline fun CoroutineScope.bindStateEvents(crossinline onRender: (S) -> Unit) {
        launch(context = Dispatchers.IO) {
            state.onChange { state ->
                withContext(context = Dispatchers.Main) {
                    handleStateChange(state) { onRender(it) }
                }
            }
        }
    }

    private class UiVMState<S : UiViewState>(initialState: S) {

        private val flow by lazy { MutableStateFlow(initialState) }

        @CheckResult
        fun get(): S {
            return flow.value
        }

        fun set(value: S) {
            flow.value = value
        }

        suspend fun onChange(withState: suspend (state: S) -> Unit) {
            flow.collect(withState)
        }
    }
}
