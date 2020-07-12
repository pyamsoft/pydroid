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

package com.pyamsoft.pydroid.core

import android.os.Looper
import androidx.annotation.CheckResult

object Enforcer {

    private val mainLooper by lazy { Looper.getMainLooper() }

    @CheckResult
    fun isMainThread(): Boolean {
        return mainLooper.thread == Thread.currentThread()
    }

    fun assertOffMainThread() {
        if (isMainThread()) {
            throw AssertionError("This operation must be OFF the Main/UI thread!")
        }
    }

    fun assertOnMainThread() {
        if (!isMainThread()) {
            throw AssertionError("This operation must be ON the Main/UI thread!")
        }
    }
}
