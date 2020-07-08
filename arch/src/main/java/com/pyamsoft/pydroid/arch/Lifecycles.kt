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

@Deprecated(
    message = "Use doOnDestroy from pydroid-util",
    replaceWith = ReplaceWith(
        expression = "doOnDestroy(func)",
        imports = ["com.pyamsoft.pydroid.util.doOnDestroy"]
    )
)
inline fun LifecycleOwner.doOnDestroy(crossinline func: () -> Unit) {
    this.lifecycle.doOnDestroy(func)
}

@Deprecated(
    message = "Use doOnDestroy from pydroid-util",
    replaceWith = ReplaceWith(
        expression = "doOnDestroy(func)",
        imports = ["com.pyamsoft.pydroid.util.doOnDestroy"]
    )
)
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

@Deprecated(
    message = "Use doOnCreate from pydroid-util",
    replaceWith = ReplaceWith(
        expression = "doOnCreate(func)",
        imports = ["com.pyamsoft.pydroid.util.doOnCreate"]
    )
)
inline fun LifecycleOwner.doOnCreate(crossinline func: () -> Unit) {
    this.lifecycle.doOnCreate(func)
}

@Deprecated(
    message = "Use doOnCreate from pydroid-util",
    replaceWith = ReplaceWith(
        expression = "doOnCreate(func)",
        imports = ["com.pyamsoft.pydroid.util.doOnCreate"]
    )
)
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

@Deprecated(
    message = "Use doOnStart from pydroid-util",
    replaceWith = ReplaceWith(
        expression = "doOnStart(func)",
        imports = ["com.pyamsoft.pydroid.util.doOnStart"]
    )
)
inline fun LifecycleOwner.doOnStart(crossinline func: () -> Unit) {
    this.lifecycle.doOnStart(func)
}

@Deprecated(
    message = "Use doOnStart from pydroid-util",
    replaceWith = ReplaceWith(
        expression = "doOnStart(func)",
        imports = ["com.pyamsoft.pydroid.util.doOnStart"]
    )
)
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

@Deprecated(
    message = "Use doOnStop from pydroid-util",
    replaceWith = ReplaceWith(
        expression = "doOnStop(func)",
        imports = ["com.pyamsoft.pydroid.util.doOnStop"]
    )
)
inline fun LifecycleOwner.doOnStop(crossinline func: () -> Unit) {
    this.lifecycle.doOnStop(func)
}

@Deprecated(
    message = "Use doOnStop from pydroid-util",
    replaceWith = ReplaceWith(
        expression = "doOnStop(func)",
        imports = ["com.pyamsoft.pydroid.util.doOnStop"]
    )
)
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

@Deprecated(
    message = "Use doOnResume from pydroid-util",
    replaceWith = ReplaceWith(
        expression = "doOnResume(func)",
        imports = ["com.pyamsoft.pydroid.util.doOnResume"]
    )
)
inline fun LifecycleOwner.doOnResume(crossinline func: () -> Unit) {
    this.lifecycle.doOnResume(func)
}

@Deprecated(
    message = "Use doOnResume from pydroid-util",
    replaceWith = ReplaceWith(
        expression = "doOnResume(func)",
        imports = ["com.pyamsoft.pydroid.util.doOnResume"]
    )
)
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

@Deprecated(
    message = "Use doOnPause from pydroid-util",
    replaceWith = ReplaceWith(
        expression = "doOnPause(func)",
        imports = ["com.pyamsoft.pydroid.util.doOnPause"]
    )
)
inline fun LifecycleOwner.doOnPause(crossinline func: () -> Unit) {
    this.lifecycle.doOnPause(func)
}

@Deprecated(
    message = "Use doOnPause from pydroid-util",
    replaceWith = ReplaceWith(
        expression = "doOnPause(func)",
        imports = ["com.pyamsoft.pydroid.util.doOnPause"]
    )
)
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
