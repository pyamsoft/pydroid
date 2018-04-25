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
import android.support.annotation.Px
import android.util.DisplayMetrics
import android.util.SparseIntArray
import android.util.TypedValue
import kotlin.math.roundToInt

private val cachedDP: SparseIntArray by lazy {
  SparseIntArray(10)
}


@CheckResult
private fun toDp(c: Context, @Px px: Int): Int {
  if (px <= 0) {
    return 0
  } else {
    val cached: Int = cachedDP[px, 0]
    if (cached != 0) {
      return cached
    } else {
      val m: DisplayMetrics = c.applicationContext.resources.displayMetrics
      val dp: Int = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, px.toFloat(), m)
          .roundToInt()
      cachedDP.put(px, dp)
      // Return
      return dp
    }
  }
}

@CheckResult
fun Number.toDp(c: Context): Int = toDp(c, this.toInt())
