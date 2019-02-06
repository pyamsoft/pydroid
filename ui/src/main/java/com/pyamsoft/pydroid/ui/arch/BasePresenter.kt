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

import androidx.annotation.CheckResult
import androidx.lifecycle.Lifecycle.Event.ON_DESTROY
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.pyamsoft.pydroid.core.bus.EventBus
import io.reactivex.Observable

abstract class BasePresenter<T : Any, C : Any>(
  protected val owner: LifecycleOwner,
  private val bus: EventBus<T>
) : Presenter<C> {

  private var callbackReference: C? = null
  protected val callback: C
    get() = requireNotNull(callbackReference)

  init {
    owner.lifecycle.addObserver(object : LifecycleObserver {

      @Suppress("unused")
      @OnLifecycleEvent(ON_DESTROY)
      fun onDestroy() {
        owner.lifecycle.removeObserver(this)
        unbind()
      }

    })
  }

  final override fun bind(callback: C) {
    // We should not need to synchronize since this should always be called on the main thread
    if (callbackReference == null) {
      callbackReference = callback
      onBind()
    }
  }

  protected abstract fun onBind()

  private fun unbind() {
    if (callbackReference != null) {
      callbackReference = null
      onUnbind()
    }
  }

  protected abstract fun onUnbind()

  protected fun publish(event: T) {
    bus.publish(event)
  }

  @CheckResult
  protected fun listen(): Observable<T> {
    return bus.listen()
  }
}