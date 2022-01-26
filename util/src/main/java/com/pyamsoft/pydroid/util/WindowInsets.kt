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
import android.view.Window
import androidx.core.view.WindowCompat
import com.pyamsoft.pydroid.core.Logger

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
  WindowCompat.setDecorFitsSystemWindows(this, false)

  Logger.d("This is where we would hide the navbar in landscape, but it crashes? $isLandscape")
  //  if (isLandscape) {
  //    this.insetsController?.systemBarsBehavior =
  //        WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
  //  }
}

@Suppress("DEPRECATION")
private fun Window.oldStableLayoutHideNavigation(isLandscape: Boolean) {
  WindowCompat.setDecorFitsSystemWindows(this, false)

  Logger.d("This is where we would hide the navbar in landscape, but it crashes? $isLandscape")
  //  if (isLandscape) {
  //    // In landscape mode, navbar is marked immersive sticky
  //    this.decorView.systemUiVisibility =
  //        this.decorView.systemUiVisibility or
  //            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
  //            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
  //  }
}
