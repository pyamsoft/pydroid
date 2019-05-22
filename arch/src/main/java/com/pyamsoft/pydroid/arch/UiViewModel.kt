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

import androidx.annotation.CheckResult
import com.pyamsoft.pydroid.core.bus.RxBus
import com.pyamsoft.pydroid.core.tryDispose
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.LinkedList
import java.util.concurrent.Executors

abstract class UiViewModel<S : UiViewState, V : UiViewEvent, C : UiControllerEvent> protected constructor(
  private val initialState: S
) {

  private val lock = Any()
  private val controllerEventBus = RxBus.create<C>()
  private val stateBus = RxBus.create<S>()
  private val flushQueueBus = RxBus.create<Unit>()

  @Volatile private var stateQueue = LinkedList<S.() -> S>()
  @Volatile private var state: S? = null

  protected abstract fun handleViewEvent(event: V)

  @PublishedApi
  @CheckResult
  internal fun render(
    vararg views: UiView<S, V>,
    onControllerEvent: (event: C) -> Unit
  ): Disposable {

    val stateChangeExecutor = Executors.newSingleThreadExecutor()
    val stateChangeScheduler = Schedulers.from(stateChangeExecutor)
    val stateChangeDisposable = flushQueueBus.listen()
        .subscribeOn(stateChangeScheduler)
        .observeOn(stateChangeScheduler)
        .subscribe { flushQueue() }

    val stateExecutor = Executors.newSingleThreadExecutor()
    val stateScheduler = Schedulers.from(stateExecutor)
    var viewDisposable: Disposable? = null
    var controllerDisposable: Disposable? = null
    val stateDisposable = stateBus.listen()
        .startWith(latestState())
        .subscribeOn(stateScheduler)
        .observeOn(AndroidSchedulers.mainThread())
        .doOnSubscribe {
          controllerDisposable = bindControllerEvents(stateScheduler, onControllerEvent)
          viewDisposable = bindViewEvents(stateScheduler, *views)
        }
        .doOnDispose {
          viewDisposable?.tryDispose()
          controllerDisposable?.tryDispose()
          viewDisposable = null
          controllerDisposable = null
        }
        .subscribe { s -> views.forEach { it.render(s) } }

    return object : Disposable {
      override fun isDisposed(): Boolean {
        return stateDisposable.isDisposed && stateChangeDisposable.isDisposed
      }

      override fun dispose() {
        stateDisposable.tryDispose()
        stateChangeDisposable.tryDispose()

        onCleared()

        stateExecutor.shutdown()
        stateScheduler.shutdown()
        stateChangeExecutor.shutdown()
        stateChangeScheduler.shutdown()
      }

    }
  }

  protected open fun onCleared() {
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

  @CheckResult
  private fun bindViewEvents(
    scheduler: Scheduler,
    vararg views: UiView<S, V>
  ): Disposable {
    return Observable.merge(views.map { it.viewEvents() })
        .subscribeOn(scheduler)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe { handleViewEvent(it) }
  }

  @CheckResult
  private inline fun bindControllerEvents(
    scheduler: Scheduler,
    crossinline onControllerEvent: (event: C) -> Unit
  ): Disposable {
    return controllerEvents()
        .subscribeOn(scheduler)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe { onControllerEvent(it) }
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
}
