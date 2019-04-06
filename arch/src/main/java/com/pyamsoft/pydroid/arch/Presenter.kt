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

  private var state: ViewState<T>? = null

  @CheckResult
  protected abstract fun initialState(): T

  protected fun setState(func: T.() -> Unit) {
    val oldState = state
    val newState = nonNullState(oldState).state.apply(func)
    state = ViewState(newState)
    callback.onRender(newState, oldState?.state)
  }

  @CheckResult
  private fun nonNullState(state: ViewState<T>?): ViewState<T> {
    if (state == null) {
      return ViewState(initialState())
    } else {
      return state.copy()
    }
  }

  interface Callback<T : Any> : UiBinder.Callback {

    fun onRender(
      state: T,
      oldState: T?
    )

  }

  private data class ViewState<M : Any>(val state: M)
}