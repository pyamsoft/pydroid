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
 *
 */

package com.pyamsoft.pydroid.ui.app.fragment;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.annotation.CheckResult;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

final class ActionBarFragmentUtil {

  private ActionBarFragmentUtil() {
    throw new RuntimeException("No instances");
  }

  @CheckResult @Nullable private static ActionBar getActionBar(@NonNull Activity activity) {
    if (activity instanceof AppCompatActivity) {
      final AppCompatActivity appCompatActivity = (AppCompatActivity) activity;
      return appCompatActivity.getSupportActionBar();
    } else {
      throw new ClassCastException("Activity not instance of AppCompatActivity");
    }
  }

  static void setActionBarUpEnabled(@NonNull Activity activity, boolean up) {
    setActionBarUpEnabled(activity, up, null);
  }

  static void setActionBarUpEnabled(@NonNull Activity activity, boolean up, @DrawableRes int icon) {
    final Drawable d;
    if (icon != 0) {
      d = ContextCompat.getDrawable(activity, icon);
    } else {
      d = null;
    }

    setActionBarUpEnabled(activity, up, d);
  }

  static void setActionBarUpEnabled(@NonNull Activity activity, boolean up,
      @Nullable Drawable icon) {
    final ActionBar bar = getActionBar(activity);
    if (bar != null) {
      bar.setHomeButtonEnabled(up);
      bar.setDisplayHomeAsUpEnabled(up);
      bar.setHomeAsUpIndicator(icon);
    }
  }

  static void setActionBarTitle(@NonNull Activity activity, @NonNull CharSequence title) {
    final ActionBar bar = getActionBar(activity);
    if (bar != null) {
      bar.setTitle(title);
    }
  }

  static void setActionBarTitle(@NonNull Activity activity, @StringRes int title) {
    final ActionBar bar = getActionBar(activity);
    if (bar != null) {
      bar.setTitle(title);
    }
  }
}
