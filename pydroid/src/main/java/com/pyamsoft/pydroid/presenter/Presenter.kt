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

abstract class Presenter protected constructor() {

  private val stopDisposables: CompositeDisposable = CompositeDisposable()
  private val destroyDisposables: CompositeDisposable = CompositeDisposable()

  fun stop() {
    onStop()
    stopDisposables.clear()
  }

  fun destroy() {
    onDestroy()
    destroyDisposables.clear()
  }

  /**
   * Override per implementation
   */
  protected open fun onStop() {

  }

  /**
   * Override per implementation
   */
  protected open fun onDestroy() {

  }

  /**
   * Add a disposable to the internal list, dispose it onStop
   */
  protected fun disposeOnStop(disposable: Disposable) {
    stopDisposables.add(disposable)
  }

  /**
   * Add a disposable to the internal list, dispose it onDestroy
   */
  protected fun disposeOnDestroy(disposable: Disposable) {
    destroyDisposables.add(disposable)
  }
}
