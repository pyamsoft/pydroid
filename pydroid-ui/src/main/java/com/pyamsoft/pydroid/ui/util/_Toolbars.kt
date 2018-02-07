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

@file:JvmName("Toolbars")
@file:JvmMultifileClass

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

import android.content.res.TypedArray
import android.graphics.drawable.Drawable
import android.support.v7.widget.Toolbar
import android.util.TypedValue
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
