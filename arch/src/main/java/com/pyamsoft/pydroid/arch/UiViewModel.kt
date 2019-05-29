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
import com.pyamsoft.pydroid.core.bus.RxBus
import com.pyamsoft.pydroid.core.singleDisposable
import com.pyamsoft.pydroid.core.tryDispose
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.LinkedList
import java.util.concurrent.Executors

abstract class UiViewModel<S : UiViewState, V : UiViewEvent, C : UiControllerEvent> protected constructor(
  private val initialState: S
) : ViewModel() {

  private val lock = Any()
  private val controllerEventBus = RxBus.create<C>()
  private val stateBus = RxBus.create<S>()
  private val flushQueueBus = RxBus.create<Unit>()

  private var stateChangeDisposable by singleDisposable()
  private val stateChangeExecutor = Executors.newSingleThreadExecutor()
  private val stateChangeScheduler = Schedulers.from(stateChangeExecutor)

  private val stateExecutor = Executors.newSingleThreadExecutor()
  private val stateScheduler = Schedulers.from(stateExecutor)

  @Volatile private var stateQueue = LinkedList<S.() -> S>()
  @Volatile private var state: S? = null

  protected abstract fun handleViewEvent(event: V)

  init {
    stateChangeDisposable = flushQueueBus.listen()
        .subscribeOn(stateChangeScheduler)
        .observeOn(stateChangeScheduler)
        .subscribe { flushQueue() }
  }

  @PublishedApi
  @CheckResult
  internal fun render(
    savedInstanceState: Bundle?,
    vararg views: UiView<S, V>,
    onControllerEvent: (event: C) -> Unit
  ): Disposable {
    return ViewModelStream(savedInstanceState, views, onControllerEvent).render()
  }

  final override fun onCleared() {
    stateChangeDisposable.tryDispose()
    stateChangeExecutor.shutdown()
    stateChangeScheduler.shutdown()
    stateExecutor.shutdown()
    stateScheduler.shutdown()
    onTeardown()
  }

  protected open fun onTeardown() {

  }

  private fun controllerEvents(): Observable<C> {
    return controllerEventBus.listen()
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

  private inner class ViewModelStream internal constructor(
    savedInstanceState: Bundle?,
    views: Array<out UiView<S, V>>,
    onControllerEvent: (event: C) -> Unit
  ) {

    private var savedState: UiSavedState? = UiSavedState(savedInstanceState)
    private var views: Array<out UiView<S, V>>? = views
    private var onControllerEvent: ((event: C) -> Unit)? = onControllerEvent

    private var viewDisposable: Disposable? = null
    private var controllerDisposable: Disposable? = null

    @CheckResult
    internal fun render(
    ): Disposable {
      val stateDisposable = stateBus.listen()
          .startWith(latestState())
          .map { combineWithSavedState(it) }
          .subscribeOn(stateScheduler)
          .observeOn(AndroidSchedulers.mainThread())
          .doOnSubscribe {
            controllerDisposable = bindEvents(requireNotNull(onControllerEvent))
            viewDisposable = bindEvents(requireNotNull(views))
          }
          .doOnDispose {
            viewDisposable?.tryDispose()
            controllerDisposable?.tryDispose()
            viewDisposable = null
            controllerDisposable = null
          }
          .subscribe { e -> requireNotNull(views).forEach { it.render(e.state, e.savedState) } }

      return object : Disposable {
        override fun isDisposed(): Boolean {
          return stateDisposable.isDisposed
        }

        override fun dispose() {
          stateDisposable.tryDispose()

          savedState = null
          views = null
          onControllerEvent = null
        }

      }
    }

    @CheckResult
    private fun combineWithSavedState(state: S): StateWithSavedState<S> {
      return StateWithSavedState(state, requireNotNull(savedState))
    }

    @CheckResult
    private fun bindEvents(views: Array<out UiView<S, V>>): Disposable {
      return Observable.merge(views.map { it.viewEvents() })
          .subscribeOn(stateScheduler)
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe { handleViewEvent(it) }
    }

    @CheckResult
    private inline fun bindEvents(crossinline onControllerEvent: (event: C) -> Unit): Disposable {
      return controllerEvents()
          .subscribeOn(stateScheduler)
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe { onControllerEvent(it) }
    }

  }

  private data class StateWithSavedState<S : UiViewState>(
    val state: S,
    val savedState: UiSavedState
  )

}
