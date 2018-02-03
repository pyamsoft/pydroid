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

package com.pyamsoft.pydroid.util

import android.content.Context
import android.support.annotation.CheckResult
import android.util.TypedValue

object AppUtil {

  private val cachedDP: MutableMap<Float, Float> by lazy {
    HashMap<Float, Float>(10)
  }

  @JvmStatic
  @CheckResult
  fun convertToDP(
    c: Context,
    px: Float
  ): Float {
    return if (px <= 0F) {
      // Return
      0F
    } else {
      val cached: Float? = cachedDP[px]
      if (cached != null) {
        // Return
        cached
      } else {
        val m = c.applicationContext.resources.displayMetrics
        val dp = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, px, m)
        cachedDP[px] = dp
        // Return
        dp
      }
    }
  }
}
