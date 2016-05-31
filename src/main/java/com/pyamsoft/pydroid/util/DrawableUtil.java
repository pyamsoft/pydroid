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
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.support.annotation.CheckResult;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;

public final class DrawableUtil {

  private DrawableUtil() {

  }

  @NonNull @CheckResult public static ColorFilter colorFilter(final @ColorInt int color) {
    return new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN);
  }

  @NonNull @CheckResult
  public static ColorFilter colorFilter(final @NonNull Context cn, final @ColorRes int c) {
    final int color = ContextCompat.getColor(cn, c);
    return colorFilter(color);
  }

  @NonNull @CheckResult
  public static Drawable createOval(final @NonNull Context cn, final @ColorRes int c) {
    final @ColorInt int i = ContextCompat.getColor(cn, c);
    return createOval(i);
  }

  @NonNull @CheckResult public static ShapeDrawable createOval(final @ColorInt int color) {
    final ShapeDrawable sd = new ShapeDrawable(new OvalShape());
    sd.getPaint().setColor(color);
    return sd;
  }

  @NonNull @CheckResult
  public static Drawable tintDrawableFromColor(@NonNull Drawable d, final @ColorInt int c) {
    d = d.mutate();
    d.setColorFilter(colorFilter(c));
    return d;
  }

  @NonNull @CheckResult
  public static Drawable tintDrawableFromColor(final @NonNull Context c, final @DrawableRes int dr,
      final @ColorInt int cl) {
    final Drawable d = ContextCompat.getDrawable(c, dr);
    return tintDrawableFromColor(d, cl);
  }

  @NonNull @CheckResult
  public static Drawable tintDrawableFromRes(final @NonNull Context c, final @NonNull Drawable d,
      final @ColorRes int cl) {
    final @ColorInt int i = ContextCompat.getColor(c, cl);
    return tintDrawableFromColor(d, i);
  }

  @NonNull @CheckResult
  public static Drawable tintDrawableFromRes(final @NonNull Context c, final @DrawableRes int dr,
      final @ColorRes int cl) {
    final Drawable d = ContextCompat.getDrawable(c, dr);
    return tintDrawableFromRes(c, d, cl);
  }
}
