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
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield

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
  public fun compose(context: CoroutineContext = EmptyCoroutineContext): State<S> {
    return modelState.compose(context = context)
  }

  /**
   * Modify the state from the previous
   *
   * Note that, like calling this.setState() in React, this operation does not happen immediately.
   * Note that your stateChange block should be quick, it generally is just a simple
   * DataClass.copy() method.
   */
  public fun setState(stateChange: S.() -> S) {
    // Does not launch a coroutine for the change andThen
    processStateChange(stateChange)
  }

  /**
   * Modify the state from the previous
   *
   * Note that, like calling this.setState() in React, this operation does not happen immediately.
   * Note that your stateChange block should be quick, it generally is just a simple
   * DataClass.copy() method.
   *
   * The andThen callback will be fired after the state has changed and the view has been notified.
   * There is no threading guarantee for the andThen callback, though it currently fires in an IO
   * context
   */
  public fun CoroutineScope.setState(
      stateChange: S.() -> S,
      andThen: suspend CoroutineScope.(newState: S) -> Unit
  ) {
    processStateChange { stateChange(it) }

    // Launch an andThen which
    this.launch(context = Dispatchers.IO) {
      // Do any other scope related work first since the andThen can be fired at any time.
      yield()

      val newState = state
      andThen(newState)
    }
  }

  /** Return the new state, which may be the same as the old state */
  private inline fun processStateChange(handleChange: (S) -> S) {
    // Use this mutex to make sure that setState changes happen in the order they are called.
    val oldState = state
    val newState = handleChange(oldState)

    // If we are in debug mode, perform the state change twice and make sure that it produces
    // the same state both times.
    UiViewStateDebug.checkStateEquality(newState) { handleChange(oldState) }

    // Update newState if it has changed
    if (oldState != newState) {
      modelState.set(newState)
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
    fun compose(context: CoroutineContext): State<S> {
      return flow.collectAsState(context = context)
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
