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
import android.view.View
import android.view.animation.AnimationUtils
import android.view.animation.Interpolator
import androidx.annotation.CheckResult
import androidx.core.view.ViewCompat
import androidx.core.view.ViewPropertyAnimatorCompat
import androidx.core.view.ViewPropertyAnimatorListener
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.google.android.material.appbar.AppBarLayout
import com.pyamsoft.pydroid.ui.app.AppBarActivity
import com.pyamsoft.pydroid.util.InsetListener
import com.pyamsoft.pydroid.util.doOnApplyWindowInsets

/** A listener which responds to some kind of change on a View */
@Deprecated("Migrate to Jetpack Compose")
public fun interface ViewListener {

  /** Cancel the view listener */
  public fun cancel()
}

@CheckResult
private fun getAccelCubicInterpolator(context: Context): Interpolator {
  return AnimationUtils.loadInterpolator(
      context.applicationContext, android.R.interpolator.accelerate_cubic)
}

/** Fade view in animation */
@CheckResult
@Deprecated("Migrate to Jetpack Compose")
public fun View.fadeIn(): ViewPropertyAnimatorCompat {
  val i: Interpolator = getAccelCubicInterpolator(context)
  alpha = 0f
  scaleX = 0.8f
  scaleY = 0.8f
  return ViewCompat.animate(this)
      .alpha(1f)
      .scaleX(1f)
      .scaleY(1f)
      .setStartDelay(300)
      .setDuration(900)
      .setInterpolator(i)
      .setListener(
          object : ViewPropertyAnimatorListener {
            override fun onAnimationEnd(view: View) {
              view.visibility = View.VISIBLE
            }

            override fun onAnimationCancel(view: View) {}

            override fun onAnimationStart(view: View) {
              view.visibility = View.VISIBLE
            }
          })
}

/** Fade view out animation */
@CheckResult
@Deprecated("Migrate to Jetpack Compose")
public fun View.fadeAway(): ViewPropertyAnimatorCompat {
  val i: Interpolator = getAccelCubicInterpolator(context)
  alpha = 1f
  scaleX = 1f
  scaleY = 1f
  return ViewCompat.animate(this)
      .alpha(0f)
      .setStartDelay(300)
      .setDuration(900)
      .setInterpolator(i)
      .setListener(
          object : ViewPropertyAnimatorListener {
            override fun onAnimationEnd(view: View) {
              view.visibility = View.GONE
            }

            override fun onAnimationCancel(view: View) {}

            override fun onAnimationStart(view: View) {
              view.visibility = View.VISIBLE
            }
          })
}

/** Flip a view vertically */
@CheckResult
@Deprecated("Migrate to Jetpack Compose")
public fun View.flipVertical(): ViewPropertyAnimatorCompat {
  val i: Interpolator = getAccelCubicInterpolator(context)
  return ViewCompat.animate(this)
      .scaleY(-scaleY)
      .setStartDelay(100)
      .setDuration(300)
      .setInterpolator(i)
}

/** Set a debounced on click listener */
@Deprecated("Migrate to Jetpack Compose")
public fun View.setOnDebouncedClickListener(listener: DebouncedOnClickListener?) {
  setOnClickListener(listener)
}

/** Set a debounced on click listener */
@Deprecated("Migrate to Jetpack Compose")
public inline fun View.setOnDebouncedClickListener(crossinline func: (View) -> Unit) {
  setOnClickListener(DebouncedOnClickListener.create(func))
}

/** Perform an action when the View Layout changes */
@Deprecated("Migrate to Jetpack Compose")
public inline fun View.doOnLayoutChanged(
    crossinline onChange: (View, Int, Int, Int, Int, Int, Int, Int, Int) -> Unit,
): ViewListener {
  val listener =
      View.OnLayoutChangeListener {
          v,
          left,
          top,
          right,
          bottom,
          oldLeft,
          oldTop,
          oldRight,
          oldBottom ->
        onChange(v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom)
      }

  this.addOnLayoutChangeListener(listener)
  return ViewListener { this.removeOnLayoutChangeListener(listener) }
}

@CheckResult
private inline fun watchToolbarOffset(
    view: View,
    crossinline onNewMargin: (Int) -> Unit,
): InsetListener {
  return view.doOnApplyWindowInsets { _, insets, _ ->
    val toolbarTopMargin = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
    onNewMargin(toolbarTopMargin)
  }
}

@CheckResult
private inline fun watchAppBarHeight(
    appBar: AppBarLayout,
    crossinline onNewHeight: (Int) -> Unit,
): ViewListener {
  return appBar.doOnLayoutChanged { v, _, _, _, _, _, _, _, _ -> onNewHeight(v.height) }.also {
    // Post in case not set up
    appBar.apply { post { onNewHeight(this.height) } }
  }
}

private fun applyNewViewOffset(
    view: View,
    initialTopPadding: Int,
    offset: Int?,
    appBarHeight: Int?,
) {
  if (offset == null) {
    return
  }

  if (appBarHeight == null) {
    return
  }

  val newPadding = initialTopPadding + offset + appBarHeight
  view.updatePadding(top = newPadding)
}

/** Offset the padding of the page content in relation to the existing app bar */
@CheckResult
@Deprecated("Migrate to Jetpack Compose")
public fun View.applyAppBarOffset(appBarActivity: AppBarActivity): ViewListener {
  val initialTopPadding = this.paddingTop

  return appBarActivity.requireAppBar { appBar ->

    // Keep track off last seen values here
    var lastOffset: Int? = null
    var lastHeight: Int? = null

    val appListener =
        watchAppBarHeight(appBar) { newHeight ->
          lastHeight = newHeight
          applyNewViewOffset(this, initialTopPadding, lastOffset, lastHeight)
        }

    val insetListener =
        watchToolbarOffset(this) { newOffset ->
          lastOffset = newOffset
          applyNewViewOffset(this, initialTopPadding, lastOffset, lastHeight)
        }

    return@requireAppBar ViewListener {
      appListener.cancel()
      insetListener.cancel()
    }
  }
}

/** Offset the padding of the page content in relation to the existing toolbar */
@CheckResult
@Deprecated("Migrate to Jetpack Compose")
public fun View.applyToolbarOffset(): InsetListener {
  val initialTopPadding = this.paddingTop

  return watchToolbarOffset(this) { newOffset ->
    applyNewViewOffset(this, initialTopPadding, newOffset, 0)
  }
}
