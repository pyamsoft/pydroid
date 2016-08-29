/*
 * Copyright 2016 Peter Kenji Yamanaka
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

package com.pyamsoft.pydroid.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.TypedValue;

public final class StringUtil {

  private StringUtil() {
    throw new RuntimeException("No instances");
  }

  /**
   * Takes an array of strings and creates a SpannableStringBuilder out of them If the array is
   * null or empty, returns null
   */
  @CheckResult @NonNull public static SpannableStringBuilder createBuilder(
      final @NonNull String... strs) {
    final int size = strs.length;
    if (size > 0) {
      final SpannableStringBuilder strb = new SpannableStringBuilder(strs[0]);
      for (int i = 1; i < size; ++i) {
        if (strs[i] != null) {
          strb.append(strs[i]);
        }
      }
      return strb;
    } else {
      throw new IllegalArgumentException("Cannot format empty array of strings");
    }
  }

  @CheckResult @NonNull
  public static SpannableStringBuilder createLineBreakBuilder(final @NonNull String... strs) {
    final int size = strs.length;
    if (size > 0) {
      final int sizeWithBreaks = size * 2 - 1;
      final String[] lineBreakStrings = new String[sizeWithBreaks];
      int j = 0;
      for (int i = 0; i < size; ++i, ++j) {
        lineBreakStrings[j] = strs[i];
        if (++j < sizeWithBreaks) {
          lineBreakStrings[j] = "\n\n";
        }
      }
      return createBuilder(lineBreakStrings);
    } else {
      throw new IllegalArgumentException("Cannot format empty array of strings");
    }
  }

  /**
   * Applies multiple Spans to a Spannable string Use passed in builder as the out parameter
   */
  public static void multiSpannable(final @NonNull Spannable out, final int start, final int stop,
      final @NonNull Object... spans) {
    for (final Object span : spans) {
      out.setSpan(span, start, stop, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
    }
  }

  public static void colorSpan(final @NonNull Spannable out, final int start, final int stop,
      final int color) {
    out.setSpan(new ForegroundColorSpan(color), start, stop, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
  }

  public static void boldSpan(final @NonNull Spannable out, final int start, final int stop) {
    out.setSpan(new StyleSpan(Typeface.BOLD), start, stop, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
  }

  public static void sizeSpan(final @NonNull Spannable out, final int start, final int stop,
      final int size) {
    out.setSpan(new AbsoluteSizeSpan(size), start, stop, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
  }

  @SuppressWarnings("WeakerAccess") @CheckResult @NonNull
  public static TypedArray getAttributeFromAppearance(final @NonNull Context context,
      final int style, final int attr) {
    final TypedValue typedValue = new TypedValue();
    context.getTheme().resolveAttribute(style, typedValue, true);
    return context.obtainStyledAttributes(typedValue.data, new int[] { attr });
  }

  @CheckResult public static int getTextSizeFromAppearance(final @NonNull Context context,
      final int textAppearance) {
    final TypedArray a =
        getAttributeFromAppearance(context, textAppearance, android.R.attr.textSize);
    final int textSize = a.getDimensionPixelSize(0, -1);
    a.recycle();
    return textSize;
  }

  @CheckResult public static int getTextColorFromAppearance(final @NonNull Context context,
      final int textAppearance) {
    final TypedArray a =
        getAttributeFromAppearance(context, textAppearance, android.R.attr.textColor);
    final int textSize = a.getColor(0, -1);
    a.recycle();
    return textSize;
  }
}
