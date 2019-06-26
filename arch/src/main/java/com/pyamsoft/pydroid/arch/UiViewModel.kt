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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.LinkedList
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

@ExperimentalCoroutinesApi
abstract class UiViewModel<S : UiViewState, V : UiViewEvent, C : UiControllerEvent> protected constructor(
  private val initialState: S
) : ViewModel() {

  private val lock = Any()
  private val controllerEventBus = EventBus.create<C>()
  private val stateBus = EventBus.create<S>()
  private val flushQueueBus = EventBus.create<Unit>()

  @Volatile private var stateQueue = LinkedList<S.() -> S>()
  @Volatile private var state: S? = null

  init {
    flushQueueBus.scopedEvent(Dispatchers.Default) { flushQueue() }
  }

  protected abstract fun handleViewEvent(event: V)

  @PublishedApi
  @CheckResult
  internal fun render(
    savedInstanceState: Bundle?,
    vararg views: UiView<S, V>,
    onControllerEvent: (event: C) -> Unit
  ): Job {
    return viewModelScope.launch(context = Dispatchers.Default) {
      bindEvents(onControllerEvent)
      bindEvents(views)

      val savedState = UiSavedState(savedInstanceState)
      handleStateChange(views, latestState(), savedState)
      stateBus.onEvent { state ->
        handleStateChange(views, state, savedState)
      }
    }
  }

  final override fun onCleared() {
    onTeardown()
  }

  protected open fun onTeardown() {

  }

  @JvmOverloads
  protected fun <E : Any> EventBus<E>.scopedEvent(
    context: CoroutineContext = EmptyCoroutineContext,
    func: (event: E) -> Unit
  ): Job {
    val bus = this
    return viewModelScope.launch(context = context) { bus.onEvent(func) }
  }

  protected fun publish(event: C) {
    controllerEventBus.publish(event)
  }

  @CheckResult
  private fun latestState(): S {
    return state ?: initialState
  }

  protected fun setState(func: S.() -> S) {
    synchronized(lock) {
      stateQueue.add(func)
    }
    flushQueueBus.publish(Unit)
  }

  @CheckResult
  private fun dequeueAllPendingStateChanges(): List<S.() -> S> {
    synchronized(lock) {
      if (stateQueue.isEmpty()) {
        return emptyList()
      }

      val queue = stateQueue
      stateQueue = LinkedList()
      return queue
    }
  }

  private fun flushQueue() {
    val stateChanges = dequeueAllPendingStateChanges()
    if (stateChanges.isEmpty()) {
      return
    }

    for (stateChange in stateChanges) {
      val newState = latestState().stateChange()
      if (newState != state) {
        synchronized(lock) {
          state = newState
        }
        stateBus.publish(newState)
      }
    }
  }

  private fun CoroutineScope.handleStateChange(
    views: Array<out UiView<S, V>>,
    state: S,
    savedState: UiSavedState
  ) = launch {
    val combined = combineWithSavedState(state, savedState)
    views.forEach {
      launch(context = Dispatchers.Main) { it.render(combined.state, combined.savedState) }
    }
  }

  @CheckResult
  private fun combineWithSavedState(
    state: S,
    savedState: UiSavedState
  ): StateWithSavedState<S> {
    return StateWithSavedState(state, savedState)
  }

  private fun CoroutineScope.bindEvents(views: Array<out UiView<S, V>>) = launch {
    views.forEach { v ->
      launch {
        v.onViewEvent { event ->
          launch(context = Dispatchers.Main) { handleViewEvent(event) }
        }
      }
    }
  }

  private inline fun CoroutineScope.bindEvents(crossinline onControllerEvent: (event: C) -> Unit) =
    launch {
      controllerEventBus.onEvent { event ->
        launch(context = Dispatchers.Main) { onControllerEvent(event) }
      }
    }

  private data class StateWithSavedState<S : UiViewState>(
    val state: S,
    val savedState: UiSavedState
  )

}
