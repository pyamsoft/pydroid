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
import android.support.v7.content.res.AppCompatResources
import android.support.v7.widget.Toolbar
import com.pyamsoft.pydroid.ui.R
import kotlin.LazyThreadSafetyMode.NONE

private val iconCache: MutableMap<Int, Drawable> by lazy(NONE) { LinkedHashMap<Int, Drawable>() }

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
        // Pull icon from cache if possible
        icon = iconCache[0]

        if (icon == null) {
            // If not icon is available, resolve it from the current theme
            val typedArray: TypedArray = context.obtainStyledAttributes(
                    intArrayOf(R.attr.toolbarStyle))
            icon = typedArray.getDrawable(0)
            typedArray.recycle()

            // Cache the loaded icon
            if (icon != null) {
                iconCache.put(0, icon)
            }
        }
    }

    if (icon != null) {
        navigationIcon = icon
    }
}

fun Toolbar.setUpEnabled(up: Boolean, customId: Int = 0) {
    var customIcon: Drawable?
    if (customId == 0) {
        customIcon = null
    } else {
        // Pull from cache if available
        customIcon = iconCache[customId]
        if (customIcon == null) {
            // Load icon into drawable and cache it
            customIcon = AppCompatResources.getDrawable(context, customId)

            if (customIcon != null) {
                iconCache.put(customId, customIcon)
            }
        }
    }
    setUpEnabled(up, customIcon)
}

fun Toolbar.setUpEnabled(up: Boolean, customIcon: Drawable? = null) {
    if (up) {
        showUpIcon(customIcon)
    } else {
        navigationIcon = null
    }
}
