/*
 * Copyright (C) 2018 Peter Kenji Yamanaka
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pyamsoft.pydroid.presenter

import android.arch.lifecycle.Lifecycle.Event.ON_CREATE
import android.arch.lifecycle.Lifecycle.Event.ON_DESTROY
import android.arch.lifecycle.Lifecycle.Event.ON_PAUSE
import android.arch.lifecycle.Lifecycle.Event.ON_RESUME
import android.arch.lifecycle.Lifecycle.Event.ON_START
import android.arch.lifecycle.Lifecycle.Event.ON_STOP
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.OnLifecycleEvent
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class Presenter<V : Any> protected constructor() : LifecycleObserver {

  private val disposables: CompositeDisposable = CompositeDisposable()
  protected var view: V? = null
    private set
  private var lifecycleOwner: LifecycleOwner? = null

  fun bind(
    owner: LifecycleOwner,
    view: V
  ) {
    this.view = view
    this.lifecycleOwner = owner
    owner.lifecycle.addObserver(this)
  }

  @OnLifecycleEvent(ON_CREATE)
  internal fun performCreate() {
    onCreate()
  }

  protected open fun onCreate() {
  }

  @OnLifecycleEvent(ON_START)
  internal fun performStart() {
    onStart()
  }

  protected open fun onStart() {
  }

  @OnLifecycleEvent(ON_RESUME)
  internal fun performResume() {
    onResume()
  }

  protected open fun onResume() {
  }

  @OnLifecycleEvent(ON_PAUSE)
  internal fun performPause() {
    onPause()
  }

  protected open fun onPause() {
  }

  @OnLifecycleEvent(ON_STOP)
  internal fun performStop() {
    onStop()
  }

  protected open fun onStop() {
  }

  @OnLifecycleEvent(ON_DESTROY)
  internal fun performDestroy() {
    // Unbind the view
    this.view = null
    onDestroy()

    // Clear disposables after onDestroy incase something accidentally subscribes
    disposables.clear()

    // Remove the lifecycle observer since we are dead
    lifecycleOwner?.lifecycle?.removeObserver(this)
    lifecycleOwner = null
  }

  protected open fun onDestroy() {
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
