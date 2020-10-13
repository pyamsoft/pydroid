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

import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import androidx.annotation.CheckResult
import androidx.annotation.StringRes
import androidx.core.view.ViewCompat
import androidx.core.view.setMargins
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.google.android.material.snackbar.BaseTransientBottomBar.BaseCallback
import com.google.android.material.snackbar.Snackbar
import com.pyamsoft.pydroid.util.doOnApplyWindowInsets
import com.pyamsoft.pydroid.util.doOnDestroy
import com.pyamsoft.pydroid.util.toDp
import java.util.concurrent.ConcurrentHashMap

object Snackbreak {

    private val cache: MutableMap<Lifecycle, MutableSet<CacheEntry>> by lazy {
        ConcurrentHashMap<Lifecycle, MutableSet<CacheEntry>>()
    }

    inline fun bindTo(
        owner: LifecycleOwner,
        crossinline withInstance: Instance.() -> Unit
    ) {
        return bindTo(owner.lifecycle, withInstance)
    }

    inline fun bindTo(
        owner: LifecycleOwner,
        id: String,
        crossinline withInstance: Instance.() -> Unit
    ) {
        return bindTo(owner.lifecycle, id, withInstance)
    }

    inline fun bindTo(
        lifecycle: Lifecycle,
        crossinline withInstance: Instance.() -> Unit
    ) {
        return realBindTo(lifecycle, null) { withInstance() }
    }

    inline fun bindTo(
        lifecycle: Lifecycle,
        id: String,
        crossinline withInstance: Instance.() -> Unit
    ) {
        return realBindTo(lifecycle, id) { withInstance() }
    }

    @PublishedApi
    internal fun realBindTo(
        lifecycle: Lifecycle,
        id: String?,
        withInstance: Instance.() -> Unit
    ) {
        if (lifecycle.currentState == Lifecycle.State.DESTROYED) {
            return
        }

        val instance = cache[lifecycle]
            ?.find { id == it.id }
            ?.instance ?: cacheInstance(lifecycle, id)

        withInstance(instance)
    }

    @CheckResult
    private fun cacheInstance(
        lifecycle: Lifecycle,
        id: String?
    ): Instance {
        val instance = Instance()
        val c = cache

        val cached = c.getOrPut(lifecycle) {
            // Set up the lifecycle listener to destroy when out of scope
            lifecycle.doOnDestroy { c.remove(lifecycle)?.forEach { it.instance.onDestroy() } }
            return@getOrPut mutableSetOf()
        }

        cached.add(CacheEntry(instance, id))
        return instance
    }

    class Instance internal constructor() {

        private var snackbar: Snackbar? = null
        private var barCallback: BaseCallback<Snackbar>? = null

        internal fun onDestroy() {
            dismiss()
        }

        private fun clearRefs() {
            barCallback = null
            snackbar = null
        }

        fun dismiss() {
            snackbar?.also { bar ->
                barCallback?.also { bar.removeCallback(it) }
                bar.dismiss()
            }
            clearRefs()
        }

        @CheckResult
        private fun canShowNewSnackbar(force: Boolean): Boolean {
            return if (force) true else {
                snackbar.let { if (it == null) true else !it.isShownOrQueued }
            }
        }

        private inline fun snack(
            force: Boolean,
            crossinline onShown: (snackbar: Snackbar) -> Unit,
            crossinline onHidden: (snackbar: Snackbar, event: Int) -> Unit,
            builder: Snackbar.() -> Snackbar,
            snack: () -> Snackbar
        ) {
            if (canShowNewSnackbar(force)) {
                dismiss()
                snackbar = snack()
                    .run(builder)
                    .let { bar ->
                        val callback = object : BaseCallback<Snackbar>() {

                            override fun onShown(transientBottomBar: Snackbar?) {
                                super.onShown(transientBottomBar)
                                onShown(bar)
                            }

                            override fun onDismissed(
                                transientBottomBar: Snackbar?,
                                event: Int
                            ) {
                                super.onDismissed(transientBottomBar, event)
                                onHidden(bar, event)

                                // Clear out the long refs on dismiss
                                bar.removeCallback(this)
                                clearRefs()
                            }
                        }

                        // Track the callback for full death
                        barCallback = callback
                        return@let bar.addCallback(callback)
                    }
                    .also { it.show() }
            }
        }

        @JvmOverloads
        fun short(
            view: View,
            message: CharSequence,
            force: Boolean = false,
            onShown: (snackbar: Snackbar) -> Unit = DEFAULT_ON_SHOWN,
            onHidden: (snackbar: Snackbar, event: Int) -> Unit = DEFAULT_ON_HIDDEN,
            builder: Snackbar.() -> Snackbar = DEFAULT_BUILDER
        ) {
            snack(force, onShown, onHidden, builder) {
                make(view, message, Snackbar.LENGTH_SHORT)
            }
        }

        @JvmOverloads
        fun short(
            view: View,
            @StringRes message: Int,
            force: Boolean = false,
            onShown: (snackbar: Snackbar) -> Unit = DEFAULT_ON_SHOWN,
            onHidden: (snackbar: Snackbar, event: Int) -> Unit = DEFAULT_ON_HIDDEN,
            builder: Snackbar.() -> Snackbar = DEFAULT_BUILDER
        ) {
            snack(force, onShown, onHidden, builder) {
                make(view, message, Snackbar.LENGTH_SHORT)
            }
        }

        @JvmOverloads
        fun long(
            view: View,
            message: CharSequence,
            force: Boolean = false,
            onShown: (snackbar: Snackbar) -> Unit = DEFAULT_ON_SHOWN,
            onHidden: (snackbar: Snackbar, event: Int) -> Unit = DEFAULT_ON_HIDDEN,
            builder: Snackbar.() -> Snackbar = DEFAULT_BUILDER
        ) {
            snack(force, onShown, onHidden, builder) {
                make(view, message, Snackbar.LENGTH_LONG)
            }
        }

        @JvmOverloads
        fun long(
            view: View,
            @StringRes message: Int,
            force: Boolean = false,
            onShown: (snackbar: Snackbar) -> Unit = DEFAULT_ON_SHOWN,
            onHidden: (snackbar: Snackbar, event: Int) -> Unit = DEFAULT_ON_HIDDEN,
            builder: Snackbar.() -> Snackbar = DEFAULT_BUILDER
        ) {
            snack(force, onShown, onHidden, builder) {
                make(view, message, Snackbar.LENGTH_LONG)
            }
        }

        @JvmOverloads
        fun make(
            view: View,
            message: CharSequence,
            force: Boolean = false,
            onShown: (snackbar: Snackbar) -> Unit = DEFAULT_ON_SHOWN,
            onHidden: (snackbar: Snackbar, event: Int) -> Unit = DEFAULT_ON_HIDDEN,
            builder: Snackbar.() -> Snackbar = DEFAULT_BUILDER
        ) {
            snack(force, onShown, onHidden, builder) {
                make(view, message, Snackbar.LENGTH_INDEFINITE)
            }
        }

        @JvmOverloads
        fun make(
            view: View,
            @StringRes message: Int,
            force: Boolean = false,
            onShown: (snackbar: Snackbar) -> Unit = DEFAULT_ON_SHOWN,
            onHidden: (snackbar: Snackbar, event: Int) -> Unit = DEFAULT_ON_HIDDEN,
            builder: Snackbar.() -> Snackbar = DEFAULT_BUILDER
        ) {
            snack(force, onShown, onHidden, builder) {
                make(view, message, Snackbar.LENGTH_INDEFINITE)
            }
        }

        companion object {

            private val DEFAULT_ON_SHOWN = { _: Snackbar -> }
            private val DEFAULT_ON_HIDDEN = { _: Snackbar, _: Int -> }
            private val DEFAULT_BUILDER: Snackbar.() -> Snackbar = { this }

            private fun fixSnackbar(view: View, margin: Int) {
                view.updateLayoutParams<MarginLayoutParams> { setMargins(margin) }
                view.updatePadding(left = 0, right = 0, top = 0, bottom = 0)
            }

            private fun Snackbar.materialMargin() {
                val params = view.layoutParams as? MarginLayoutParams
                if (params != null) {
                    val margin = 8.toDp(view.context)

                    // Fix the margins to be material-y
                    fixSnackbar(view, margin)

                    // The Snackbar in material library sets a Material design theme but
                    // it fucks the window insets if your app is using LAYOUT_HIDE_NAVIGATION
                    // and adjusting for bottom padding - it adds the bottom padding from the insets
                    // into the snackbar as well.
                    view.doOnApplyWindowInsets { v, _, _ -> fixSnackbar(v, margin) }
                }
            }

            private fun Snackbar.materialElevation() {
                ViewCompat.setElevation(view, 6.toDp(context).toFloat())
            }

            private fun Snackbar.materialDesign() {
                materialMargin()
                materialElevation()
            }

            @JvmStatic
            @CheckResult
            private fun make(
                view: View,
                @StringRes resId: Int,
                duration: Int
            ): Snackbar {
                return Snackbar.make(view, resId, duration)
                    .also { it.materialDesign() }
            }

            @JvmStatic
            @CheckResult
            private fun make(
                view: View,
                message: CharSequence,
                duration: Int
            ): Snackbar {
                return Snackbar.make(view, message, duration)
                    .also { it.materialDesign() }
            }
        }
    }

    private data class CacheEntry(
        val instance: Instance,
        val id: String?
    )
}
