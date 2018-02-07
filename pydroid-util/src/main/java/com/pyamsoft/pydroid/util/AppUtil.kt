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

package com.pyamsoft.pydroid.util

import android.content.Context
import android.support.annotation.CheckResult
import android.util.TypedValue

object AppUtil {

  private val cachedDP: MutableMap<Float, Float> by lazy {
    LinkedHashMap<Float, Float>(10)
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
