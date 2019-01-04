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

package com.pyamsoft.pydroid.util

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Lifecycle.Event.ON_CREATE
import androidx.lifecycle.Lifecycle.Event.ON_DESTROY
import androidx.lifecycle.Lifecycle.Event.ON_PAUSE
import androidx.lifecycle.Lifecycle.Event.ON_RESUME
import androidx.lifecycle.Lifecycle.Event.ON_START
import androidx.lifecycle.Lifecycle.Event.ON_STOP
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.OnLifecycleEvent

inline fun runWhenReady(
  owner: LifecycleOwner,
  crossinline func: () -> Unit
) {
  runWhenReady(owner.lifecycle, func)
}

inline fun runWhenReady(
  lifecycle: Lifecycle,
  crossinline func: () -> Unit
) {
  val observer = object : LifecycleObserver {

    @Suppress("unused")
    @OnLifecycleEvent(ON_START)
    fun onReady() {
      lifecycle.removeObserver(this)
      func()
    }

  }

  lifecycle.addObserver(observer)
}

fun LifecycleRegistry.fakeBind() {
  handleLifecycleEvent(ON_CREATE)
  handleLifecycleEvent(ON_START)
  handleLifecycleEvent(ON_RESUME)
}

fun LifecycleRegistry.fakeUnbind() {
  handleLifecycleEvent(ON_PAUSE)
  handleLifecycleEvent(ON_STOP)
  handleLifecycleEvent(ON_DESTROY)
}
