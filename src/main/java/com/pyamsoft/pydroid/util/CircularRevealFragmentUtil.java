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

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.CheckResult;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.DecelerateInterpolator;
import java.util.ArrayList;
import java.util.List;

public final class CircularRevealFragmentUtil {

  @SuppressWarnings("WeakerAccess") static final long DEFAULT_DURATION = 1000L;
  @SuppressWarnings("WeakerAccess") @NonNull static final String CENTER_X = "cX";
  @SuppressWarnings("WeakerAccess") @NonNull static final String CENTER_Y = "cY";
  @SuppressWarnings("WeakerAccess") @NonNull static final String ANIM_DURATION = "duration";
  @ColorRes private static final int DEFAULT_COLOR = 0;
  @NonNull private static final String BG_COLOR = "bg_color";

  private CircularRevealFragmentUtil() {
    throw new RuntimeException("No instances");
  }

  @CheckResult @NonNull public static Bundle bundleArguments(int cX, int cY) {
    return bundleArguments(cX, cY, DEFAULT_COLOR);
  }

  @SuppressWarnings("WeakerAccess") @CheckResult @NonNull
  public static Bundle bundleArguments(int cX, int cY, @ColorRes int color) {
    return bundleArguments(cX, cY, color, DEFAULT_DURATION);
  }

  @CheckResult @NonNull public static Bundle bundleArguments(int cX, int cY, long duration) {
    return bundleArguments(cX, cY, DEFAULT_COLOR, duration);
  }

  @SuppressWarnings("WeakerAccess") @CheckResult @NonNull
  public static Bundle bundleArguments(int cX, int cY, @ColorRes int color, long duration) {
    final Bundle args = new Bundle();
    args.putInt(CENTER_X, cX);
    args.putInt(CENTER_Y, cY);
    args.putInt(BG_COLOR, color);
    args.putLong(ANIM_DURATION, duration);
    return args;
  }

  @CheckResult @NonNull
  public static Bundle bundleArguments(@NonNull View fromView, @NonNull View containerView) {
    return bundleArguments(fromView, containerView, DEFAULT_COLOR);
  }

  @CheckResult @NonNull
  public static Bundle bundleArguments(@NonNull View fromView, @NonNull View containerView,
      @ColorRes int color) {
    return bundleArguments(fromView, containerView, color, DEFAULT_DURATION);
  }

  @CheckResult @NonNull
  public static Bundle bundleArguments(@NonNull View fromView, @NonNull View containerView,
      long duration) {
    return bundleArguments(fromView, containerView, DEFAULT_COLOR, duration);
  }

  @SuppressWarnings("WeakerAccess") @CheckResult @NonNull
  public static Bundle bundleArguments(@NonNull View fromView, @NonNull View containerView,
      @ColorRes int color, long duration) {
    final int[] fromLocation = new int[2];
    fromView.getLocationInWindow(fromLocation);

    final int[] containerLocation = new int[2];
    containerView.getLocationInWindow(containerLocation);

    final int relativeLeft = fromLocation[0] - containerLocation[0];
    final int relativeTop = fromLocation[1] - containerLocation[1];

    final int cX = fromView.getWidth() / 2 + relativeLeft;
    final int cY = fromView.getHeight() / 2 + relativeTop;

    return bundleArguments(cX, cY, color, duration);
  }

  public static void runCircularRevealOnViewCreated(@NonNull View view, Bundle arguments) {
    if (arguments == null) {
      throw new NullPointerException("Bundle arguments cannot be null");
    }

    final Context context = view.getContext();
    @ColorRes final int bgColor = arguments.getInt(BG_COLOR, DEFAULT_COLOR);
    if (bgColor != DEFAULT_COLOR) {
      view.setBackgroundColor(ContextCompat.getColor(context, bgColor));
    }

    // To run the animation as soon as the view is layout in the view hierarchy we add this
    // listener and remove it
    // as soon as it runs to prevent multiple animations if the view changes bounds
    view.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
      @Override
      public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft,
          int oldTop, int oldRight, int oldBottom) {
        v.removeOnLayoutChangeListener(this);

        final int cx = arguments.getInt(CENTER_X, 0);
        final int cy = arguments.getInt(CENTER_Y, 0);

        if (cx != 0 || cy != 0) {
          final List<Animator> animatorList = new ArrayList<>();
          final Animator alpha = ObjectAnimator.ofFloat(v, View.ALPHA, 0F, 1F);
          animatorList.add(alpha);

          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            final int radius = (int) Math.hypot(right, bottom);
            final Animator reveal = ViewAnimationUtils.createCircularReveal(v, cx, cy, 0, radius);
            animatorList.add(reveal);
          }

          final long duration = arguments.getLong(ANIM_DURATION, DEFAULT_DURATION);
          final AnimatorSet set = new AnimatorSet();
          set.setInterpolator(new DecelerateInterpolator(2F));
          set.setDuration(duration);
          set.playTogether(animatorList);
          set.start();
        }
      }
    });
  }
}
