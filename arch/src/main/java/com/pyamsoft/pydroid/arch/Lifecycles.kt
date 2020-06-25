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

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Lifecycle.Event.ON_CREATE
import androidx.lifecycle.Lifecycle.Event.ON_DESTROY
import androidx.lifecycle.Lifecycle.Event.ON_RESUME
import androidx.lifecycle.Lifecycle.Event.ON_START
import androidx.lifecycle.Lifecycle.Event.ON_STOP
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent

inline fun LifecycleOwner.doOnDestroy(crossinline func: () -> Unit) {
    this.lifecycle.doOnDestroy(func)
}

inline fun Lifecycle.doOnDestroy(crossinline func: () -> Unit) {
    val self = this
    self.addObserver(object : LifecycleObserver {

        @Suppress("unused")
        @OnLifecycleEvent(ON_DESTROY)
        fun onDestroy() {
            self.removeObserver(this)
            func()
        }
    })
}

inline fun LifecycleOwner.doOnCreate(crossinline func: () -> Unit) {
    this.lifecycle.doOnCreate(func)
}

inline fun Lifecycle.doOnCreate(crossinline func: () -> Unit) {
    val self = this
    self.addObserver(object : LifecycleObserver {

        @Suppress("unused")
        @OnLifecycleEvent(ON_CREATE)
        fun onDestroy() {
            self.removeObserver(this)
            func()
        }
    })
}

inline fun LifecycleOwner.doOnStart(crossinline func: () -> Unit) {
    this.lifecycle.doOnStart(func)
}

inline fun Lifecycle.doOnStart(crossinline func: () -> Unit) {
    val self = this
    self.addObserver(object : LifecycleObserver {

        @Suppress("unused")
        @OnLifecycleEvent(ON_START)
        fun onDestroy() {
            self.removeObserver(this)
            func()
        }
    })
}

inline fun LifecycleOwner.doOnStop(crossinline func: () -> Unit) {
    this.lifecycle.doOnStop(func)
}

inline fun Lifecycle.doOnStop(crossinline func: () -> Unit) {
    val self = this
    self.addObserver(object : LifecycleObserver {

        @Suppress("unused")
        @OnLifecycleEvent(ON_STOP)
        fun onDestroy() {
            self.removeObserver(this)
            func()
        }
    })
}

inline fun LifecycleOwner.doOnResume(crossinline func: () -> Unit) {
    this.lifecycle.doOnResume(func)
}

inline fun Lifecycle.doOnResume(crossinline func: () -> Unit) {
    val self = this
    self.addObserver(object : LifecycleObserver {

        @Suppress("unused")
        @OnLifecycleEvent(ON_RESUME)
        fun onDestroy() {
            self.removeObserver(this)
            func()
        }
    })
}

inline fun LifecycleOwner.doOnPause(crossinline func: () -> Unit) {
    this.lifecycle.doOnPause(func)
}

inline fun Lifecycle.doOnPause(crossinline func: () -> Unit) {
    val self = this
    self.addObserver(object : LifecycleObserver {

        @Suppress("unused")
        @OnLifecycleEvent(ON_STOP)
        fun onDestroy() {
            self.removeObserver(this)
            func()
        }
    })
}
