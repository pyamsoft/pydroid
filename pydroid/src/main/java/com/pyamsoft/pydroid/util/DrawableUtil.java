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
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.support.annotation.CheckResult;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import com.pyamsoft.pydroid.helper.Checker;

public final class DrawableUtil {

  private DrawableUtil() {
    throw new RuntimeException("No instances");
  }

  @NonNull @CheckResult public static ColorFilter colorFilter(final @ColorInt int color) {
    return new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN);
  }

  @NonNull @CheckResult
  public static Drawable tintDrawableFromColor(@NonNull Drawable d, final @ColorInt int c) {
    d = Checker.checkNonNull(d).mutate();
    d.setColorFilter(colorFilter(c));
    return d;
  }

  @NonNull @CheckResult
  public static Drawable tintDrawableFromRes(final @NonNull Context c, final @NonNull Drawable d,
      final @ColorRes int cl) {
    final @ColorInt int i = ContextCompat.getColor(c, cl);
    return tintDrawableFromColor(d, i);
  }
}
