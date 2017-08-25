/*
 * Copyright 2017 Peter Kenji Yamanaka
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
import android.graphics.ColorFilter
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.support.annotation.CheckResult
import android.support.annotation.ColorInt
import android.support.annotation.ColorRes
import android.support.v4.content.ContextCompat

object DrawableUtil {

  @JvmStatic @CheckResult fun colorFilter(@ColorInt color: Int): ColorFilter =
      PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN)

  @JvmStatic @CheckResult fun tintDrawableFromColor(d: Drawable, @ColorInt c: Int): Drawable {
    d.colorFilter = colorFilter(c)
    return d
  }

  @JvmStatic @CheckResult fun tintDrawableFromRes(c: Context, d: Drawable,
      @ColorRes cl: Int): Drawable {
    @ColorInt val i = ContextCompat.getColor(c, cl)
    return tintDrawableFromColor(d, i)
  }
}
