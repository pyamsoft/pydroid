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
import android.view.ViewGroup.MarginLayoutParams
import androidx.annotation.CheckResult
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.setMargins
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Lifecycle.Event.ON_DESTROY
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.google.android.material.snackbar.BaseTransientBottomBar.BaseCallback
import com.google.android.material.snackbar.Snackbar
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.util.toDp
import timber.log.Timber
import java.util.concurrent.ConcurrentHashMap

object Snackbreak {

  private val DEFAULT_ON_SHOWN = { _: Snackbar -> }
  private val DEFAULT_ON_HIDDEN = { _: Snackbar, _: Int -> }
  private val DEFAULT_BUILDER: Snackbar.() -> Snackbar = { this }

  private fun Snackbar.setMargin() {
    val params = view.layoutParams as? MarginLayoutParams
    if (params != null) {
      val margin = 8.toDp(view.context)
      view.updateLayoutParams<MarginLayoutParams> { setMargins(margin) }
      view.updatePadding(left = 0, right = 0, top = 0, bottom = 0)
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

  private data class CacheEntry(
    val instance: Instance,
    val id: String?
  )

  private val cache: MutableMap<Lifecycle, MutableSet<CacheEntry>> by lazy {
    ConcurrentHashMap<Lifecycle, MutableSet<CacheEntry>>()
  }

  inline fun bindTo(
    owner: LifecycleOwner,
    crossinline withInstance: Instance.() -> Unit
  ) {
    return bindTo(owner.lifecycle) { withInstance() }
  }

  inline fun bindTo(
    owner: LifecycleOwner,
    id: String,
    crossinline withInstance: Instance.() -> Unit
  ) {
    return bindTo(owner.lifecycle, id) { withInstance() }
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
    val instance = cache[lifecycle]
        ?.find { id == it.id }
        ?.instance
    if (instance == null) {
      Timber.d("Caching Snackbreak for later: $id")
      cacheInstance(lifecycle, id, withInstance)
    } else {
      Timber.d("Continue existing Snackbreak: $id")
      withInstance(instance)
    }
  }

  @PublishedApi
  internal fun cacheInstance(
    lifecycle: Lifecycle,
    id: String?,
    withInstance: Instance.() -> Unit
  ) {
    if (lifecycle.currentState == Lifecycle.State.DESTROYED) {
      Timber.w("Snackbreak is destroyed, cannot continue calling it")
      return
    }

    val instance = Instance()
    cache.getOrPut(lifecycle) {
      lifecycle.addObserver(object : LifecycleObserver {

        @Suppress("unused")
        @OnLifecycleEvent(ON_DESTROY)
        fun onDestroy() {
          lifecycle.removeObserver(this)
          cache.remove(lifecycle)
              ?.forEach { entry ->
                Timber.d("Destroy Snackbreak: ${entry.id}")
                entry.instance.onDestroy()
              }
        }

      })

      return@getOrPut mutableSetOf()
    }
        .add(CacheEntry(instance, id))
    withInstance(instance)
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
      crossinline onShown: (snackbar: Snackbar) -> Unit,
      crossinline onHidden: (snackbar: Snackbar, event: Int) -> Unit,
      builder: Snackbar.() -> Snackbar,
      snack: () -> Snackbar
    ) {
      requireStillAlive()
      if (canShowNewSnackbar(force)) {
        dismiss()
        snackbar = snack()
            .run(builder)
            .let { bar ->
              return@let bar.addCallback(object : BaseCallback<Snackbar>() {

                override fun onShown(transientBottomBar: Snackbar?) {
                  super.onShown(transientBottomBar)
                  bar.removeCallback(this)
                  onShown(bar)
                }
              })
            }
            .let { bar ->
              return@let bar.addCallback(object : BaseCallback<Snackbar>() {

                override fun onDismissed(
                  transientBottomBar: Snackbar?,
                  event: Int
                ) {
                  super.onDismissed(transientBottomBar, event)
                  bar.removeCallback(this)
                  onHidden(bar, event)
                }
              })
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
      onShown: (snackbar: Snackbar) -> Unit = DEFAULT_ON_SHOWN,
      onHidden: (snackbar: Snackbar, event: Int) -> Unit = DEFAULT_ON_HIDDEN,
      builder: Snackbar.() -> Snackbar = DEFAULT_BUILDER
    ) {
      snack(false, onShown, onHidden, builder) {
        make(view, message, Snackbar.LENGTH_INDEFINITE)
      }
    }

    @JvmOverloads
    fun make(
      view: View,
      @StringRes message: Int,
      onShown: (snackbar: Snackbar) -> Unit = DEFAULT_ON_SHOWN,
      onHidden: (snackbar: Snackbar, event: Int) -> Unit = DEFAULT_ON_HIDDEN,
      builder: Snackbar.() -> Snackbar = DEFAULT_BUILDER
    ) {
      snack(false, onShown, onHidden, builder) {
        make(view, message, Snackbar.LENGTH_INDEFINITE)
      }
    }

  }

}
