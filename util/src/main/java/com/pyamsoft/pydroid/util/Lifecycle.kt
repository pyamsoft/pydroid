/*
 * Copyright 2024 pyamsoft
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pyamsoft.pydroid.util

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner

/** Run a block once on Lifecycle destroy */
public inline fun LifecycleOwner.doOnDestroy(crossinline func: () -> Unit) {
  this.lifecycle.doOnDestroy(func)
}

/** Run a block once on Lifecycle destroy */
public inline fun Lifecycle.doOnDestroy(crossinline func: () -> Unit) {
  this.addObserver(
      object : DefaultLifecycleObserver {

        override fun onDestroy(owner: LifecycleOwner) {
          owner.lifecycle.removeObserver(this)
          func()
        }
      },
  )
}

/** Run a block once on Lifecycle create */
public inline fun LifecycleOwner.doOnCreate(crossinline func: () -> Unit) {
  this.lifecycle.doOnCreate(func)
}

/** Run a block once on Lifecycle create */
public inline fun Lifecycle.doOnCreate(crossinline func: () -> Unit) {
  this.addObserver(
      object : DefaultLifecycleObserver {

        override fun onCreate(owner: LifecycleOwner) {
          owner.lifecycle.removeObserver(this)
          func()
        }
      },
  )
}

/** Run a block once on Lifecycle start */
public inline fun LifecycleOwner.doOnStart(crossinline func: () -> Unit) {
  this.lifecycle.doOnStart(func)
}

/** Run a block once on Lifecycle start */
public inline fun Lifecycle.doOnStart(crossinline func: () -> Unit) {
  this.addObserver(
      object : DefaultLifecycleObserver {

        override fun onStart(owner: LifecycleOwner) {
          owner.lifecycle.removeObserver(this)
          func()
        }
      },
  )
}

/** Run a block once on Lifecycle stop */
public inline fun LifecycleOwner.doOnStop(crossinline func: () -> Unit) {
  this.lifecycle.doOnStop(func)
}

/** Run a block once on Lifecycle stop */
public inline fun Lifecycle.doOnStop(crossinline func: () -> Unit) {
  this.addObserver(
      object : DefaultLifecycleObserver {

        override fun onStop(owner: LifecycleOwner) {
          owner.lifecycle.removeObserver(this)
          func()
        }
      },
  )
}

/** Run a block once on Lifecycle resume */
public inline fun LifecycleOwner.doOnResume(crossinline func: () -> Unit) {
  this.lifecycle.doOnResume(func)
}

/** Run a block once on Lifecycle resume */
public inline fun Lifecycle.doOnResume(crossinline func: () -> Unit) {
  this.addObserver(
      object : DefaultLifecycleObserver {

        override fun onResume(owner: LifecycleOwner) {
          owner.lifecycle.removeObserver(this)
          func()
        }
      },
  )
}

/** Run a block once on Lifecycle pause */
public inline fun LifecycleOwner.doOnPause(crossinline func: () -> Unit) {
  this.lifecycle.doOnPause(func)
}

/** Run a block once on Lifecycle pause */
public inline fun Lifecycle.doOnPause(crossinline func: () -> Unit) {
  this.addObserver(
      object : DefaultLifecycleObserver {

        override fun onPause(owner: LifecycleOwner) {
          owner.lifecycle.removeObserver(this)
          func()
        }
      },
  )
}
