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

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class Presenter<in V : Any> protected constructor() {

  private val disposables: CompositeDisposable = CompositeDisposable()

  fun bind(v: V) {
    onBind(v)
  }

  /**
   * Override per implementation
   */
  protected open fun onBind(v: V) {
    // Intentionally empty
  }

  fun unbind() {
    onUnbind()
    disposables.clear()
  }

  /**
   * Override per implementation
   */
  protected open fun onUnbind() {
    // Intentionally empty
  }

  /**
   * Add a disposable to the internal list, dispose it onUnbind
   */
  protected inline fun dispose(func: () -> Disposable) {
    dispose(func())
  }

  /**
   * Add a disposable to the internal list, dispose it onUnbind
   */
  protected fun dispose(disposable: Disposable) {
    disposables.add(disposable)
  }
}
