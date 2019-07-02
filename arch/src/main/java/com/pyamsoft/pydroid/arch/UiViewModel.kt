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
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.LinkedList
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

abstract class UiViewModel<S : UiViewState, V : UiViewEvent, C : UiControllerEvent> protected constructor(
  private val initialState: S
) : ViewModel() {

  private val isInitialized = AtomicBoolean(false)
  private val mutex = Mutex()
  private val controllerEventBus = EventBus.create<C>()
  private val stateBus = EventBus.create<S>()
  private val flushQueueBus = EventBus.create<Unit>()

  @Volatile private var stateQueue = LinkedList<S.() -> S>()
  @Volatile private var state: S? = null

  init {
    flushQueueBus.scopedEvent(Dispatchers.Default) { flushQueue() }
  }

  protected abstract fun onInit()

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
              views, state, savedState, fromInitialState = false
          )
        }
      }
    }

    // Push most recent state
    val mostRecentState = latestState()
    handleStateChange(views, mostRecentState, savedState, fromInitialState = true)

    // Bind ViewModel
    bindEvents(onControllerEvent)
    views.forEach { bindEvent(it) }
    initialize()

    // If most recent and latest get out of sync, do it again
    // This should not happen, but we leave it around just in case - since it was happening at one point.
    val currentState = latestState()
    if (currentState != mostRecentState) {
      Timber.w("State is out of sync, re-emit. Old: $mostRecentState -- New $currentState")
      handleStateChange(views, currentState, savedState, fromInitialState = true)
    }
  }

  final override fun onCleared() {
    if (isInitialized.compareAndSet(true, false)) {
      onTeardown()
    } else {
      Timber.w("Teardown is already complete.")
    }
  }

  protected open fun onTeardown() {

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
    controllerEventBus.publish(event)
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

      flushQueueBus.publish(Unit)
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

  private suspend fun flushQueue() = coroutineScope {
    mutex.withLock {
      val stateChanges = dequeueAllPendingStateChanges()
      if (stateChanges.isEmpty()) {
        Timber.w("State queue is empty, ignore flush.")
        return@coroutineScope
      }

      for (stateChange in stateChanges) {
        val newState = latestState().stateChange()
        if (newState != state) {
          state = newState

          stateBus.publish(newState)
        }
      }
    }
  }

  private fun handleStateChange(
    views: Array<out UiView<S, V>>,
    state: S,
    savedState: UiSavedState,
    fromInitialState: Boolean
  ) {
    val combined = combineWithSavedState(state, savedState)
    Timber.d("Handle ${if (fromInitialState) "initial" else "new"} state change: $state")
    views.forEach { it.render(combined.state, combined.savedState) }
  }

  private fun initialize() {
    if (isInitialized.compareAndSet(false, true)) {
      onInit()
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

  private fun CoroutineScope.bindEvent(view: UiView<S, V>) = launch(context = Dispatchers.Default) {
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

  private data class StateWithSavedState<S : UiViewState>(
    val state: S,
    val savedState: UiSavedState
  )

}
