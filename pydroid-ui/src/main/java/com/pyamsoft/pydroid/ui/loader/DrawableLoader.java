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

package com.pyamsoft.pydroid.ui.loader;

import android.support.annotation.CheckResult;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ImageView;
import com.pyamsoft.pydroid.function.ActionSingle;
import com.pyamsoft.pydroid.helper.Checker;

public final class DrawableLoader {

  private DrawableLoader() {
  }

  @CheckResult @NonNull public static Loader load(@DrawableRes int drawableRes) {
    return load(drawableRes, new RXLoader());
  }

  @SuppressWarnings("WeakerAccess") @CheckResult @NonNull
  public static Loader load(@DrawableRes int drawableRes, @NonNull Loader loader) {
    loader = Checker.checkNonNull(loader);
    loader.setResource(drawableRes);
    return loader;
  }

  public interface Loaded {

    void unload();

    @CheckResult boolean isUnloaded();
  }

  public static abstract class Loader {

    @DrawableRes private int resource;
    @ColorRes private int tint;
    @Nullable private ActionSingle<ImageView> startAction;
    @Nullable private ActionSingle<ImageView> errorAction;
    @Nullable private ActionSingle<ImageView> completeAction;

    protected Loader() {
      tint = 0;
    }

    void setResource(@DrawableRes int resource) {
      this.resource = resource;
    }

    @CheckResult @NonNull public final Loader tint(@ColorRes int color) {
      this.tint = color;
      return this;
    }

    @CheckResult @NonNull
    public final Loader setStartAction(@NonNull ActionSingle<ImageView> startAction) {
      this.startAction = startAction;
      return this;
    }

    @CheckResult @NonNull
    public final Loader setErrorAction(@NonNull ActionSingle<ImageView> errorAction) {
      this.errorAction = errorAction;
      return this;
    }

    @CheckResult @NonNull
    public final Loader setCompleteAction(@NonNull ActionSingle<ImageView> completeAction) {
      this.completeAction = completeAction;
      return this;
    }

    @CheckResult @NonNull public Loaded into(@NonNull ImageView imageView) {
      if (resource == 0) {
        throw new IllegalStateException("No resource to load");
      }

      return load(imageView, resource, tint, startAction, errorAction, completeAction);
    }

    @CheckResult @NonNull
    protected abstract Loaded load(@NonNull ImageView imageView, @DrawableRes int resource,
        @ColorRes int tint, @Nullable ActionSingle<ImageView> startAction,
        @Nullable ActionSingle<ImageView> errorAction,
        @Nullable ActionSingle<ImageView> completeAction);
  }
}
