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

package com.pyamsoft.pydroid.arch.impl

import androidx.annotation.CheckResult
import com.pyamsoft.pydroid.arch.UiControllerEvent
import com.pyamsoft.pydroid.arch.UiView
import com.pyamsoft.pydroid.arch.UiViewEvent
import com.pyamsoft.pydroid.arch.UiViewModel
import com.pyamsoft.pydroid.arch.UiViewState
import com.pyamsoft.pydroid.core.bus.RxBus
import com.pyamsoft.pydroid.core.tryDispose
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

abstract class BaseUiViewModel<S : UiViewState, V : UiViewEvent, C : UiControllerEvent> protected constructor(
  private val initialState: S
) : UiViewModel<S, V, C> {

  private val controllerEventBus = RxBus.create<C>()

  private val lock = Any()
  private val stateBus = RxBus.create<S.() -> S>()
  private var state: S? = null

  @CheckResult
  final override fun render(
    vararg views: UiView<S, V>,
    onControllerEvent: (event: C) -> Unit
  ): Disposable {
    val executor = Executors.newSingleThreadExecutor()
    val scheduler = Schedulers.from(executor)

    var viewDisposable: Disposable? = null
    var controllerDisposable: Disposable? = null

    return stateBus.listen()
        .distinctUntilChanged()
        .startWith { initialState }
        .map { changeState(it) }
        .subscribeOn(scheduler)
        .observeOn(AndroidSchedulers.mainThread())
        .doOnSubscribe {
          synchronized(lock) {
            onBind()
            viewDisposable = bindViewEvents(scheduler, *views)
            controllerDisposable = bindControllerEvents(scheduler, onControllerEvent)
          }
        }
        .doOnTerminate { cleanup(viewDisposable, controllerDisposable, executor, scheduler) }
        .doOnDispose { cleanup(viewDisposable, controllerDisposable, executor, scheduler) }
        .subscribe { change -> views.forEach { it.render(change.state, change.oldState) } }
  }

  private fun cleanup(
    viewDisposable: Disposable?,
    controllerDisposable: Disposable?,
    executor: ExecutorService,
    scheduler: Scheduler
  ) {
    synchronized(lock) {
      onUnbind()

      viewDisposable?.tryDispose()
      controllerDisposable?.tryDispose()

      executor.shutdown()
      scheduler.shutdown()
    }
  }

  private fun controllerEvents(): Observable<C> {
    return controllerEventBus.listen()
  }

  protected fun publish(event: C) {
    controllerEventBus.publish(event)
  }

  @CheckResult
  private fun nonNullState(state: S?): S {
    return state ?: initialState
  }

  @CheckResult
  private fun changeState(stateChange: S.() -> S): StateChange<S> {
    synchronized(lock) {
      val oldState = state
      val newState = nonNullState(oldState).run(stateChange)
      state = newState
      return StateChange(newState, oldState)
    }
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

  protected abstract fun handleViewEvent(event: V)

  protected open fun onBind() {
  }

  protected open fun onUnbind() {
  }

  protected fun setState(func: S.() -> S) {
    stateBus.publish(func)
  }

  private data class StateChange<T : Any>(
    val state: T,
    val oldState: T?
  )
}
