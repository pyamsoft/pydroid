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

package com.pyamsoft.pydroid.ui.arch

import android.os.Bundle
import androidx.annotation.CheckResult
import androidx.annotation.IdRes
import androidx.lifecycle.Lifecycle.Event.ON_DESTROY
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.pyamsoft.pydroid.bootstrap.SchedulerProvider
import com.pyamsoft.pydroid.core.bus.Listener
import io.reactivex.Observable

abstract class BaseUiComponent<T : ViewEvent, V : UiView> @JvmOverloads protected constructor(
  protected val view: V,
  private val uiBus: Listener<T>,
  protected val owner: LifecycleOwner,
  protected val schedulerProvider: SchedulerProvider = SchedulerProvider.DEFAULT
) : UiComponent<T, V> {

  @IdRes
  @CheckResult
  final override fun id(): Int {
    return view.id()
  }

  @CheckResult
  final override fun onUiEvent(): Observable<T> {
    return uiBus.listen()
        .subscribeOn(schedulerProvider.backgroundScheduler)
        .observeOn(schedulerProvider.foregroundScheduler)
  }

  final override fun create(savedInstanceState: Bundle?) {
    view.inflate(savedInstanceState)
    owner.runOnDestroy { destroy() }
    onCreate(savedInstanceState)
  }

  protected open fun onCreate(savedInstanceState: Bundle?) {

  }

  final override fun saveState(outState: Bundle) {
    view.saveState(outState)
    onSaveState(outState)
  }

  protected open fun onSaveState(outState: Bundle) {

  }

  private fun destroy() {
    view.teardown()
    onDestroy()
  }

  protected open fun onDestroy() {
  }

  private fun LifecycleOwner.runOnDestroy(func: () -> Unit) {
    lifecycle.addObserver(object : LifecycleObserver {

      @Suppress("unused")
      @OnLifecycleEvent(ON_DESTROY)
      fun onDestroy() {
        lifecycle.removeObserver(this)
        func()
      }

    })
  }
}
