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
import com.pyamsoft.pydroid.core.singleDisposable
import com.pyamsoft.pydroid.core.tryDispose
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.Executors

abstract class UiViewModel<T : UiState> protected constructor(
  private val initialState: T
) {

  // Lifecycle
  private var bound: Boolean
  private val disposables = CompositeDisposable()

  private var callback: ((state: T, oldState: T?) -> Unit)? = null

  // State handling
  private val lock = Any()
  private val executor by lazy { Executors.newSingleThreadExecutor() }
  private val stateBus = RxBus.create<T.() -> T>()
  private var stateDisposable by singleDisposable()
  private var state: T? = null

  init {
    bound = false
  }

  protected abstract fun onBind()

  protected abstract fun onUnbind()

  fun bind(onRender: (state: T, oldState: T?) -> Unit) {
    // We should not need to synchronize since this should always be called on the main thread
    if (!bound) {
      bound = true
      callback = onRender
      register()
      onBind()
    }
  }

  fun unbind() {
    if (bound) {
      bound = false
      callback = null
      disposables.clear()
      shutdown()
      onUnbind()
    }
  }

  private fun register() {
    stateDisposable = stateBus.listen()
        .map { changeState(it) }
        .subscribeOn(Schedulers.from(executor))
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe { requireNotNull(callback).invoke(it.state, it.oldState) }
  }

  @CheckResult
  private fun nonNullState(state: T?): T {
    return state ?: initialState
  }

  @CheckResult
  private fun changeState(stateChange: T.() -> T): StateChange<T> {
    synchronized(lock) {
      val oldState = state
      val newState = nonNullState(oldState).run(stateChange)
      state = newState
      return StateChange(newState, oldState)
    }
  }

  private fun shutdown() {
    stateDisposable.tryDispose()
    executor.shutdown()
  }

  protected fun setState(func: T.() -> T) {
    stateBus.publish(func)
  }

  /**
   * For an operation which is always called with the same data, in order to be caught correctly
   * by the onRender callback we generally need to first set the value of whatever field back to
   * its initial state, and then immediately update it to the proper value.
   */
  protected fun <M : Any> setUniqueState(
    value: M,
    old: (state: T) -> M,
    applyValue: (state: T, value: M) -> T
  ) {
    setState {
      if (old(this) == value) {
        applyValue(this, old(initialState)).also {
          setUniqueState(value, old, applyValue)
        }
      } else {
        applyValue(this, value)
      }
    }
  }

  protected fun Disposable.destroy() {
    disposables.add(this)
  }

  private data class StateChange<T : Any>(
    val state: T,
    val oldState: T?
  )
}
