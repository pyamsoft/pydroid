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

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

@Deprecated("Use UiViewModel")
abstract class UiBinder<C : UiBinder.Callback> protected constructor() {

  private var bound: Boolean
  private val disposables = CompositeDisposable()

  private var _callback: C? = null
  protected val callback: C
    get() = requireNotNull(_callback)

  init {
    bound = false
  }

  fun bind(callback: C) {
    // We should not need to synchronize since this should always be called on the main thread
    if (!bound) {
      bound = true
      _callback = callback
      internalOnBind()
      onBind()
    }
  }

  fun unbind() {
    if (bound) {
      bound = false
      _callback = null
      disposables.clear()
      internalOnUnbind()
      onUnbind()
    }
  }

  protected fun Disposable.destroy() {
    disposables.add(this)
  }

  protected abstract fun onBind()

  protected abstract fun onUnbind()

  internal open fun internalOnBind() {

  }

  internal open fun internalOnUnbind() {

  }

  interface Callback
}