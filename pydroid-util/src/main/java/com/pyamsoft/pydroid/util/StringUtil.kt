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

import android.R.attr
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Typeface
import android.support.annotation.AttrRes
import android.support.annotation.CheckResult
import android.support.annotation.ColorInt
import android.support.annotation.Size
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.TypedValue

object StringUtil {

  /**
   * Takes an array of strings and creates a SpannableStringBuilder out of them If the array is
   * null or empty, returns null
   */
  @JvmStatic @CheckResult fun createBuilder(vararg strs: String): SpannableStringBuilder {
    val size = strs.size
    if (size > 0) {
      val strb = SpannableStringBuilder(strs[0])
      for (i in 1 until size) {
        strb.append(strs[i])
      }
      return strb
    } else {
      throw IllegalArgumentException("Cannot format empty array of strings")
    }
  }

  @JvmStatic @CheckResult fun createLineBreakBuilder(vararg strs: String): SpannableStringBuilder {
    val size = strs.size
    if (size > 0) {
      val sizeWithBreaks = (size shl 1) - 1
      val lineBreakStrings = Array(sizeWithBreaks, { "" })
      var j = 0
      var i = 0
      while (i < size) {
        lineBreakStrings[j] = strs[i]
        if (++j < sizeWithBreaks) {
          lineBreakStrings[j] = "\n\n"
        }
        ++i
        ++j
      }
      return createBuilder(*lineBreakStrings)
    } else {
      throw IllegalArgumentException("Cannot format empty array of strings")
    }
  }

  @JvmStatic fun colorSpan(out: Spannable, start: Int, stop: Int, @ColorInt color: Int) {
    out.setSpan(ForegroundColorSpan(color), start, stop, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
  }

  @JvmStatic fun boldSpan(out: Spannable, start: Int, stop: Int) {
    out.setSpan(StyleSpan(Typeface.BOLD), start, stop, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
  }

  @JvmStatic fun sizeSpan(out: Spannable, start: Int, stop: Int, @Size size: Int) {
    out.setSpan(AbsoluteSizeSpan(size), start, stop, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
  }

  @SuppressLint("Recycle") @JvmStatic @CheckResult fun getAttributeFromAppearance(context: Context,
      @AttrRes style: Int, @AttrRes attr: Int): TypedArray {
    val typedValue = TypedValue()
    context.theme.resolveAttribute(style, typedValue, true)
    return context.obtainStyledAttributes(typedValue.data, intArrayOf(attr))
  }

  @JvmStatic @Size @CheckResult fun getTextSizeFromAppearance(context: Context,
      @AttrRes textAppearance: Int): Int {
    val a = getAttributeFromAppearance(context, textAppearance,
        attr.textSize)
    val textSize = a.getDimensionPixelSize(0, -1)
    a.recycle()
    return textSize
  }

  @JvmStatic @ColorInt @CheckResult fun getTextColorFromAppearance(context: Context,
      @AttrRes textAppearance: Int): Int {
    val a = getAttributeFromAppearance(context, textAppearance,
        attr.textColor)
    val color = a.getColor(0, -1)
    a.recycle()
    return color
  }
}
