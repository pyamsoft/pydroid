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
   * null or empty, throws if null
   */
  @CheckResult
  fun createBuilder(vararg strs: String): SpannableStringBuilder {
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

  /**
   * Build a list of strings with line breaks in between each string
   */
  @CheckResult
  fun createLineBreakBuilder(vararg strs: String): SpannableStringBuilder {
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

  fun colorSpan(out: Spannable, start: Int, stop: Int, @ColorInt color: Int) {
    out.setSpan(ForegroundColorSpan(color), start, stop, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
  }

  fun boldSpan(out: Spannable, start: Int, stop: Int) {
    out.setSpan(StyleSpan(Typeface.BOLD), start, stop, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
  }

  fun sizeSpan(out: Spannable, start: Int, stop: Int, @Size size: Int) {
    out.setSpan(AbsoluteSizeSpan(size), start, stop, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
  }

  @SuppressLint("Recycle")
  @CheckResult
  fun getAttributeFromAppearance(context: Context,
      @AttrRes style: Int, @AttrRes attr: Int): TypedArray {
    val typedValue = TypedValue()
    context.theme.resolveAttribute(style, typedValue, true)
    return context.obtainStyledAttributes(typedValue.data, intArrayOf(attr))
  }

  @Size
  @CheckResult
  fun getTextSizeFromAppearance(context: Context,
      @AttrRes textAppearance: Int): Int {
    val a = getAttributeFromAppearance(context, textAppearance,
        attr.textSize)
    val textSize = a.getDimensionPixelSize(0, -1)
    a.recycle()
    return textSize
  }

  @ColorInt
  @CheckResult
  fun getTextColorFromAppearance(context: Context,
      @AttrRes textAppearance: Int): Int {
    val a = getAttributeFromAppearance(context, textAppearance,
        attr.textColor)
    val color = a.getColor(0, -1)
    a.recycle()
    return color
  }
}
