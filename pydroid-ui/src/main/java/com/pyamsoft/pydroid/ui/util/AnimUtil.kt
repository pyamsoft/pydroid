/*
 * Copyright 2017 Peter Kenji Yamanaka
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
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.support.v4.view.ViewCompat
import android.support.v4.view.ViewPropertyAnimatorCompat
import android.support.v4.view.ViewPropertyAnimatorListener
import android.support.v7.widget.ActionMenuView
import android.support.v7.widget.Toolbar
import android.view.View
import android.view.animation.AnimationUtils
import android.view.animation.Interpolator
import android.widget.TextView
import com.pyamsoft.pydroid.ui.PYDroid

object AnimUtil {

  internal lateinit var context: Context
  private val overshootInterpolator: Interpolator
  private val accelCubicInterpolator: Interpolator

  init {
    PYDroid.with {
      it.inject(this)
    }

    overshootInterpolator = AnimationUtils.loadInterpolator(context.applicationContext,
        android.R.interpolator.overshoot)
    accelCubicInterpolator = AnimationUtils.loadInterpolator(context.applicationContext,
        android.R.interpolator.accelerate_cubic)
  }

  @JvmStatic fun popShow(v: View, startDelay: Int, duration: Int): ViewPropertyAnimatorCompat {
    val i = overshootInterpolator
    v.alpha = 0f
    v.scaleX = 0f
    v.scaleY = 0f
    return ViewCompat.animate(v).alpha(1f).scaleX(1f).scaleY(1f).setStartDelay(
        startDelay.toLong()).setDuration(duration.toLong()).setInterpolator(i).setListener(
        object : ViewPropertyAnimatorListener {
          override fun onAnimationStart(view: View) {
            view.visibility = View.VISIBLE
          }

          override fun onAnimationEnd(view: View) {
            view.visibility = View.VISIBLE
          }

          override fun onAnimationCancel(view: View) {
            view.visibility = View.VISIBLE
          }
        })
  }

  @JvmStatic fun popHide(v: View, startDelay: Int, duration: Int): ViewPropertyAnimatorCompat {
    val i = overshootInterpolator
    v.alpha = 1f
    v.scaleX = 1f
    v.scaleY = 1f
    v.visibility = View.VISIBLE
    return ViewCompat.animate(v).alpha(0f).scaleX(0f).scaleY(0f).setStartDelay(
        startDelay.toLong()).setDuration(duration.toLong()).setInterpolator(i).setListener(
        object : ViewPropertyAnimatorListener {
          override fun onAnimationStart(view: View) {
            view.visibility = View.VISIBLE
          }

          override fun onAnimationEnd(view: View) {
            view.visibility = View.GONE
          }

          override fun onAnimationCancel(view: View) {
            view.visibility = View.GONE
          }
        })
  }

  @JvmStatic fun fadeIn(v: View): ViewPropertyAnimatorCompat {
    val i = accelCubicInterpolator
    v.alpha = 0f
    v.scaleX = 0.8f
    v.scaleY = 0.8f
    return ViewCompat.animate(v).alpha(1f).scaleX(1f).scaleY(1f).setStartDelay(300).setDuration(
        900).setInterpolator(i).setListener(null)
  }

  @JvmStatic fun fadeAway(v: View): ViewPropertyAnimatorCompat {
    val i = accelCubicInterpolator
    v.alpha = 1f
    v.scaleX = 1f
    v.scaleY = 1f
    return ViewCompat.animate(v).alpha(0f).setStartDelay(300).setDuration(900).setInterpolator(
        i).setListener(null)
  }

  @JvmStatic fun flipVertical(v: View): ViewPropertyAnimatorCompat {
    val i = accelCubicInterpolator
    return ViewCompat.animate(v).scaleY(-v.scaleY).setStartDelay(100).setDuration(
        300).setInterpolator(i).setListener(null)
  }

  @JvmStatic fun animateActionBarToolbar(toolbar: Toolbar) {
    val t = toolbar.getChildAt(0)
    if (t is TextView && VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
      fadeIn(t).start()
    }

    val amv = toolbar.getChildAt(1)
    if (amv is ActionMenuView) {
      val duration = 200
      var delay = 500
      for (i in 0..amv.childCount - 1) {
        val item = amv.getChildAt(i) ?: continue
        popShow(item, delay, duration).start()
        delay += duration
      }
    }
  }
}
