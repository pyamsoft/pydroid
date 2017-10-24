/*
 *     Copyright (C) 2017 Peter Kenji Yamanaka
 *
 *     This program is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation; either version 2 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License along
 *     with this program; if not, write to the Free Software Foundation, Inc.,
 *     51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.pyamsoft.pydroid.presenter

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class Presenter<V : Any> protected constructor() {

  private val disposables: CompositeDisposable = CompositeDisposable()
  protected var view: V? = null
    private set

  fun bind(v: V) {
    view = v
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
    view = null
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
