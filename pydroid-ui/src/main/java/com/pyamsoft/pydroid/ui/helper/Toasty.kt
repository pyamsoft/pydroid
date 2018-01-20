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

package com.pyamsoft.pydroid.ui.helper

import android.content.Context
import android.support.annotation.CheckResult
import android.support.annotation.StringRes
import android.widget.Toast

/**
 * Toasty is a drop in replacement for native Android toasts.
 *
 * It explicitly uses the application context to create the toast. If this is not done it is possible
 * though very unlikely for the Toast to leak the Activity context if it is displayed and then
 * dismissed very quickly
 */
object Toasty {

    enum class Duration {
        LENGTH_SHORT, LENGTH_LONG
    }

    @JvmField
    val LENGTH_SHORT = Duration.LENGTH_SHORT

    @JvmField
    val LENGTH_LONG = Duration.LENGTH_LONG

    @JvmStatic
    @CheckResult
    fun makeText(c: Context, message: CharSequence, duration: Duration): Toast {
        return Toast.makeText(
            c.applicationContext, message, when (duration) {
                Duration.LENGTH_SHORT -> Toast.LENGTH_SHORT
                Duration.LENGTH_LONG -> Toast.LENGTH_LONG
            }
        )
    }

    @JvmStatic
    @CheckResult
    fun makeText(c: Context, @StringRes resId: Int, duration: Duration): Toast =
        makeText(c, c.applicationContext.getString(resId), duration)

    @JvmStatic
    @CheckResult
    fun makeText(c: Context, @StringRes resId: Int, duration: Int): Toast =
        makeText(c, c.applicationContext.getString(resId), duration)

    @JvmStatic
    @CheckResult
    fun makeText(c: Context, message: CharSequence, duration: Int): Toast {
        return makeText(
            c, message, when (duration) {
                Toast.LENGTH_SHORT -> LENGTH_SHORT
                Toast.LENGTH_LONG -> LENGTH_LONG
                else -> throw IllegalArgumentException(
                    "Duration must be either Toast.LENGTH_SHORT or Toast.LENGTH_LONG"
                )
            }
        )
    }
}
