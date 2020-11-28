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

package com.pyamsoft.pydroid.ui

import android.annotation.SuppressLint
import android.content.Context
import androidx.annotation.CheckResult

/**
 * Injects PYDroid and its managed services into a context
 */
object Injector {

    /**
     * Obtain an object, like a Component from the Context service locator
     */
    @JvmStatic
    @CheckResult
    @SuppressLint("WrongConstant")
    inline fun <reified T : Any> obtain(context: Context): T {
        return obtain(context, T::class.java)
    }

    /**
     * Obtain an object, like a Component from the Context service locator
     */
    @JvmStatic
    @CheckResult
    @SuppressLint("WrongConstant")
    fun <T : Any> obtain(
        context: Context,
        targetClass: Class<T>
    ): T {
        val name = targetClass.name
        val service: Any = context.getSystemService(name) ?: throw ServiceLookupException(name)

        @Suppress("UNCHECKED_CAST")
        return service as T
    }
}

private class ServiceLookupException(
    name: String
) : IllegalStateException("Unable to location service: $name")
