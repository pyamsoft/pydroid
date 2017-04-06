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
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.CheckResult;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.pyamsoft.pydroid.helper.Checker;
import com.pyamsoft.pydroid.ui.databinding.ViewProgressOverlayBinding;
import com.pyamsoft.pydroid.util.AppUtil;
import timber.log.Timber;

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
    @ColorInt private int spinnerColor;
    @Nullable private ViewGroup rootViewGroup;
    private int alphaPercent;
    private int elevation;

    public Builder() {
      alphaPercent = 50;
      backgroundColor = 0;
      elevation = 16;
      spinnerColor = 0;
      rootViewGroup = null;
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

    @CheckResult @NonNull public Builder setSpinnerColor(int spinnerColor) {
      this.spinnerColor = spinnerColor;
      return this;
    }

    @CheckResult @NonNull public Builder setRootViewGroup(@NonNull ViewGroup rootViewGroup) {
      this.rootViewGroup = rootViewGroup;
      return this;
    }

    @CheckResult @NonNull private ProgressOverlay inflateOverlay(@NonNull Activity activity,
        @NonNull ViewGroup rootView) {
      activity = Checker.checkNonNull(activity);
      rootView = Checker.checkNonNull(rootView);

      ViewProgressOverlayBinding binding =
          ViewProgressOverlayBinding.inflate(LayoutInflater.from(activity), rootView, false);

      // Set elevation to above basically everything
      // Make sure elevation cannot be negative
      elevation = Math.max(0, elevation);
      ViewCompat.setElevation(binding.getRoot(), AppUtil.convertToDP(activity, elevation));

      // Set alpha
      binding.getRoot().setAlpha(((float) alphaPercent / 100.0F));

      if (backgroundColor == 0) {
        // Get color from theme
        TypedValue themeValue = new TypedValue();
        activity.getTheme().resolveAttribute(android.R.attr.windowBackground, themeValue, true);
        if (themeValue.type >= TypedValue.TYPE_FIRST_COLOR_INT
            && themeValue.type <= TypedValue.TYPE_LAST_COLOR_INT) {
          // windowBackground is a color
          binding.getRoot().setBackgroundColor(themeValue.data);
        } else {
          Drawable drawable = ContextCompat.getDrawable(activity, themeValue.resourceId);
          if (drawable != null) {
            // windowBackground is not a color, probably a drawable
            binding.getRoot().setBackground(drawable);
          } else {
            // Default to white
            binding.getRoot()
                .setBackgroundColor(ContextCompat.getColor(activity, android.R.color.white));
          }
        }
      } else {
        // Set custom defined color
        binding.getRoot().setBackgroundColor(backgroundColor);
      }

      if (spinnerColor != 0) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
          binding.progressOverlayBar.setIndeterminateTintList(ColorStateList.valueOf(spinnerColor));
        }
      }

      // Eat any click attempts while the overlay is showing
      binding.getRoot().setOnClickListener(v -> Timber.w("Eat click attempt with Overlay"));

      return new Impl(binding, rootView);
    }

    @CheckResult @NonNull public ProgressOverlay build(@NonNull Activity activity) {
      activity = Checker.checkNonNull(activity);
      final View rootView;
      if (rootViewGroup == null) {
        // Use the default Android content view as Overlay root
        Timber.d("Using Android content view as root");
        rootView = activity.getWindow().getDecorView().findViewById(android.R.id.content);
      } else {
        // Locate a view in the given activity and use it as the root view
        Timber.d("Using builder-defined view as root");
        rootView = rootViewGroup;
      }
      if (rootView instanceof ViewGroup) {
        return inflateOverlay(activity, (ViewGroup) rootView);
      } else {
        throw new IllegalStateException("Root view is not a ViewGroup");
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

    @NonNull private final ViewProgressOverlayBinding binding;
    @NonNull private final ViewGroup root;
    private boolean disposed;

    Impl(@NonNull ViewProgressOverlayBinding binding, @NonNull ViewGroup root) {
      this.binding = Checker.checkNonNull(binding);
      this.root = Checker.checkNonNull(root);
      disposed = false;

      root.addView(binding.getRoot());
    }

    @Override public void dispose() {
      if (!disposed) {
        root.removeView(binding.getRoot());
        binding.unbind();
        disposed = true;
      }
    }

    @Override public boolean isDisposed() {
      return disposed;
    }
  }
}
