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

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.TextView;

public final class AnimUtil {

  private AnimUtil() {
    throw new RuntimeException("No instances");
  }

  @SuppressWarnings("WeakerAccess") @NonNull
  public static ViewPropertyAnimatorCompat popShow(final @NonNull View v, final int startDelay,
      final int duration) {
    final Interpolator i =
        AnimationUtils.loadInterpolator(v.getContext(), android.R.interpolator.overshoot);
    v.setAlpha(0f);
    v.setScaleX(0f);
    v.setScaleY(0f);
    return ViewCompat.animate(v)
        .alpha(1f)
        .scaleX(1f)
        .scaleY(1f)
        .setStartDelay(startDelay)
        .setDuration(duration)
        .setInterpolator(i)
        .setListener(new ViewPropertyAnimatorListener() {
          @Override public void onAnimationStart(View view) {
            view.setVisibility(View.VISIBLE);
          }

          @Override public void onAnimationEnd(View view) {
            view.setVisibility(View.VISIBLE);
          }

          @Override public void onAnimationCancel(View view) {
            view.setVisibility(View.VISIBLE);
          }
        });
  }

  @NonNull
  public static ViewPropertyAnimatorCompat popHide(final @NonNull View v, final int startDelay,
      final int duration) {
    final Interpolator i =
        AnimationUtils.loadInterpolator(v.getContext(), android.R.interpolator.overshoot);
    v.setAlpha(1f);
    v.setScaleX(1f);
    v.setScaleY(1f);
    v.setVisibility(View.VISIBLE);
    return ViewCompat.animate(v)
        .alpha(0f)
        .scaleX(0f)
        .scaleY(0f)
        .setStartDelay(startDelay)
        .setDuration(duration)
        .setInterpolator(i)
        .setListener(new ViewPropertyAnimatorListener() {
          @Override public void onAnimationStart(View view) {
            view.setVisibility(View.VISIBLE);
          }

          @Override public void onAnimationEnd(View view) {
            view.setVisibility(View.GONE);
          }

          @Override public void onAnimationCancel(View view) {
            view.setVisibility(View.GONE);
          }
        });
  }

  @SuppressWarnings("WeakerAccess") @NonNull
  public static ViewPropertyAnimatorCompat fadeIn(final @NonNull View v) {
    final Interpolator i =
        AnimationUtils.loadInterpolator(v.getContext(), android.R.interpolator.accelerate_cubic);
    v.setAlpha(0f);
    v.setScaleX(0.8f);
    v.setScaleY(0.8f);
    return ViewCompat.animate(v)
        .alpha(1f)
        .scaleX(1f)
        .scaleY(1f)
        .setStartDelay(300)
        .setDuration(900)
        .setInterpolator(i)
        .setListener(null);
  }

  @NonNull public static ViewPropertyAnimatorCompat fadeAway(final @NonNull View v) {
    final Interpolator i =
        AnimationUtils.loadInterpolator(v.getContext(), android.R.interpolator.accelerate_cubic);
    v.setAlpha(1f);
    v.setScaleX(1f);
    v.setScaleY(1f);
    return ViewCompat.animate(v)
        .alpha(0f)
        .setStartDelay(300)
        .setDuration(900)
        .setInterpolator(i)
        .setListener(null);
  }

  @NonNull public static ViewPropertyAnimatorCompat flipVertical(final @NonNull View v) {
    final Interpolator i =
        AnimationUtils.loadInterpolator(v.getContext(), android.R.interpolator.accelerate_cubic);
    return ViewCompat.animate(v)
        .scaleY(-v.getScaleY())
        .setStartDelay(100)
        .setDuration(300)
        .setInterpolator(i)
        .setListener(null);
  }

  public static void animateActionBarToolbar(final @NonNull Toolbar toolbar) {
    final View t = toolbar.getChildAt(0);
    if (t != null && t instanceof TextView &&
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      final TextView title = (TextView) t;
      AnimUtil.fadeIn(title).start();
    }

    final View amv = toolbar.getChildAt(1);
    if (amv != null && amv instanceof ActionMenuView) {
      final ActionMenuView actions = (ActionMenuView) amv;
      final int childCount = actions.getChildCount();
      final int duration = 200;
      int delay = 500;
      for (int i = 0; i < childCount; ++i) {
        final View item = actions.getChildAt(i);
        if (item == null) {
          continue;
        }
        AnimUtil.popShow(item, delay, duration).start();
        delay += duration;
      }
    }
  }
}
