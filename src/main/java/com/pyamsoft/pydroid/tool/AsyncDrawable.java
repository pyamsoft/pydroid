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

package com.pyamsoft.pydroid.tool;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.CheckResult;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v7.content.res.AppCompatResources;
import android.widget.ImageView;
import com.pyamsoft.pydroid.util.DrawableUtil;
import timber.log.Timber;

public final class AsyncDrawable {

  private AsyncDrawable() {
  }

  @CheckResult @NonNull public static Loader load(@DrawableRes int drawableRes) {
    return load(drawableRes, new DefaultLoader());
  }

  @CheckResult @NonNull
  public static Loader load(@DrawableRes int drawableRes, @NonNull Loader loader) {
    loader.setResource(drawableRes);
    return loader;
  }

  @SuppressWarnings("WeakerAccess") public static final class DefaultLoader
      extends Loader<AsyncDrawableTaskEntry> {

    @NonNull @Override
    public AsyncDrawableTaskEntry load(@NonNull ImageView imageView, @DrawableRes int resource,
        @ColorRes int tint) {
      final AsyncDrawableTaskEntry<Drawable> taskEntry = new AsyncDrawableTaskEntry<Drawable>() {
        @Override protected Drawable doInBackground(Context... params) {
          if (params == null) {
            Timber.e("No context passed to AsyncDrawable loader");
            return null;
          }

          final Context context = params[0];
          if (context == null) {
            Timber.e("ImageView context is NULL");
            return null;
          }

          Timber.d("Load drawable in background");
          Drawable loaded = AppCompatResources.getDrawable(context, resource);
          if (loaded == null) {
            Timber.e("Could not load drawable for resource: %d", resource);
            return null;
          }

          if (tint != 0) {
            loaded = DrawableUtil.tintDrawableFromRes(context, loaded, tint);
          }

          return loaded;
        }

        @Override protected void onPostExecute(Drawable drawable) {
          super.onPostExecute(drawable);
          if (drawable != null) {
            Timber.d("Load drawable into image");
            imageView.setImageDrawable(drawable);
          }
        }
      };

      // Execute it
      AsyncTaskCompat.executeParallel(taskEntry, imageView.getContext());
      return taskEntry;
    }
  }

  /**
   * A map that makes it convenient to load AsyncDrawables
   */
  public static final class Mapper extends AsyncMap<AsyncMap.Entry> {

  }

  public static abstract class Loader<T extends AsyncMap.Entry> {

    @DrawableRes private int resource;
    @ColorRes private int tint;

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

    @CheckResult @NonNull public T into(@NonNull ImageView imageView) {
      return load(imageView, resource, tint);
    }

    @CheckResult @NonNull
    protected abstract T load(@NonNull ImageView imageView, @DrawableRes int resource,
        @ColorRes int tint);
  }
}
