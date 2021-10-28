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
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.BaseTransientBottomBar.BaseCallback
import com.google.android.material.snackbar.Snackbar
import com.pyamsoft.pydroid.core.Logger

import com.pyamsoft.pydroid.util.asDp
import com.pyamsoft.pydroid.util.doOnDestroy
import java.util.UUID

/** Snackbar manager with lifecycle */
@Deprecated("Migrate to Jetpack Compose")
public object Snackbreak {

  private fun <B : BaseTransientBottomBar<B>> B.materialMargin() {
    val params = view.layoutParams as? MarginLayoutParams
    if (params != null) {
      val margin = 8.asDp(view.context)
      view.updateLayoutParams<MarginLayoutParams> { setMargins(margin) }
    }
  }

  private fun <B : BaseTransientBottomBar<B>> B.materialElevation() {
    ViewCompat.setElevation(view, 6.asDp(context).toFloat())
  }

  private fun <B : BaseTransientBottomBar<B>> B.materialPadding() {
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

  @PublishedApi
  internal fun <B : BaseTransientBottomBar<B>> B.materialDesign() {
    materialElevation()
    materialMargin()
    materialPadding()
  }

  @PublishedApi
  internal val snackbarBreaker: Breaker<Snackbar> = Breaker { view, message, duration ->
    Snackbar.make(view, message, duration).apply { materialDesign() }
  }

  /** Create a custom Snackbreak.Breaker */
  @CheckResult
  @JvmOverloads
  public inline fun <reified B : BaseTransientBottomBar<B>> create(
      options: Options? = null,
      crossinline createBar: (view: View, message: CharSequence, duration: Int) -> B
  ): Breaker<B> {
    return Breaker { view, message, duration ->
      createBar(view, message, duration).apply {
        if (options?.applyMaterialDesign != false) {
          materialDesign()
        }
      }
    }
  }

  /** Bind to a lifecycle, automatically dismisses on lifecycle destroy */
  public inline fun bindTo(
      owner: LifecycleOwner,
      crossinline withInstance: Instance<Snackbar>.() -> Unit
  ) {
    return snackbarBreaker.bindTo(owner, withInstance)
  }

  /** Bind to a lifecycle, automatically dismisses on lifecycle destroy */
  public inline fun bindTo(
      lifecycle: Lifecycle,
      crossinline withInstance: Instance<Snackbar>.() -> Unit
  ) {
    return snackbarBreaker.bindTo(lifecycle, withInstance)
  }

  /** Snackbreak options */
  public data class Options(
      /** Apply material design to the BottomBar */
      public val applyMaterialDesign: Boolean
  )

  /** Class which handles the creation and lifecycle binding of BaseTransientBottomBar classes */
  public class Breaker<B : BaseTransientBottomBar<B>>
  @PublishedApi
  internal constructor(
      private val makeBar: (view: View, message: CharSequence, duration: Int) -> B,
  ) {

    /** Snackbreak cache */
    private var cached: Snacky<*>? = null

    /** Bind to a lifecycle, automatically dismisses on lifecycle destroy */
    public inline fun bindTo(
        owner: LifecycleOwner,
        crossinline withInstance: Instance<B>.() -> Unit
    ) {
      return bindTo(owner.lifecycle, withInstance)
    }

    /** Bind to a lifecycle, automatically dismisses on lifecycle destroy */
    public inline fun bindTo(
        lifecycle: Lifecycle,
        crossinline withInstance: Instance<B>.() -> Unit
    ) {
      return realBindTo(lifecycle) { withInstance() }
    }

    @PublishedApi
    internal fun realBindTo(lifecycle: Lifecycle, withInstance: Instance<B>.() -> Unit) {
      if (lifecycle.currentState == Lifecycle.State.DESTROYED) {
        return
      }

      withInstance(cacheInstance(lifecycle, makeBar))
    }

    @CheckResult
    private inline fun <B : BaseTransientBottomBar<B>> cacheInstance(
        lifecycle: Lifecycle,
        crossinline makeBar: (View, CharSequence, Int) -> B
    ): Instance<B> {
      val instance = Instance { view, message, duration -> makeBar(view, message, duration) }

      lifecycle.doOnDestroy {
        instance.destroy()

        // If this is the cache, null it out
        if (cached?.instance?.id == instance.id) {
          Logger.d("Clear Snackbreak.Breaker cached instance.")
          cached = null
        }
      }

      cached?.instance?.destroy()
      cached = Snacky(lifecycle, instance)
      return instance
    }
  }

  /** Bound bottom bar instance handler */
  public class Instance<B : BaseTransientBottomBar<B>>
  internal constructor(
      private val makeBar: (view: View, message: CharSequence, duration: Int) -> B,
  ) {

    internal val id = UUID.randomUUID().toString()

    private val defaultOnShown = { _: B -> }
    private val defaultOnHidden = { _: B, _: Int -> }
    private val defaulltBuilder: B.() -> B = { this }

    private var bottomBar: B? = null
    private var barCallback: BaseCallback<B>? = null

    internal fun destroy() {
      dismiss()
    }

    private fun clearRefs() {
      barCallback = null
      bottomBar = null
    }

    @CheckResult
    private fun canShowNewSnackbar(force: Boolean): Boolean {
      return if (force) true
      else {
        bottomBar.let { if (it == null) true else !it.isShownOrQueued }
      }
    }

    private inline fun createBar(
        force: Boolean,
        crossinline onShown: (bar: B) -> Unit,
        crossinline onHidden: (bar: B, event: Int) -> Unit,
        builder: B.() -> B,
        snack: () -> B
    ) {
      if (canShowNewSnackbar(force)) {
        dismiss()
        bottomBar =
            snack()
                .run(builder)
                .let { bar ->
                  val callback =
                      object : BaseCallback<B>() {

                        override fun onShown(transientBottomBar: B?) {
                          super.onShown(transientBottomBar)
                          onShown(bar)
                        }

                        override fun onDismissed(transientBottomBar: B?, event: Int) {
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

    /** Dismiss snackbar */
    public fun dismiss() {
      bottomBar?.also { bar ->
        barCallback?.also { bar.removeCallback(it) }
        bar.dismiss()
      }
      clearRefs()
    }

    /** Show for short time */
    @JvmOverloads
    public fun short(
        view: View,
        message: CharSequence,
        force: Boolean = false,
        onShown: (bar: B) -> Unit = defaultOnShown,
        onHidden: (bar: B, event: Int) -> Unit = defaultOnHidden,
        builder: B.() -> B = defaulltBuilder
    ) {
      createBar(force, onShown, onHidden, builder) {
        makeBar(view, message, BaseTransientBottomBar.LENGTH_SHORT)
      }
    }

    /** Show for short time */
    @JvmOverloads
    public fun short(
        view: View,
        @StringRes message: Int,
        force: Boolean = false,
        onShown: (bar: B) -> Unit = defaultOnShown,
        onHidden: (kbar: B, event: Int) -> Unit = defaultOnHidden,
        builder: B.() -> B = defaulltBuilder
    ) {
      createBar(force, onShown, onHidden, builder) {
        makeBar(view, view.resources.getText(message), BaseTransientBottomBar.LENGTH_SHORT)
      }
    }

    /** Show for long time */
    @JvmOverloads
    public fun long(
        view: View,
        message: CharSequence,
        force: Boolean = false,
        onShown: (bar: B) -> Unit = defaultOnShown,
        onHidden: (bar: B, event: Int) -> Unit = defaultOnHidden,
        builder: B.() -> B = defaulltBuilder
    ) {
      createBar(force, onShown, onHidden, builder) {
        makeBar(view, message, BaseTransientBottomBar.LENGTH_LONG)
      }
    }

    /** Show for long time */
    @JvmOverloads
    public fun long(
        view: View,
        @StringRes message: Int,
        force: Boolean = false,
        onShown: (bar: B) -> Unit = defaultOnShown,
        onHidden: (bar: B, event: Int) -> Unit = defaultOnHidden,
        builder: B.() -> B = defaulltBuilder
    ) {
      createBar(force, onShown, onHidden, builder) {
        makeBar(view, view.resources.getText(message), BaseTransientBottomBar.LENGTH_LONG)
      }
    }

    /** Show until dismissed manually, or until another snackbar instance is bound */
    @JvmOverloads
    public fun make(
        view: View,
        message: CharSequence,
        force: Boolean = false,
        onShown: (bar: B) -> Unit = defaultOnShown,
        onHidden: (bar: B, event: Int) -> Unit = defaultOnHidden,
        builder: B.() -> B = defaulltBuilder
    ) {
      createBar(force, onShown, onHidden, builder) {
        makeBar(view, message, BaseTransientBottomBar.LENGTH_INDEFINITE)
      }
    }

    /** Show until dismissed manually, or until another snackbar instance is bound */
    @JvmOverloads
    public fun make(
        view: View,
        @StringRes message: Int,
        force: Boolean = false,
        onShown: (bar: B) -> Unit = defaultOnShown,
        onHidden: (bar: B, event: Int) -> Unit = defaultOnHidden,
        builder: B.() -> B = defaulltBuilder
    ) {
      createBar(force, onShown, onHidden, builder) {
        makeBar(view, view.resources.getText(message), BaseTransientBottomBar.LENGTH_INDEFINITE)
      }
    }
  }

  private data class Snacky<B : BaseTransientBottomBar<B>>(
      val lifecycle: Lifecycle,
      val instance: Instance<B>
  )
}
