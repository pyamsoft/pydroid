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

import android.app.Activity
import android.content.Context
import android.util.DisplayMetrics
import android.util.SparseIntArray
import android.util.TypedValue
import androidx.annotation.CheckResult
import androidx.annotation.Dimension
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
private fun pxAsDp(activity: Activity, @Px px: Int): Int {
  return pxAsDpRawContext(activity, px)
}

@CheckResult
private fun pxAsDpRawContext(c: Context, @Px px: Int): Int {
  return if (px <= 0) 0
  else
      cachedDP.getOrElse(px) { array ->
        val m: DisplayMetrics = c.resources.displayMetrics
        return@getOrElse TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, px.toFloat(), m)
            .roundToInt()
            .also { array.put(px, it) }
      }
}

@CheckResult
private fun dpAsPx(activity: Activity, @Dimension(unit = Dimension.DP) dp: Int): Int {
  return dpAsPxRawContext(activity, dp)
}

@CheckResult
private fun dpAsPxRawContext(c: Context, @Dimension(unit = Dimension.DP) dp: Int): Int {
  return if (dp <= 0) 0
  else
      TypedValue.applyDimension(
              TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), c.resources.displayMetrics)
          .toInt()
}

/**
 * Convert a number in Pixels to DP
 *
 * Prefer this method over the one that only takes a context [Number.asDp]
 */
@CheckResult public fun Number.asDp(activity: Activity): Int = pxAsDp(activity, this.toInt())

/**
 * Convert a number in Pixels to DP
 *
 * Prefer the method that takes an activity over this one [Number.asDp]
 */
@CheckResult public fun Number.asDp(c: Context): Int = pxAsDpRawContext(c, this.toInt())

/**
 * Convert a number in DP to Pixels
 *
 * Prefer this method over the one that only takes a context [Number.asPx]
 */
@CheckResult public fun Number.asPx(activity: Activity): Int = dpAsPx(activity, this.toInt())

/**
 * Convert a number in DP to Pixels
 *
 * Prefer the method that takes an activity over this one [Number.asPx]
 */
@CheckResult public fun Number.asPx(c: Context): Int = dpAsPxRawContext(c, this.toInt())
