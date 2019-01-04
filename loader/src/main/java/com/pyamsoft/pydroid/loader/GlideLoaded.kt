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

package com.pyamsoft.pydroid.loader

import androidx.lifecycle.Lifecycle.Event.ON_DESTROY
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.bumptech.glide.Glide
import timber.log.Timber

class GlideLoaded(
  private val target: ImageTarget<*>
) : Loaded, LifecycleObserver {

  private var lifeCycleOwner: LifecycleOwner? = null

  override fun bind(owner: LifecycleOwner) {
    Timber.d("Bind to lifecycle")
    owner.lifecycle.addObserver(this)
    lifeCycleOwner = owner
  }

  @OnLifecycleEvent(ON_DESTROY)
  internal fun unbindOnDestroy() {
    Timber.d("Unbind on destroy")
    lifeCycleOwner?.lifecycle?.removeObserver(this)
    lifeCycleOwner = null

    val view = target.view()
    Glide.with(view.context.applicationContext)
        .clear(view)
    target.clear()
  }

}
