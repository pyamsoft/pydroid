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

abstract class Presenter<T : Any, C : Presenter.Callback<T>> protected constructor(

) : UiBinder<C>() {

  private var state: T? = null

  @CheckResult
  protected abstract fun initialState(): T

  protected fun setState(func: T.() -> T) {
    val oldState = state
    val newState = nonNullState(oldState).run(func)
    state = newState
    callback.onRender(newState, oldState)
  }

  @CheckResult
  private fun nonNullState(state: T?): T {
    return state ?: initialState()
  }

  interface Callback<T : Any> : UiBinder.Callback {

    fun onRender(
      state: T,
      oldState: T?
    )

  }
}