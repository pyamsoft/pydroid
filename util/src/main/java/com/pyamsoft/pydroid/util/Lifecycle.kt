/*
 * Copyright 2020 Peter Kenji Yamanaka
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
import androidx.lifecycle.OnLifecycleEvent

/** Run a block once on Lifecycle destroy */
public inline fun LifecycleOwner.doOnDestroy(crossinline func: () -> Unit) {
  this.lifecycle.doOnDestroy(func)
}

/** Run a block once on Lifecycle destroy */
public inline fun Lifecycle.doOnDestroy(crossinline func: () -> Unit) {
  val self = this
  self.addObserver(
      object : LifecycleObserver {

        @Suppress("unused")
        @OnLifecycleEvent(ON_DESTROY)
        fun onEvent() {
          self.removeObserver(this)
          func()
        }
      })
}

/** Run a block once on Lifecycle create */
public inline fun LifecycleOwner.doOnCreate(crossinline func: () -> Unit) {
  this.lifecycle.doOnCreate(func)
}

/** Run a block once on Lifecycle create */
public inline fun Lifecycle.doOnCreate(crossinline func: () -> Unit) {
  val self = this
  self.addObserver(
      object : LifecycleObserver {

        @Suppress("unused")
        @OnLifecycleEvent(ON_CREATE)
        fun onEvent() {
          self.removeObserver(this)
          func()
        }
      })
}

/** Run a block once on Lifecycle start */
public inline fun LifecycleOwner.doOnStart(crossinline func: () -> Unit) {
  this.lifecycle.doOnStart(func)
}

/** Run a block once on Lifecycle start */
public inline fun Lifecycle.doOnStart(crossinline func: () -> Unit) {
  val self = this
  self.addObserver(
      object : LifecycleObserver {

        @Suppress("unused")
        @OnLifecycleEvent(ON_START)
        fun onEvent() {
          self.removeObserver(this)
          func()
        }
      })
}

/** Run a block once on Lifecycle stop */
public inline fun LifecycleOwner.doOnStop(crossinline func: () -> Unit) {
  this.lifecycle.doOnStop(func)
}

/** Run a block once on Lifecycle stop */
public inline fun Lifecycle.doOnStop(crossinline func: () -> Unit) {
  val self = this
  self.addObserver(
      object : LifecycleObserver {

        @Suppress("unused")
        @OnLifecycleEvent(ON_STOP)
        fun onEvent() {
          self.removeObserver(this)
          func()
        }
      })
}

/** Run a block once on Lifecycle resume */
public inline fun LifecycleOwner.doOnResume(crossinline func: () -> Unit) {
  this.lifecycle.doOnResume(func)
}

/** Run a block once on Lifecycle resume */
public inline fun Lifecycle.doOnResume(crossinline func: () -> Unit) {
  val self = this
  self.addObserver(
      object : LifecycleObserver {

        @Suppress("unused")
        @OnLifecycleEvent(ON_RESUME)
        fun onEvent() {
          self.removeObserver(this)
          func()
        }
      })
}

/** Run a block once on Lifecycle pause */
public inline fun LifecycleOwner.doOnPause(crossinline func: () -> Unit) {
  this.lifecycle.doOnPause(func)
}

/** Run a block once on Lifecycle pause */
public inline fun Lifecycle.doOnPause(crossinline func: () -> Unit) {
  val self = this
  self.addObserver(
      object : LifecycleObserver {

        @Suppress("unused")
        @OnLifecycleEvent(ON_PAUSE)
        fun onEvent() {
          self.removeObserver(this)
          func()
        }
      })
}
