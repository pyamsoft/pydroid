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

import android.graphics.drawable.GradientDrawable
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CheckResult
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Lifecycle.Event.ON_DESTROY
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.google.android.material.snackbar.Snackbar
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.util.toDp
import java.util.concurrent.ConcurrentHashMap

object Snackbreak {

  private fun Snackbar.setMargin() {
    val params = view.layoutParams as? ViewGroup.MarginLayoutParams
    if (params != null) {
      val margin = 8.toDp(context)
      params.setMargins(margin, margin, margin, margin)
      view.layoutParams = params
    }
  }

  private fun Snackbar.setBackground() {
    val drawable = GradientDrawable().mutate() as GradientDrawable

    val background = drawable.apply {
      shape = GradientDrawable.RECTANGLE
      setColor(ContextCompat.getColor(context, R.color.snackbar))

      cornerRadius = 4.toDp(context)
          .toFloat()
    }

    view.background = background
  }

  private fun Snackbar.materialDesign() {
    setMargin()
    setBackground()
    ViewCompat.setElevation(view, 6.toDp(context).toFloat())
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

  private val cache: MutableMap<Lifecycle, Instance> by lazy { ConcurrentHashMap<Lifecycle, Instance>() }

  @CheckResult
  fun bindTo(owner: LifecycleOwner): Instance {
    return bindTo(owner.lifecycle)
  }

  @CheckResult
  fun bindTo(lifecycle: Lifecycle): Instance {
    if (cache.containsKey(lifecycle)) {
      return requireNotNull(cache[lifecycle])
    } else {
      return cacheInstance(lifecycle)
    }
  }

  @CheckResult
  private fun cacheInstance(lifecycle: Lifecycle): Instance {
    val instance = Instance()
    cache[lifecycle] = instance

    lifecycle.addObserver(object : LifecycleObserver {

      @Suppress("unused")
      @OnLifecycleEvent(ON_DESTROY)
      fun onDestroy() {
        lifecycle.removeObserver(this)
        cache.remove(lifecycle)
        instance.onDestroy()
      }

    })

    return instance
  }

  class Instance internal constructor() {

    private var alive = true
    private var snackbar: Snackbar? = null

    internal fun onDestroy() {
      dismiss()
      alive = false
    }

    private fun requireStillAlive() {
      require(alive) { "This Snackbreak.${Instance::class.java.simpleName} is Dead" }
    }

    fun dismiss() {
      snackbar?.dismiss()
      snackbar = null
    }

    @CheckResult
    private fun canShowNewSnackbar(force: Boolean): Boolean {
      if (force) {
        return true
      } else {
        return snackbar.let { if (it == null) true else !it.isShownOrQueued }
      }
    }

    private inline fun snack(
      force: Boolean,
      builder: Snackbar.() -> Snackbar,
      snack: () -> Snackbar
    ) {
      requireStillAlive()
      if (canShowNewSnackbar(force)) {
        dismiss()
        snackbar = snack()
            .run(builder)
            .also { it.show() }
      }
    }

    @JvmOverloads
    @Deprecated("Use make() to fit in better to the MVI flow")
    fun short(
      view: View,
      message: CharSequence,
      force: Boolean = false,
      builder: Snackbar.() -> Snackbar = { this }
    ) {
      snack(force, builder) { make(view, message, Snackbar.LENGTH_SHORT) }
    }

    @JvmOverloads
    @Deprecated("Use make() to fit in better to the MVI flow")
    fun short(
      view: View,
      @StringRes message: Int,
      force: Boolean = false,
      builder: Snackbar.() -> Snackbar = { this }
    ) {
      snack(force, builder) { make(view, message, Snackbar.LENGTH_SHORT) }
    }

    @JvmOverloads
    @Deprecated("Use make() to fit in better to the MVI flow")
    fun long(
      view: View,
      message: CharSequence,
      force: Boolean = false,
      builder: Snackbar.() -> Snackbar = { this }
    ) {
      snack(force, builder) { make(view, message, Snackbar.LENGTH_LONG) }
    }

    @JvmOverloads
    @Deprecated("Use make() to fit in better to the MVI flow")
    fun long(
      view: View,
      @StringRes message: Int,
      force: Boolean = false,
      builder: Snackbar.() -> Snackbar = { this }
    ) {
      snack(force, builder) { make(view, message, Snackbar.LENGTH_LONG) }
    }

    @JvmOverloads
    @Deprecated("Use make() instead", ReplaceWith("make(view, message)"))
    fun indefinite(
      view: View,
      message: CharSequence,
      force: Boolean = false,
      builder: Snackbar.() -> Snackbar = { this }
    ) {
      make(view, message, builder)
    }

    @JvmOverloads
    fun make(
      view: View,
      message: CharSequence,
      builder: Snackbar.() -> Snackbar = { this }
    ) {
      snack(false, builder) { make(view, message, Snackbar.LENGTH_INDEFINITE) }
    }

    @JvmOverloads
    @Deprecated("Use make() instead", ReplaceWith("make(view, message)"))
    fun indefinite(
      view: View,
      @StringRes message: Int,
      force: Boolean = false,
      builder: Snackbar.() -> Snackbar = { this }
    ) {
      make(view, message, builder)
    }

    @JvmOverloads
    fun make(
      view: View,
      @StringRes message: Int,
      builder: Snackbar.() -> Snackbar = { this }
    ) {
      snack(false, builder) { make(view, message, Snackbar.LENGTH_INDEFINITE) }
    }

  }

}
