/*
 * Copyright (C) 2018 Peter Kenji Yamanaka
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pyamsoft.pydroid.ui.util

import android.content.res.TypedArray
import android.graphics.drawable.Drawable
import android.os.Build
import android.support.v7.widget.ActionMenuView
import android.support.v7.widget.Toolbar
import android.util.TypedValue
import android.widget.TextView
import com.pyamsoft.pydroid.ui.R

private var cachedIcon: Drawable? = null

private fun Toolbar.loadIcon(): Drawable? {
  // Pull icon from cache if possible
  var icon = cachedIcon

  if (icon == null) {
    // If not icon is available, resolve it from the current theme
    val typedValue = TypedValue()
    context.theme.resolveAttribute(R.attr.toolbarStyle, typedValue, true)
    val typedArray: TypedArray = context.obtainStyledAttributes(
        typedValue.data,
        intArrayOf(R.attr.homeAsUpIndicator)
    )
    icon = typedArray.getDrawable(0)
    typedArray.recycle()

    // Cache the loaded icon
    if (icon != null) {
      cachedIcon = icon
    }
  }

  return icon
}

private fun Toolbar.showUpIcon(customIcon: Drawable? = null) {
  var icon: Drawable?

  if (customIcon != null) {
    // Use the custom icon if available
    icon = customIcon
  } else {
    // Use the current icon if available
    icon = navigationIcon
  }

  if (icon == null) {
    icon = loadIcon()
  }

  if (icon != null) {
    navigationIcon = icon
  }
}

fun Toolbar.setUpEnabled(
  up: Boolean,
  customIcon: Drawable? = null
) {
  if (up) {
    showUpIcon(customIcon)
  } else {
    navigationIcon = null
  }
}

fun Toolbar.animateMenu() {
  val t = getChildAt(0)
  if (t is TextView && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
    t.fadeIn()
        .start()
  }

  val amv = getChildAt(1)
  if (amv is ActionMenuView) {
    val duration: Long = 300L
    var delay: Long = 500L
    for (i in 0 until amv.childCount) {
      val item = amv.getChildAt(i) ?: continue
      item.popShow(delay, duration)
          .start()
      delay += duration
    }
  }
}
