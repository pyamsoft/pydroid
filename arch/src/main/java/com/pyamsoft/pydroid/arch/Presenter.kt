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
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.Executors

abstract class Presenter<T : Any, C : Presenter.Callback<T>> protected constructor(
) : UiBinder<C>() {

  private val lock = Any()
  private val executor by lazy { Executors.newSingleThreadExecutor() }
  private val stateBus = RxBus.create<T.() -> T>()
  private var stateDisposable by singleDisposable()
  private var state: T? = null

  @CheckResult
  protected abstract fun initialState(): T

  final override fun internalOnBind() {
    stateDisposable = stateBus.listen()
        .map { func ->
          synchronized(lock) {
            val oldState = state
            val newState = nonNullState(oldState).run(func)
            state = newState
            return@map newState to oldState
          }
        }
        .subscribeOn(Schedulers.from(executor))
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe { callback.onRender(it.first, it.second) }
  }

  final override fun internalOnUnbind() {
    stateDisposable.tryDispose()
    executor.shutdown()
  }

  protected fun setState(func: T.() -> T) {
    stateBus.publish(func)
  }

  @CheckResult
  private fun nonNullState(state: T?): T {
    return state ?: initialState()
  }

  private data class PayLoad<T : Any>(
    val state: T,
    val oldState: T?
  )

  interface Callback<T : Any> : UiBinder.Callback {

    fun onRender(
      state: T,
      oldState: T?
    )

  }
}