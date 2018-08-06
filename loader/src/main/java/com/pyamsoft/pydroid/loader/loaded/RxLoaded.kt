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

package com.pyamsoft.pydroid.loader.loaded

import androidx.lifecycle.Lifecycle.Event.ON_DESTROY
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.pyamsoft.pydroid.loader.targets.Target
import io.reactivex.disposables.Disposable
import timber.log.Timber

class RxLoaded(
  private val target: Target<*>,
  private val disposable: Disposable
) : Loaded, LifecycleObserver {

  private var lifeCycleOwner: LifecycleOwner? = null

  override fun bind(owner: LifecycleOwner) {
    Timber.d("Bind RxLoaded to lifecycle")
    owner.lifecycle.addObserver(this)
    lifeCycleOwner = owner
  }

  @OnLifecycleEvent(ON_DESTROY)
  internal fun unbindOnDestroy() {
    Timber.d("RxLoaded unbind on destroy")
    lifeCycleOwner?.lifecycle?.removeObserver(this)
    lifeCycleOwner = null

    disposable.dispose()
    target.clear()
  }
}
