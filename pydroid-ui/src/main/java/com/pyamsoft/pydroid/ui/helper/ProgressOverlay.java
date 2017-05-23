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
 */

package com.pyamsoft.pydroid.ui.helper;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.annotation.CheckResult;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
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
@SuppressWarnings("WeakerAccess") public abstract class ProgressOverlay {

  private ProgressOverlay() {

  }

  @CheckResult @NonNull public static ProgressOverlay empty() {
    return new Empty();
  }

  @CheckResult @NonNull public static Builder builder() {
    return new Builder();
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
    @Nullable private ViewGroup rootViewGroup;
    private int alphaPercent;
    private int elevation;
    @StyleRes private int theme;

    Builder() {
      alphaPercent = 50;
      theme = 0;
      backgroundColor = 0;
      elevation = 16;
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

    @CheckResult @NonNull public Builder setRootViewGroup(@NonNull ViewGroup rootViewGroup) {
      this.rootViewGroup = rootViewGroup;
      return this;
    }

    @CheckResult @NonNull public Builder setTheme(@StyleRes int theme) {
      this.theme = theme;
      return this;
    }

    @CheckResult @NonNull private ProgressOverlay inflateOverlay(@NonNull Activity activity,
        @NonNull ViewGroup rootView) {
      activity = Checker.Companion.checkNonNull(activity);
      rootView = Checker.Companion.checkNonNull(rootView);

      final LayoutInflater inflater;
      if (theme == 0) {
        inflater = LayoutInflater.from(activity);
      } else {
        inflater =
            LayoutInflater.from(activity).cloneInContext(new ContextThemeWrapper(activity, theme));
      }
      ViewProgressOverlayBinding binding =
          ViewProgressOverlayBinding.inflate(inflater, rootView, false);

      // Set elevation to above basically everything
      // Make sure elevation cannot be negative
      elevation = Math.max(0, elevation);
      ViewCompat.setElevation(binding.getRoot(), AppUtil.Companion.convertToDP(activity, elevation));

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

      // Eat any click attempts while the overlay is showing
      binding.getRoot().setOnClickListener(v -> Timber.w("Eat click attempt with Overlay"));

      return new Impl(binding, rootView);
    }

    @CheckResult @NonNull public ProgressOverlay build(@NonNull Activity activity) {
      activity = Checker.Companion.checkNonNull(activity);
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

  private static final class Impl extends ProgressOverlay {

    @NonNull private final ViewProgressOverlayBinding binding;
    @NonNull private final ViewGroup root;
    private boolean disposed;

    Impl(@NonNull ViewProgressOverlayBinding binding, @NonNull ViewGroup root) {
      this.binding = Checker.Companion.checkNonNull(binding);
      this.root = Checker.Companion.checkNonNull(root);
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
