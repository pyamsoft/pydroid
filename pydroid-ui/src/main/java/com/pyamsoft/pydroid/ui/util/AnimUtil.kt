/*
 *     Copyright (C) 2017 Peter Kenji Yamanaka
 *
 *     This program is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation; either version 2 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License along
 *     with this program; if not, write to the Free Software Foundation, Inc.,
 *     51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.pyamsoft.pydroid.ui.util

import android.content.Context
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.support.annotation.CheckResult
import android.support.v4.view.ViewCompat
import android.support.v4.view.ViewPropertyAnimatorCompat
import android.support.v4.view.ViewPropertyAnimatorListener
import android.support.v7.widget.ActionMenuView
import android.support.v7.widget.Toolbar
import android.view.View
import android.view.animation.AnimationUtils
import android.view.animation.Interpolator
import android.widget.TextView
import com.pyamsoft.pydroid.helper.notNull

object AnimUtil {

  private var overshootInterpolator: Interpolator? = null
  private var accelCubicInterpolator: Interpolator? = null

  @CheckResult private fun getOvershootInterpolator(context: Context): Interpolator {
    if (overshootInterpolator == null) {
      overshootInterpolator = AnimationUtils.loadInterpolator(context.applicationContext,
          android.R.interpolator.overshoot)
    }

    return overshootInterpolator.notNull("overshootInterpolator")
  }

  @CheckResult private fun getAccelCubicInterpolator(context: Context): Interpolator {
    if (accelCubicInterpolator == null) {
      accelCubicInterpolator = AnimationUtils.loadInterpolator(context.applicationContext,
          android.R.interpolator.accelerate_cubic)
    }

    return accelCubicInterpolator.notNull("accelCubicInterpolator")
  }


  fun popShow(v: View, startDelay: Int, duration: Int): ViewPropertyAnimatorCompat {
    val i: Interpolator = getOvershootInterpolator(v.context)
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


  fun popHide(v: View, startDelay: Int, duration: Int): ViewPropertyAnimatorCompat {
    val i: Interpolator = getOvershootInterpolator(v.context)
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


  fun fadeIn(v: View): ViewPropertyAnimatorCompat {
    val i: Interpolator = getAccelCubicInterpolator(v.context)
    v.alpha = 0f
    v.scaleX = 0.8f
    v.scaleY = 0.8f
    return ViewCompat.animate(v).alpha(1f).scaleX(1f).scaleY(1f).setStartDelay(300).setDuration(
        900).setInterpolator(i).setListener(null)
  }


  fun fadeAway(v: View): ViewPropertyAnimatorCompat {
    val i: Interpolator = getAccelCubicInterpolator(v.context)
    v.alpha = 1f
    v.scaleX = 1f
    v.scaleY = 1f
    return ViewCompat.animate(v).alpha(0f).setStartDelay(300).setDuration(900).setInterpolator(
        i).setListener(null)
  }


  fun flipVertical(v: View): ViewPropertyAnimatorCompat {
    val i: Interpolator = getAccelCubicInterpolator(v.context)
    return ViewCompat.animate(v).scaleY(-v.scaleY).setStartDelay(100).setDuration(
        300).setInterpolator(i).setListener(null)
  }


  fun animateActionBarToolbar(toolbar: Toolbar) {
    val t = toolbar.getChildAt(0)
    if (t is TextView && VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
      fadeIn(t).start()
    }

    val amv = toolbar.getChildAt(1)
    if (amv is ActionMenuView) {
      val duration = 200
      var delay = 500
      for (i in 0 until amv.childCount) {
        val item = amv.getChildAt(i) ?: continue
        popShow(item, delay, duration).start()
        delay += duration
      }
    }
  }
}
