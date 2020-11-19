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

package com.pyamsoft.pydroid.ui.util

import android.content.Context
import android.widget.Toast
import androidx.annotation.CheckResult
import androidx.annotation.StringRes
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.pyamsoft.pydroid.util.doOnDestroy

object Toaster {

    private val cache by lazy { mutableMapOf<Lifecycle, Instance>() }

    @CheckResult
    fun bindTo(owner: LifecycleOwner): Instance {
        return bindTo(owner.lifecycle)
    }

    @CheckResult
    fun bindTo(lifecycle: Lifecycle): Instance {
        return cache[lifecycle] ?: cacheInstance(lifecycle)
    }

    @CheckResult
    private fun cacheInstance(lifecycle: Lifecycle): Instance {
        val instance = Instance()
        val c = cache

        c[lifecycle] = instance
        lifecycle.doOnDestroy {
            c.remove(lifecycle)
            instance.onDestroy()
        }

        return instance
    }

    class Instance internal constructor() {

        private var toast: Toast? = null
        private var toastCallback: Toast.Callback? = null

        internal fun onDestroy() {
            dismiss()
        }

        private fun removeCallback(toast: Toast, callback: Toast.Callback) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                toast.removeCallback(callback)
            }
        }

        private inline fun addCallback(
            toast: Toast,
            crossinline onShown: (toast: Toast) -> Unit,
            crossinline onHidden: (toast: Toast) -> Unit
        ) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                toast.addCallback(object : Toast.Callback() {

                    override fun onToastShown() {
                        super.onToastShown()
                        onShown(toast)
                    }

                    override fun onToastHidden() {
                        super.onToastHidden()
                        onHidden(toast)

                        toast.removeCallback(this)
                        clearRefs()
                    }

                })
            }
        }

        fun dismiss() {
            toast?.also { t ->
                toastCallback?.also { removeCallback(t, it) }
                t.cancel()
            }
            clearRefs()
        }

        private fun clearRefs() {
            toast = null
            toastCallback = null
        }

        @JvmOverloads
        fun short(
            context: Context,
            message: CharSequence,
            onShown: (toast: Toast) -> Unit = DEFAULT_ON_SHOWN,
            onHidden: (toast: Toast) -> Unit = DEFAULT_ON_HIDDEN,
        ) {
            dismiss()
            make(context, message, Toast.LENGTH_SHORT)
                .also { toast = it }
                .also { addCallback(it, onShown, onHidden) }
                .also { it.show() }
        }

        @JvmOverloads
        fun short(
            context: Context,
            @StringRes message: Int,
            onShown: (toast: Toast) -> Unit = DEFAULT_ON_SHOWN,
            onHidden: (toast: Toast) -> Unit = DEFAULT_ON_HIDDEN,
        ) {
            dismiss()
            make(context, message, Toast.LENGTH_SHORT)
                .also { toast = it }
                .also { addCallback(it, onShown, onHidden) }
                .also { it.show() }
        }

        @JvmOverloads
        fun long(
            context: Context,
            message: CharSequence,
            onShown: (toast: Toast) -> Unit = DEFAULT_ON_SHOWN,
            onHidden: (toast: Toast) -> Unit = DEFAULT_ON_HIDDEN,
        ) {
            dismiss()
            make(context, message, Toast.LENGTH_LONG)
                .also { toast = it }
                .also { addCallback(it, onShown, onHidden) }
                .also { it.show() }
        }

        @JvmOverloads
        fun long(
            context: Context,
            @StringRes message: Int,
            onShown: (toast: Toast) -> Unit = DEFAULT_ON_SHOWN,
            onHidden: (toast: Toast) -> Unit = DEFAULT_ON_HIDDEN,
        ) {
            dismiss()
            make(context, message, Toast.LENGTH_LONG)
                .also { toast = it }
                .also { addCallback(it, onShown, onHidden) }
                .also { it.show() }
        }

        companion object {

            private val DEFAULT_ON_SHOWN = { _: Toast -> }
            private val DEFAULT_ON_HIDDEN = { _: Toast -> }

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

        }
    }
}
