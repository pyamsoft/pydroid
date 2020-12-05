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

import androidx.annotation.CallSuper
import androidx.annotation.CheckResult
import androidx.annotation.UiThread
import com.pyamsoft.pydroid.arch.debug.UiViewStateDebug
import com.pyamsoft.pydroid.core.Enforcer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import timber.log.Timber

/**
 * A State model managing a single state object.
 *
 * Access the current state via withState and manipulate it via setState.
 * These calls are asynchronous.
 */
public open class UiStateModel<S : UiViewState> @JvmOverloads constructor(
    /**
     * Initial state
     */
    public val initialState: S,

    /**
     * Coroutine Scope
     */
    public val stateModelScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
) {

    // Mutex to make sure that setState operations happen in order
    private val mutex = Mutex()

    private var modelState = UiVMState(initialState)

    /**
     * The current state
     *
     * The StateFlow implementation should be thread/coroutine safe, so this can be called from anywhere.
     */
    public val state: S
        @get:CheckResult get() {
            return modelState.get()
        }

    /**
     * Modify the state from the previous
     *
     * Note that, like calling this.setState() in React, this operation does not happen immediately.
     */
    public fun setState(stateChange: S.() -> S) {
        setState(stateChange = stateChange, andThen = {})
    }

    /**
     * Modify the state from the previous
     *
     * Note that, like calling this.setState() in React, this operation does not happen immediately.
     *
     * The andThen callback will be fired after the state has changed and the view has been notified.
     * If the stateChange payload does not cause a state update, the andThen call will not be fired.
     *
     * There is no threading guarantee for the andThen callback
     */
    public fun setState(stateChange: S.() -> S, andThen: suspend (newState: S) -> Unit) {
        stateModelScope.launch(context = Dispatchers.Main) {
            withContext(context = Dispatchers.Default) {
                processStateChange(
                    isSetState = true,
                    stateChange = stateChange,
                )?.also { andThen(it) }
            }
        }
    }

    /**
     * Act upon the current state
     *
     * Note that like accessing state in React using this.state.<var>, this is not immediate and
     * may not be up to date with the latest setState() call.
     */
    @Deprecated("Use the state variable directly to access the current state. To access state after a setState call, chain a follow up andThen() call")
    public fun withState(func: S.() -> Unit) {
        stateModelScope.launch(context = Dispatchers.Main) {
            // Yield to any setState calls happening at this point
            yield()

            withContext(context = Dispatchers.Default) {
                processStateChange(
                    isSetState = false,
                    stateChange = { this.apply(func) },
                )
            }
        }
    }

    /**
     * Clear the state model
     */
    @UiThread
    @CallSuper
    public open fun clear() {
        stateModelScope.cancel()
    }

    /**
     * Bind renderables to this ViewModel.
     *
     * Once bound, any changes to the ViewModel.state will be sent to these renderables.
     */
    @UiThread
    @CheckResult
    public fun bind(vararg renderables: Renderable<S>): Job {
        return stateModelScope.launch(context = Dispatchers.Main) {
            bindState(this, renderables)
        }
    }

    private fun onRender(renderables: Array<out Renderable<S>>, state: S) {
        renderables.forEach { it.render(state) }
    }

    // internal instead of protected so that only callers in the module can use this
    internal fun bindState(scope: CoroutineScope, renderables: Array<out Renderable<S>>) {
        // Listen for any further state changes at this point
        scope.bindStateEvents { onRender(renderables, it) }

        // Render the latest or initial state
        handleStateChange(state) { onRender(renderables, it) }
    }

    /**
     * Return the newState if it has changed or null if it has not
     */
    private suspend inline fun processStateChange(
        isSetState: Boolean,
        stateChange: S.() -> S
    ): S? {
        Enforcer.assertOffMainThread()

        // Use this mutex to make sure that setState changes happen in the order they are called.
        return mutex.withLock {
            val oldState = state
            val newState = oldState.stateChange()

            // If we are in debug mode, perform the state change twice and make sure that it produces
            // the same state both times.
            if (isSetState) {
                UiViewStateDebug.checkStateEquality(newState, oldState.stateChange())
            }

            return@withLock if (oldState == newState) null else {
                newState.also { modelState.set(it) }
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

    private inline fun CoroutineScope.bindStateEvents(crossinline onRender: (S) -> Unit) {
        launch(context = Dispatchers.IO) {
            modelState.onChange { state ->
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
