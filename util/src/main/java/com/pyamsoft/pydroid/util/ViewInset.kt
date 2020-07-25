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
 *
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

fun Activity.stableLayoutHideNavigation() {
    val isLandscape =
        this.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
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
    this.decorView.systemUiVisibility = this.decorView.systemUiVisibility or
        View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

    if (isLandscape) {
        // In landscape mode, navbar is marked immersive sticky
        this.decorView.systemUiVisibility = this.decorView.systemUiVisibility or
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
    }
}

fun View.doOnApplyWindowInsets(func: (v: View, insets: WindowInsetsCompat, padding: InitialPadding) -> Unit) {
    // Create a snapshot of the view's padding state
    val initialPadding = recordInitialPaddingForView(this)

    // Set an actual OnApplyWindowInsetsListener which proxies to the given
    // lambda, also passing in the original padding state
    ViewCompat.setOnApplyWindowInsetsListener(this) { v, insets ->
        func(v, insets, initialPadding)
        // Always return the insets, so that children can also use them
        return@setOnApplyWindowInsetsListener insets
    }

    // request some insets
    this.requestApplyInsetsWhenAttached()
}

data class InitialPadding internal constructor(
    val left: Int,
    val top: Int,
    val right: Int,
    val bottom: Int
)

@CheckResult
private fun recordInitialPaddingForView(view: View): InitialPadding {
    return InitialPadding(
        view.paddingLeft, view.paddingTop, view.paddingRight, view.paddingBottom
    )
}

private fun View.requestApplyInsetsWhenAttached() {
    if (isAttachedToWindow) {
        // We're already attached, just request as normal
        this.requestApplyInsets()
    } else {
        // We're not attached to the hierarchy, add a listener to
        // request when we are
        this.addOnAttachStateChangeListener(object : OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View) {
                v.removeOnAttachStateChangeListener(this)
                v.requestApplyInsets()
            }

            override fun onViewDetachedFromWindow(v: View) {}
        })
    }
}
