/*
 * Copyright 2017 Peter Kenji Yamanaka
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

package com.pyamsoft.pydroid.presenter

import com.pyamsoft.pydroid.helper.add
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class Presenter<V : Any> protected constructor() {

  protected var view: V? = null
  private val stopDisposables: CompositeDisposable = CompositeDisposable()

  fun start(bound: V) {
    view = bound
    onStart(bound)
  }

  /**
   * Override per implementation
   */
  protected open fun onStart(bound: V) {
    // Intentionally empty
  }

  fun stop() {
    val bound: V? = view
    if (bound != null) {
      onStop(bound)
    }
    stopDisposables.clear()
    view = null
  }

  /**
   * Override per implementation
   */
  protected open fun onStop(bound: V) {

  }

  /**
   * Add a disposable to the internal list, dispose it onStop
   */
  protected fun disposeOnStop(func: () -> Disposable) {
    stopDisposables.add(func)
  }

  /**
   * Add a disposable to the internal list, dispose it onStop
   */
  protected fun disposeOnStop(disposable: Disposable) {
    stopDisposables.add(disposable)
  }
}
