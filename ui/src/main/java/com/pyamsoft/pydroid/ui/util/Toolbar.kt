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

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import androidx.annotation.CheckResult
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.Toolbar
import androidx.core.content.withStyledAttributes
import com.google.android.material.R
import com.pyamsoft.pydroid.util.tintWith

private var cachedIcon: Drawable? = null

@CheckResult
private fun Toolbar.loadIcon(@ColorRes customColor: Int): Drawable? {
    // Pull icon from cache if possible
    var icon: Drawable? = cachedIcon

    if (icon == null) {
        // If no icon is available, resolve it from the current theme
        context.withStyledAttributes(
            R.attr.toolbarStyle,
            intArrayOf(
                R.attr.homeAsUpIndicator,
                R.attr.titleTextColor
            )
        ) {
            @DrawableRes val iconId = getResourceId(0, 0)
            if (iconId != 0) {
                // May be a vector on API < 21
                icon = AppCompatResources.getDrawable(context, iconId)
            }

            @ColorRes val colorId: Int
            if (customColor == 0) {
                // This warns about not being a styleable, but its ok I guess
                @SuppressLint("ResourceType")
                colorId = getResourceId(1, 0)
            } else {
                colorId = customColor
            }

            if (colorId != 0) {
                icon = icon?.tintWith(context, colorId)
            }
        }

        // Cache the loaded icon
        if (icon != null) {
            cachedIcon = icon
        }
    }

    return icon
}

private fun Toolbar.showUpIcon(customIcon: Drawable? = null, @ColorRes customColor: Int = 0) {
    var icon: Drawable? = customIcon ?: navigationIcon
    if (icon == null) {
        icon = loadIcon(customColor)
    }

    if (icon != null) {
        navigationIcon = icon
    }
}

/**
 * Show the toolbar up arrow
 */
@JvmOverloads
public fun Toolbar.setUpEnabled(
    up: Boolean,
    customIcon: Drawable? = null
) {
    if (up) {
        showUpIcon(customIcon)
    } else {
        navigationIcon = null
    }
}
