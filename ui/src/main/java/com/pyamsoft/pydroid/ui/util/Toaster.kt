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

package com.pyamsoft.pydroid.ui.util

import android.content.Context
import android.widget.Toast
import androidx.annotation.CheckResult
import androidx.annotation.StringRes
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.pyamsoft.pydroid.util.doOnDestroy
import java.util.concurrent.ConcurrentHashMap

object Toaster {

    @JvmStatic
    @CheckResult
    private fun make(
        context: Context,
        @StringRes resId: Int,
        duration: Int
    ): Toast {
        return Toast.makeText(context.applicationContext, resId, duration)
    }

    @JvmStatic
    @CheckResult
    private fun make(
        context: Context,
        message: CharSequence,
        duration: Int
    ): Toast {
        return Toast.makeText(context.applicationContext, message, duration)
    }

    private val cache: MutableMap<Lifecycle, Instance> by lazy { ConcurrentHashMap<Lifecycle, Instance>() }

    @CheckResult
    fun bindTo(owner: LifecycleOwner): Instance {
        return bindTo(owner.lifecycle)
    }

    @CheckResult
    fun bindTo(lifecycle: Lifecycle): Instance {
        return if (cache.containsKey(lifecycle)) {
            requireNotNull(cache[lifecycle])
        } else {
            cacheInstance(lifecycle)
        }
    }

    @CheckResult
    private fun cacheInstance(lifecycle: Lifecycle): Instance {
        val instance = Instance()
        cache[lifecycle] = instance

        lifecycle.doOnDestroy {
            cache.remove(lifecycle)
            instance.onDestroy()
        }

        return instance
    }

    class Instance internal constructor() {

        private var alive = true
        private var toast: Toast? = null

        internal fun onDestroy() {
            dismiss()
            alive = false
        }

        private fun requireStillAlive() {
            require(alive) { "This Toaster.${Instance::class.java.simpleName} is Dead" }
        }

        fun dismiss() {
            toast?.cancel()
            toast = null
        }

        @CheckResult
        fun short(
            context: Context,
            message: CharSequence
        ): Toast {
            requireStillAlive()
            dismiss()
            return make(context, message, Toast.LENGTH_SHORT).also { toast = it }
        }

        @CheckResult
        fun short(
            context: Context,
            @StringRes message: Int
        ): Toast {
            requireStillAlive()
            dismiss()
            return make(context, message, Toast.LENGTH_SHORT).also { toast = it }
        }

        @CheckResult
        fun long(
            context: Context,
            message: CharSequence
        ): Toast {
            requireStillAlive()
            dismiss()
            return make(context, message, Toast.LENGTH_LONG).also { toast = it }
        }

        @CheckResult
        fun long(
            context: Context,
            @StringRes message: Int
        ): Toast {
            requireStillAlive()
            dismiss()
            return make(context, message, Toast.LENGTH_LONG).also { toast = it }
        }
    }
}
