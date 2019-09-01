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

import android.content.Context
import android.view.View
import android.view.animation.AnimationUtils
import android.view.animation.Interpolator
import androidx.annotation.CheckResult
import androidx.core.view.ViewCompat
import androidx.core.view.ViewPropertyAnimatorListener

@CheckResult
private fun getOvershootInterpolator(context: Context): Interpolator {
    return AnimationUtils.loadInterpolator(
        context.applicationContext,
        android.R.interpolator.overshoot
    )
}

@CheckResult
private fun getAccelCubicInterpolator(context: Context): Interpolator {
    return AnimationUtils.loadInterpolator(
        context.applicationContext,
        android.R.interpolator.accelerate_cubic
    )
}

@JvmOverloads
fun View.popShow(
    startDelay: Long = 300L,
    duration: Long = 500L,
    listener: ViewPropertyAnimatorListener? = null
) {
    val i: Interpolator = getOvershootInterpolator(context)
    alpha = 0f
    scaleX = 0f
    scaleY = 0f
    ViewCompat.animate(this)
        .alpha(1f)
        .scaleX(1f)
        .scaleY(1f)
        .setStartDelay(startDelay)
        .setDuration(duration)
        .setInterpolator(i)
        .setListener(listener ?: object : ViewPropertyAnimatorListener {
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

@JvmOverloads
fun View.popHide(
    startDelay: Long = 300L,
    duration: Long = 500L,
    listener: ViewPropertyAnimatorListener? = null
) {
    val i: Interpolator = getOvershootInterpolator(context)
    alpha = 1f
    scaleX = 1f
    scaleY = 1f
    visibility = View.VISIBLE
    ViewCompat.animate(this)
        .alpha(0f)
        .scaleX(0f)
        .scaleY(0f)
        .setStartDelay(startDelay)
        .setDuration(duration)
        .setInterpolator(i)
        .setListener(listener ?: object : ViewPropertyAnimatorListener {
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

@JvmOverloads
fun View.fadeIn(listener: ViewPropertyAnimatorListener? = null) {
    val i: Interpolator = getAccelCubicInterpolator(context)
    alpha = 0f
    scaleX = 0.8f
    scaleY = 0.8f
    ViewCompat.animate(this)
        .alpha(1f)
        .scaleX(1f)
        .scaleY(1f)
        .setStartDelay(300)
        .setDuration(900)
        .setInterpolator(i)
        .setListener(listener)
}

@JvmOverloads
fun View.fadeAway(listener: ViewPropertyAnimatorListener? = null) {
    val i: Interpolator = getAccelCubicInterpolator(context)
    alpha = 1f
    scaleX = 1f
    scaleY = 1f
    ViewCompat.animate(this)
        .alpha(0f)
        .setStartDelay(300)
        .setDuration(900)
        .setInterpolator(i)
        .setListener(listener)
}

@JvmOverloads
fun View.flipVertical(listener: ViewPropertyAnimatorListener? = null) {
    val i: Interpolator = getAccelCubicInterpolator(context)
    ViewCompat.animate(this)
        .scaleY(-scaleY)
        .setStartDelay(100)
        .setDuration(300)
        .setInterpolator(i)
        .setListener(listener)
}

fun View.setOnDebouncedClickListener(listener: DebouncedOnClickListener?) {
    setOnClickListener(listener)
}

inline fun View.setOnDebouncedClickListener(crossinline func: (View) -> Unit) {
    setOnClickListener(DebouncedOnClickListener.create(func))
}
