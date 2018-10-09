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

import android.graphics.drawable.Drawable
import androidx.annotation.CheckResult
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.Toolbar
import androidx.core.content.withStyledAttributes
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.util.tintWith

private var cachedIcon: Drawable? = null

@CheckResult
private fun Toolbar.loadIcon(): Drawable? {
  // Pull icon from cache if possible
  var icon = cachedIcon

  if (icon == null) {
    // If no icon is available, resolve it from the current theme
    context.withStyledAttributes(R.attr.toolbarStyle, intArrayOf(R.attr.homeAsUpIndicator)) {
      val resId = getResourceId(0, 0)
      if (resId != 0) {
        icon = AppCompatResources.getDrawable(context, resId)
            ?.tintWith(context, R.color.white)
      }
    }

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
