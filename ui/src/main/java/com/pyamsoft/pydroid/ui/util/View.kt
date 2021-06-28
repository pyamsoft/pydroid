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
import androidx.lifecycle.LifecycleOwner
import com.pyamsoft.pydroid.ui.app.AppBarActivity
import com.pyamsoft.pydroid.util.doOnApplyWindowInsets
import com.pyamsoft.pydroid.util.doOnDestroy
import timber.log.Timber

@CheckResult
private fun getAccelCubicInterpolator(context: Context): Interpolator {
  return AnimationUtils.loadInterpolator(
      context.applicationContext, android.R.interpolator.accelerate_cubic)
}

/** Fade view in animation */
@CheckResult
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
public fun View.flipVertical(): ViewPropertyAnimatorCompat {
  val i: Interpolator = getAccelCubicInterpolator(context)
  return ViewCompat.animate(this)
      .scaleY(-scaleY)
      .setStartDelay(100)
      .setDuration(300)
      .setInterpolator(i)
}

/** Set a debounced on click listener */
public fun View.setOnDebouncedClickListener(listener: DebouncedOnClickListener?) {
  setOnClickListener(listener)
}

/** Set a debounced on click listener */
public inline fun View.setOnDebouncedClickListener(crossinline func: (View) -> Unit) {
  setOnClickListener(DebouncedOnClickListener.create(func))
}

private inline fun watchToolbarOffset(
    view: View,
    owner: LifecycleOwner,
    crossinline onNewMargin: (Int) -> Unit,
) {
  view.doOnApplyWindowInsets(owner) { _, insets, _ ->
    val toolbarTopMargin = insets.getInsets(WindowInsetsCompat.Type.systemBars()).top
    onNewMargin(toolbarTopMargin)
  }
}

private inline fun watchAppBarHeight(
    appBar: View,
    owner: LifecycleOwner,
    crossinline onNewHeight: (Int) -> Unit,
) {
  val listener = View.OnLayoutChangeListener { v, _, _, _, _, _, _, _, _ -> onNewHeight(v.height) }
  appBar.addOnLayoutChangeListener(listener)
  owner.doOnDestroy { appBar.removeOnLayoutChangeListener(listener) }
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
  Timber.d("Apply new offset padding: $view $newPadding")
  view.updatePadding(top = newPadding)
}

/** Offset the padding of the page content in relation to the existing app bar */
public fun View.applyAppBarOffset(appBarActivity: AppBarActivity, owner: LifecycleOwner) {
  val initialTopPadding = this.paddingTop
  appBarActivity.withAppBar { appBar ->

    // Keep track off last seen values here
    var lastOffset: Int? = null
    var lastHeight: Int? = null

    watchAppBarHeight(appBar, owner) { newHeight ->
      lastHeight = newHeight
      applyNewViewOffset(this, initialTopPadding, lastOffset, lastHeight)
    }

    watchToolbarOffset(this, owner) { newOffset ->
      lastOffset = newOffset
      applyNewViewOffset(this, initialTopPadding, lastOffset, lastHeight)
    }
  }
}
/** Offset the padding of the page content in relation to the existing toolbar */
public fun View.applyToolbarOffset(owner: LifecycleOwner) {
  val initialTopPadding = this.paddingTop

  watchToolbarOffset(this, owner) { newOffset ->
    applyNewViewOffset(this, initialTopPadding, newOffset, 0)
  }
}
