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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pyamsoft.pydroid.core.Enforcer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job

/**
 * A ViewModel implementation which models a single state object.
 *
 * Access the current state via withState and manipulate it via setState.
 * These calls are asynchronous.
 */
public abstract class UiStateViewModel<S : UiViewState> protected constructor(
    initialState: S,
) : ViewModel() {

    private val delegate = UiStateModel(initialState, viewModelScope)

    /**
     * The current state
     *
     * The StateFlow implementation should be thread/coroutine safe, so this can be called from anywhere.
     */
    protected val state: S
        @get:CheckResult get() {
            return delegate.state
        }

    /**
     * Bind renderables to this ViewModel.
     *
     * Once bound, any changes to the ViewModel.state will be sent to these renderables.
     */
    @UiThread
    @CheckResult
    public fun bind(vararg renderables: Renderable<S>): Job {
        return delegate.bind(*renderables)
    }

    /**
     * Bind a renderable to this ViewModel.
     *
     * Once bound, any changes to the ViewModel.state will be sent to this renderable.
     */
    @UiThread
    @CheckResult
    public inline fun bind(crossinline onRender: (UiRender<S>) -> Unit): Job {
        return bind(Renderable { onRender(it) })
    }

    // internal instead of protected so that only callers in the module can use this
    internal suspend fun bindState(renderables: Array<out Renderable<S>>) {
        delegate.bindState(renderables)
    }

    /**
     * Modify the state from the previous
     *
     * Note that, like calling this.setState() in React, this operation does not happen immediately.
     */
    protected fun setState(stateChange: suspend S.() -> S) {
        delegate.setState(stateChange)
    }

    /**
     * Modify the state from the previous
     *
     * Note that, like calling this.setState() in React, this operation does not happen immediately.
     */
    protected fun CoroutineScope.setState(stateChange: suspend S.() -> S) {
        val scope = this

        // Call the extension on the delegate
        delegate.apply {
            scope.setState(stateChange)
        }
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
    protected fun setState(stateChange: suspend S.() -> S, andThen: suspend (newState: S) -> Unit) {
        delegate.setState(stateChange, andThen)
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
    protected fun CoroutineScope.setState(
        stateChange: suspend S.() -> S,
        andThen: suspend (newState: S) -> Unit
    ) {
        val scope = this
        delegate.apply {
            scope.setState(stateChange, andThen)
        }
    }

    /**
     * Clear the view model
     */
    @CallSuper
    override fun onCleared() {
        super.onCleared()
        Enforcer.assertOnMainThread()

        delegate.clear()
    }
}
