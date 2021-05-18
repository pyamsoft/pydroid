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

import android.content.Context
import android.util.DisplayMetrics
import android.util.SparseIntArray
import android.util.TypedValue
import androidx.annotation.CheckResult
import androidx.annotation.Px
import kotlin.math.roundToInt

private val cachedDP by lazy { SparseIntArray(10) }

@CheckResult
private inline fun SparseIntArray.getOrElse(key: Int, block: (array: SparseIntArray) -> Int): Int {
  val fallbackValue = -1
  val result = this.get(key, fallbackValue)
  return if (result != fallbackValue) result else block(this)
}

@CheckResult
private fun asDp(c: Context, @Px px: Int): Int {
  return if (px <= 0) 0
  else
      cachedDP.getOrElse(px) { array ->
        val m: DisplayMetrics = c.applicationContext.resources.displayMetrics
        return@getOrElse TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, px.toFloat(), m)
            .roundToInt()
            .also { array.put(px, it) }
      }
}

/** Convert a number in pixels to DP */
@CheckResult public fun Number.asDp(c: Context): Int = asDp(c, this.toInt())
