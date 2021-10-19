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

package com.pyamsoft.pydroid.util

import android.annotation.SuppressLint
import android.app.Activity
import android.content.res.Configuration
import android.os.Build
import android.view.View
import android.view.View.OnAttachStateChangeListener
import android.view.Window
import android.view.WindowInsetsController
import androidx.annotation.CheckResult
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

/** A listener which responds to some kind of change on WindowInsets */
@Deprecated("Migrate to Jetpack Compose")
public fun interface InsetListener {

  /** Cancel the inset listener */
  public fun cancel()
}

/**
 * Places the Activity into Stable Layout and allows the app to draw behind the navbar and status
 * bar.
 *
 * You must handle insets on your own.
 */
public fun Activity.stableLayoutHideNavigation() {
  val isLandscape = this.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
  val w = this.window
  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
    w.newStableLayoutHideNavigation(isLandscape)
  } else {
    w.oldStableLayoutHideNavigation(isLandscape)
  }
}

@SuppressLint("NewApi")
private fun Window.newStableLayoutHideNavigation(isLandscape: Boolean) {
  this.setDecorFitsSystemWindows(false)
  if (isLandscape) {
    this.insetsController?.systemBarsBehavior =
        WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
  }
}

@Suppress("DEPRECATION")
private fun Window.oldStableLayoutHideNavigation(isLandscape: Boolean) {
  this.decorView.systemUiVisibility =
      this.decorView.systemUiVisibility or
          View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
          View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
          View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

  if (isLandscape) {
    // In landscape mode, navbar is marked immersive sticky
    this.decorView.systemUiVisibility =
        this.decorView.systemUiVisibility or
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
  }
}

/** Run a block once when the WindowInsets are applied */
@CheckResult
@Deprecated("Migrate to Jetpack Compose")
public inline fun View.doOnApplyWindowInsets(
    crossinline func: (v: View, insets: WindowInsetsCompat, padding: InitialPadding) -> Unit
): InsetListener {
  val view = this
  // Create a snapshot of the view's padding state
  val initialPadding = recordInitialPaddingForView(view)

  // Set an actual OnApplyWindowInsetsListener which proxies to the given
  // lambda, also passing in the original padding state
  ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
    // Do the thing
    func(v, insets, initialPadding)

    // Always return the insets, so that children can also use them
    return@setOnApplyWindowInsetsListener insets
  }

  // request some insets
  view.requestApplyInsetsWhenAttached()

  return InsetListener { ViewCompat.setOnApplyWindowInsetsListener(view, null) }
}

/** The representation of the originalk padding of the application before it was modified */
@Deprecated("Migrate to Jetpack Compose")
public data class InitialPadding
internal constructor(
    /** Top padding */
    val top: Int,

    /** Bottom padding */
    val bottom: Int,

    /** Start padding */
    val start: Int,

    /** End padding */
    val end: Int,
)

@CheckResult
@PublishedApi
@Deprecated("Migrate to Jetpack Compose")
internal fun recordInitialPaddingForView(view: View): InitialPadding {
  return InitialPadding(
      top = view.paddingTop,
      bottom = view.paddingBottom,
      start = view.paddingStart,
      end = view.paddingEnd)
}

@PublishedApi
@Deprecated("Migrate to Jetpack Compose")
internal fun View.requestApplyInsetsWhenAttached() {
  if (isAttachedToWindow) {
    // We're already attached, just request as normal
    this.requestApplyInsets()
  } else {
    // We're not attached to the hierarchy, add a listener to
    // request when we are
    this.addOnAttachStateChangeListener(
        object : OnAttachStateChangeListener {
          override fun onViewAttachedToWindow(v: View) {
            v.removeOnAttachStateChangeListener(this)
            v.requestApplyInsets()
          }

          override fun onViewDetachedFromWindow(v: View) {}
        })
  }
}
