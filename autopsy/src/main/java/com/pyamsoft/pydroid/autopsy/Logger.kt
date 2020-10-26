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

package com.pyamsoft.pydroid.autopsy

import android.util.Log

internal object Logger {

    @JvmStatic
    fun tag(tag: String): Logging {
        return Logging(tag)
    }

    @JvmStatic
    fun tag(instance: Any): Logging {
        return Logging(instance::class.java.simpleName)
    }
}

internal class Logging internal constructor(private val tag: String) {

    fun d(throwable: Throwable? = null, message: String) {
        Log.d(tag, message, throwable)
    }

    fun d(message: String) {
        Log.d(tag, message)
    }

    fun w(throwable: Throwable? = null, message: String) {
        Log.w(tag, message, throwable)
    }

    fun w(message: String) {
        Log.w(tag, message)
    }

    fun i(throwable: Throwable? = null, message: String) {
        Log.i(tag, message, throwable)
    }

    fun i(message: String) {
        Log.i(tag, message)
    }

    fun e(throwable: Throwable? = null, message: String) {
        Log.e(tag, message, throwable)
    }

    fun e(message: String) {
        Log.e(tag, message)
    }
}
