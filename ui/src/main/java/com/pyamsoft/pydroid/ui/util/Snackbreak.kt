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
import com.pyamsoft.pydroid.util.asDp
import com.pyamsoft.pydroid.util.doOnDestroy
import timber.log.Timber
import java.util.UUID

/**
 * Snackbar manager with lifecycle
 */
public object Snackbreak {

    private var cached: Snacky? = null

    /**
     * Bind to a lifecycle, automatically dismisses on lifecycle destroy
     */
    public inline fun bindTo(
        owner: LifecycleOwner,
        crossinline withInstance: Instance.() -> Unit
    ) {
        return bindTo(owner.lifecycle, withInstance)
    }

    /**
     * Bind to a lifecycle, automatically dismisses on lifecycle destroy
     */
    public inline fun bindTo(
        lifecycle: Lifecycle,
        crossinline withInstance: Instance.() -> Unit
    ) {
        return realBindTo(lifecycle) { withInstance() }
    }

    @PublishedApi
    internal fun realBindTo(
        lifecycle: Lifecycle,
        withInstance: Instance.() -> Unit
    ) {
        if (lifecycle.currentState == Lifecycle.State.DESTROYED) {
            return
        }

        withInstance(cacheInstance(lifecycle))
    }

    @CheckResult
    private fun cacheInstance(lifecycle: Lifecycle): Instance {
        val instance = Instance()

        lifecycle.doOnDestroy {
            instance.destroy()

            // If this is the cache, null it out
            if (cached?.instance?.id == instance.id) {
                Timber.d("Clear Snackbreak cached instance.")
                cached = null
            }
        }

        cached?.instance?.destroy()
        cached = Snacky(lifecycle, instance)
        return instance
    }

    /**
     * Bound snackbar instance handler
     */
    public class Instance internal constructor() {

        internal val id = UUID.randomUUID().toString()

        private var snackbar: Snackbar? = null
        private var barCallback: BaseCallback<Snackbar>? = null

        internal fun destroy() {
            dismiss()
        }

        private fun clearRefs() {
            barCallback = null
            snackbar = null
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

                                // Remove the listener on bar dismiss
                                ViewCompat.setOnApplyWindowInsetsListener(bar.view, null)

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

        /**
         * Dismiss snackbar
         */
        public fun dismiss() {
            snackbar?.also { bar ->
                barCallback?.also { bar.removeCallback(it) }
                bar.dismiss()
            }
            clearRefs()
        }

        /**
         * Show for short time
         */
        @JvmOverloads
        public fun short(
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

        /**
         * Show for short time
         */
        @JvmOverloads
        public fun short(
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

        /**
         * Show for long time
         */
        @JvmOverloads
        public fun long(
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

        /**
         * Show for long time
         */
        @JvmOverloads
        public fun long(
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

        /**
         * Show until dismissed manually, or until another snackbar instance is bound
         */
        @JvmOverloads
        public fun make(
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

        /**
         * Show until dismissed manually, or until another snackbar instance is bound
         */
        @JvmOverloads
        public fun make(
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

        public companion object {

            private val DEFAULT_ON_SHOWN = { _: Snackbar -> }
            private val DEFAULT_ON_HIDDEN = { _: Snackbar, _: Int -> }
            private val DEFAULT_BUILDER: Snackbar.() -> Snackbar = { this }

            private fun Snackbar.materialMargin() {
                val params = view.layoutParams as? MarginLayoutParams
                if (params != null) {
                    val margin = 8.asDp(view.context)
                    view.updateLayoutParams<MarginLayoutParams> { setMargins(margin) }
                }
            }

            private fun Snackbar.materialElevation() {
                ViewCompat.setElevation(view, 6.asDp(context).toFloat())
            }

            private fun Snackbar.materialPadding() {
                // The Snackbar in material library sets a Material design theme but
                // it fucks the window insets if your app is using LAYOUT_HIDE_NAVIGATION
                // and adjusting for bottom padding - it adds the bottom padding from the insets
                // into the snackbar as well.
                view.updatePadding(left = 0, right = 0, top = 0, bottom = 0)
                ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
                    v.updatePadding(left = 0, right = 0, top = 0, bottom = 0)
                    return@setOnApplyWindowInsetsListener insets
                }
            }

            private fun Snackbar.materialDesign() {
                materialElevation()
                materialMargin()
                materialPadding()
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

    private data class Snacky(
        val lifecycle: Lifecycle,
        val instance: Instance
    )
}
