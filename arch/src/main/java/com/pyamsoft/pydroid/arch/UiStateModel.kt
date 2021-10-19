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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import com.pyamsoft.pydroid.arch.debug.UiViewStateDebug
import com.pyamsoft.pydroid.core.Enforcer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

/**
 * A State model managing a single state object.
 *
 * Access the current state via state and manipulate it via setState. The setState call is
 * asynchronous.
 */
public open class UiStateModel<S : UiViewState>(
    /** Initial state */
    public val initialState: S,
) {

  // Mutex to make sure that setState operations happen in order
  private val mutex = Mutex()

  private val modelState = MutableUiVMState(MutableStateFlow(initialState))

  /**
   * The current state
   *
   * The StateFlow implementation should be thread/coroutine safe, so this can be called from
   * anywhere.
   */
  public val state: S
    @get:CheckResult
    get() {
      return modelState.get()
    }

  /** Get VM state as a Composable object */
  @Composable
  @CheckResult
  public fun compose(): State<S> {
    return modelState.compose()
  }

  /**
   * Modify the state from the previous
   *
   * Note that, like calling this.setState() in React, this operation does not happen immediately.
   */
  public fun CoroutineScope.setState(stateChange: suspend S.() -> S) {
    this.setState(stateChange = stateChange, andThen = {})
  }

  /**
   * Modify the state from the previous
   *
   * Note that, like calling this.setState() in React, this operation does not happen immediately.
   *
   * The andThen callback will be fired after the state has changed and the view has been notified.
   *
   * There is no threading guarantee for the andThen callback
   */
  public fun CoroutineScope.setState(
      stateChange: suspend S.() -> S,
      andThen: suspend CoroutineScope.(newState: S) -> Unit
  ) {
    this.launch(context = Dispatchers.IO) {
      val newState = processStateChange { stateChange(it) }
      andThen(newState)
    }
  }

  /** Return the new state, which may be the same as the old state */
  @CheckResult
  private suspend inline fun processStateChange(handleChange: (S) -> S): S {
    Enforcer.assertOffMainThread()

    // Use this mutex to make sure that setState changes happen in the order they are called.
    return mutex.withLock {
      val oldState = state
      val newState = handleChange(oldState)

      // If we are in debug mode, perform the state change twice and make sure that it produces
      // the same state both times.
      UiViewStateDebug.checkStateEquality(newState) { handleChange(oldState) }

      return@withLock if (oldState == newState) newState else newState.also { modelState.set(it) }
    }
  }

  /** Clear the state model */
  @UiThread @CallSuper public open fun clear() {}

  /**
   * Bind renderables to this ViewModel.
   *
   * Once bound, any changes to the ViewModel.state will be sent to these renderables.
   */
  @UiThread
  @CheckResult
  @Deprecated("Migrate to Jetpack Compose")
  public fun bindState(scope: CoroutineScope, vararg renderables: Renderable<S>): Job {
    return scope.launch(context = Dispatchers.Main) { internalBindState(renderables) }
  }

  // internal instead of protected so that only callers in the module can use this
  @Deprecated("Migrate to Jetpack Compose")
  internal suspend fun internalBindState(renderables: Array<out Renderable<S>>) {
    val state = modelState

    withContext(context = Dispatchers.Main) { renderables.forEach { it.render(state) } }
  }

  private class MutableUiVMState<S : UiViewState>(private val flow: MutableStateFlow<S>) :
      UiVMState<S>(flow) {

    @Composable
    @CheckResult
    fun compose(): State<S> {
      return flow.collectAsState()
    }

    @CheckResult
    fun get(): S {
      return flow.value
    }

    fun set(value: S) {
      flow.value = value
    }
  }

  /**
   * This is a separate interface so that mapChanged will not expose the implementation
   * MutableUiVMState get() or set() functions
   */
  private open class UiVMState<S>(private val flow: Flow<S>) : UiRender<S> {

    override fun <T> mapChanged(change: (state: S) -> T): UiRender<T> {
      return UiVMState(flow.map { change(it) }.distinctUntilChanged())
    }

    final override fun render(scope: CoroutineScope, onRender: (state: S) -> Unit) {
      scope.launch(context = Dispatchers.IO) {
        flow.collect { state -> withContext(context = Dispatchers.Main) { onRender(state) } }
      }
    }
  }
}
