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

package com.pyamsoft.pydroid.ui.helper;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.annotation.CheckResult;
import android.support.annotation.ColorInt;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.pyamsoft.pydroid.helper.Checker;
import com.pyamsoft.pydroid.ui.R;
import com.pyamsoft.pydroid.util.AppUtil;

/**
 * Spinner view which takes over the screen and displays indeterminate progress
 *
 * Since the progress dialog class is deprecated in Android O, this overlay will serve as a shim
 * replacement.
 * The overlay is a full view which will take over the screen and eat input, but will not disrupt
 * the user experience as much as a dialog would
 */
public abstract class ProgressOverlay {

  private ProgressOverlay() {

  }

  @CheckResult @NonNull public static ProgressOverlay empty() {
    return new Empty();
  }

  public abstract void dispose();

  @CheckResult public abstract boolean isDisposed();

  private static final class Empty extends ProgressOverlay {

    Empty() {

    }

    @Override public void dispose() {

    }

    @Override public boolean isDisposed() {
      return false;
    }
  }

  public static final class Builder {

    @ColorInt private int backgroundColor;
    private int alphaPercent;
    @Nullable private ViewGroup rootViewGroup;
    @IdRes private int rootResId;
    private int elevation;

    public Builder() {
      alphaPercent = 50;
      backgroundColor = 0;
      rootResId = 0;
      rootViewGroup = null;
      elevation = 16;
    }

    @CheckResult @NonNull public Builder setElevation(int elevation) {
      if (elevation < 0) {
        throw new IllegalArgumentException("Cannot set negative elevation");
      }
      this.elevation = elevation;
      return this;
    }

    @CheckResult @NonNull public Builder setBackgroundColor(@ColorInt int backgroundColor) {
      this.backgroundColor = backgroundColor;
      return this;
    }

    @CheckResult @NonNull public Builder setAlphaPercent(int alphaPercent) {
      this.alphaPercent = alphaPercent;
      return this;
    }

    @CheckResult @NonNull public Builder setRootResId(@IdRes int rootResId) {
      this.rootResId = rootResId;
      return this;
    }

    @CheckResult @NonNull public Builder setRootViewGroup(@Nullable ViewGroup rootViewGroup) {
      this.rootViewGroup = Checker.checkNonNull(rootViewGroup);
      return this;
    }

    private void checkRootViewValidity() {
      if (rootResId == 0 && rootViewGroup == null) {
        throw new IllegalStateException("Must set root view by either ViewGroup or Resource Id");
      }
    }

    @CheckResult @NonNull private ProgressOverlay inflateOverlay(@NonNull Activity activity,
        @NonNull ViewGroup rootView) {
      activity = Checker.checkNonNull(activity);
      rootView = Checker.checkNonNull(rootView);

      View overlay =
          LayoutInflater.from(activity).inflate(R.layout.view_progress_overlay, rootView, false);

      // Set elevation to above basically everything
      // Make sure elevation cannot be negative
      elevation = Math.max(0, elevation);
      ViewCompat.setElevation(overlay, AppUtil.convertToDP(overlay.getContext(), elevation));

      // Set alpha
      overlay.getRootView().setAlpha(((float) alphaPercent / 100.0F));

      if (backgroundColor == 0) {
        // Get color from theme
        TypedValue themeValue = new TypedValue();
        activity.getTheme().resolveAttribute(android.R.attr.windowBackground, themeValue, true);
        if (themeValue.type >= TypedValue.TYPE_FIRST_COLOR_INT
            && themeValue.type <= TypedValue.TYPE_LAST_COLOR_INT) {
          // windowBackground is a color
          overlay.getRootView().setBackgroundColor(themeValue.data);
        } else {
          Drawable drawable = ContextCompat.getDrawable(activity, themeValue.resourceId);
          if (drawable != null) {
            // windowBackground is not a color, probably a drawable
            overlay.getRootView().setBackground(drawable);
          } else {
            // Default to white
            overlay.getRootView()
                .setBackgroundColor(ContextCompat.getColor(activity, android.R.color.white));
          }
        }
      } else {
        // Set custom defined color
        overlay.getRootView().setBackgroundColor(backgroundColor);
      }

      return new Impl(rootView, overlay);
    }

    @CheckResult @NonNull public ProgressOverlay build(@NonNull Activity activity) {
      activity = Checker.checkNonNull(activity);
      checkRootViewValidity();
      if (rootViewGroup == null) {
        View rootView = activity.findViewById(rootResId);
        if (rootView instanceof ViewGroup) {
          return inflateOverlay(activity, (ViewGroup) rootView);
        } else {
          throw new IllegalStateException("Root view is not a ViewGroup");
        }
      } else {
        return inflateOverlay(activity, rootViewGroup);
      }
    }
  }

  public static final class Helper {

    private Helper() {
      throw new RuntimeException("No instances");
    }

    @CheckResult @NonNull public static ProgressOverlay dispose(@Nullable ProgressOverlay overlay) {
      if (overlay == null) {
        return empty();
      }

      if (!overlay.isDisposed()) {
        overlay.dispose();
      }
      return empty();
    }
  }

  private static final class Impl extends ProgressOverlay {

    @NonNull private final ViewGroup root;
    @NonNull private final View overlay;
    private boolean disposed;

    Impl(@NonNull ViewGroup root, @NonNull View overlay) {
      this.root = Checker.checkNonNull(root);
      this.overlay = Checker.checkNonNull(overlay);
      disposed = false;

      root.addView(overlay);
    }

    @Override public void dispose() {
      if (!disposed) {
        root.removeView(overlay);
        disposed = true;
      }
    }

    @Override public boolean isDisposed() {
      return disposed;
    }
  }
}
