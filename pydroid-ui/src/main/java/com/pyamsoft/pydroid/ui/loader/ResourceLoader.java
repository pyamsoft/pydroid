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

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.CheckResult;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v7.content.res.AppCompatResources;
import android.widget.ImageView;
import com.pyamsoft.pydroid.function.ActionSingle;
import com.pyamsoft.pydroid.helper.Checker;
import com.pyamsoft.pydroid.ui.loader.rx.RxResourceLoader;
import com.pyamsoft.pydroid.ui.loader.targets.DrawableImageTarget;
import com.pyamsoft.pydroid.ui.loader.targets.Target;
import com.pyamsoft.pydroid.util.DrawableUtil;

/**
 * Loads Images from Resources.
 *
 * Supports Drawable resource types
 */
public final class ResourceLoader {

  private ResourceLoader() {
  }

  @CheckResult @NonNull public static Loader load(@DrawableRes int drawableRes) {
    return load(drawableRes, new RxResourceLoader());
  }

  @SuppressWarnings("WeakerAccess") @CheckResult @NonNull
  public static Loader load(@DrawableRes int drawableRes, @NonNull Loader loader) {
    loader = Checker.checkNonNull(loader);
    loader.setResource(drawableRes);
    return loader;
  }

  public static abstract class Loader extends GenericLoader<Drawable> {

    @DrawableRes private int resource;

    void setResource(@DrawableRes int resource) {
      this.resource = resource;
    }

    @NonNull @Override public Loader tint(@ColorRes int color) {
      super.tint(color);
      return this;
    }

    @NonNull @Override
    public Loader setCompleteAction(@NonNull ActionSingle<Target<Drawable>> completeAction) {
      super.setCompleteAction(completeAction);
      return this;
    }

    @NonNull @Override
    public Loader setErrorAction(@NonNull ActionSingle<Target<Drawable>> errorAction) {
      super.setErrorAction(errorAction);
      return this;
    }

    @NonNull @Override
    public GenericLoader setStartAction(@NonNull ActionSingle<Target<Drawable>> startAction) {
      super.setStartAction(startAction);
      return this;
    }

    @CheckResult @NonNull public Loaded into(@NonNull ImageView imageView) {
      return into(DrawableImageTarget.forImageView(imageView));
    }

    @CheckResult @NonNull public Loaded into(@NonNull Target<Drawable> target) {
      if (resource == 0) {
        throw new IllegalStateException("No resource to load");
      }

      return load(target, resource);
    }

    @CheckResult @NonNull protected Drawable loadResource(@NonNull Context context) {
      context = Checker.checkNonNull(context);
      Drawable loaded = AppCompatResources.getDrawable(context, resource);
      if (loaded == null) {
        throw new NullPointerException("Could not load drawable for resource: " + resource);
      }

      int tint = tint();
      if (tint != 0) {
        loaded = DrawableUtil.tintDrawableFromRes(context, loaded, tint);
      }
      return loaded;
    }

    @CheckResult @NonNull
    protected abstract Loaded load(@NonNull Target<Drawable> target, @DrawableRes int resource);
  }
}
