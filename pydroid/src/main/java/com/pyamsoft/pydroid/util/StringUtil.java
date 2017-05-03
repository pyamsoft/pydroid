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
 *
 */

package com.pyamsoft.pydroid.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.annotation.AttrRes;
import android.support.annotation.CheckResult;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Size;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.TypedValue;
import com.pyamsoft.pydroid.helper.Checker;

public class StringUtil {

  private StringUtil() {
    throw new RuntimeException("No instances");
  }

  /**
   * Takes an array of strings and creates a SpannableStringBuilder out of them If the array is
   * null or empty, returns null
   */
  @SuppressWarnings("WeakerAccess") @CheckResult @NonNull
  public static SpannableStringBuilder createBuilder(@NonNull String... strs) {
    strs = Checker.checkNonNull(strs);

    int size = strs.length;
    if (size > 0) {
      SpannableStringBuilder strb = new SpannableStringBuilder(strs[0]);
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
  public static SpannableStringBuilder createLineBreakBuilder(@NonNull String... strs) {
    strs = Checker.checkNonNull(strs);

    int size = strs.length;
    if (size > 0) {
      int sizeWithBreaks = (size << 1) - 1;
      String[] lineBreakStrings = new String[sizeWithBreaks];
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

  public static void colorSpan(@NonNull Spannable out, int start, int stop, @ColorInt int color) {
    Checker.checkNonNull(out)
        .setSpan(new ForegroundColorSpan(color), start, stop, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
  }

  public static void boldSpan(@NonNull Spannable out, int start, int stop) {
    Checker.checkNonNull(out)
        .setSpan(new StyleSpan(Typeface.BOLD), start, stop, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
  }

  public static void sizeSpan(@NonNull Spannable out, int start, int stop, @Size int size) {
    Checker.checkNonNull(out)
        .setSpan(new AbsoluteSizeSpan(size), start, stop, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
  }

  @SuppressWarnings("WeakerAccess") @CheckResult @NonNull
  public static TypedArray getAttributeFromAppearance(@NonNull Context context, @AttrRes int style,
      @AttrRes int attr) {
    context = Checker.checkNonNull(context);
    TypedValue typedValue = new TypedValue();
    context.getTheme().resolveAttribute(style, typedValue, true);
    return context.obtainStyledAttributes(typedValue.data, new int[] { attr });
  }

  @Size @CheckResult public static int getTextSizeFromAppearance(@NonNull Context context,
      @AttrRes int textAppearance) {
    context = Checker.checkNonNull(context);
    TypedArray a = getAttributeFromAppearance(context, textAppearance, android.R.attr.textSize);
    int textSize = a.getDimensionPixelSize(0, -1);
    a.recycle();
    return textSize;
  }

  @ColorInt @CheckResult public static int getTextColorFromAppearance(@NonNull Context context,
      @AttrRes int textAppearance) {
    context = Checker.checkNonNull(context);
    TypedArray a = getAttributeFromAppearance(context, textAppearance, android.R.attr.textColor);
    int color = a.getColor(0, -1);
    a.recycle();
    return color;
  }
}
