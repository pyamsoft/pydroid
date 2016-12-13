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

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.annotation.CheckResult;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v7.content.res.AppCompatResources;
import android.widget.ImageView;
import com.pyamsoft.pydroid.util.DrawableUtil;
import timber.log.Timber;

public final class AsyncDrawable {

  @NonNull private final Activity activity;

  private AsyncDrawable(@NonNull Activity activity) {
    this.activity = activity;
  }

  @CheckResult @NonNull public static AsyncDrawable with(@NonNull Activity activity) {
    return new AsyncDrawable(activity);
  }

  @CheckResult @NonNull public Loader load(@DrawableRes int drawableRes) {
    return load(drawableRes, new DefaultLoader());
  }

  @CheckResult @NonNull public Loader load(@DrawableRes int drawableRes, @NonNull Loader loader) {
    loader.setActivity(activity);
    loader.setResource(drawableRes);
    return loader;
  }

  @SuppressWarnings("WeakerAccess") public static final class DefaultLoader
      extends Loader<AsyncDrawableTaskEntry> {

    @NonNull @Override
    public AsyncDrawableTaskEntry load(@NonNull Activity activity, @NonNull ImageView imageView,
        @DrawableRes int resource, @ColorRes int tint) {
      final AsyncDrawableTaskEntry<Drawable> taskEntry = new AsyncDrawableTaskEntry<Drawable>() {
        @Override protected Drawable doInBackground(Activity... params) {
          if (params == null) {
            Timber.e("No Activity passed to AsyncDrawable loader");
            return null;
          }

          final Activity activity1 = params[0];
          if (activity1 == null) {
            Timber.e("Activity is NULL");
            return null;
          }

          Timber.d("Load drawable in background");
          Drawable loaded = AppCompatResources.getDrawable(activity1, resource);
          if (loaded == null) {
            Timber.e("Could not load drawable for resource: %d", resource);
            return null;
          }

          if (tint != 0) {
            loaded = DrawableUtil.tintDrawableFromRes(activity1, loaded, tint);
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
      AsyncTaskCompat.executeParallel(taskEntry, activity);
      return taskEntry;
    }
  }

  /**
   * A map that makes it convenient to load AsyncDrawables
   */
  public static final class Mapper extends AsyncMap<AsyncMap.Entry> {

  }

  public static abstract class Loader<T extends AsyncMap.Entry> {

    @Nullable private Activity activity;
    @DrawableRes private int resource;
    @ColorRes private int tint;

    protected Loader() {
      tint = 0;
    }

    void setActivity(@NonNull Activity activity) {
      this.activity = activity;
    }

    void setResource(@DrawableRes int resource) {
      this.resource = resource;
    }

    @CheckResult @NonNull public final Loader tint(@ColorRes int color) {
      this.tint = color;
      return this;
    }

    @CheckResult @NonNull public T into(@NonNull ImageView imageView) {
      if (activity == null) {
        throw new IllegalStateException("Activity is NULL");
      }
      if (resource == 0) {
        throw new IllegalStateException("No resource to load");
      }

      return load(activity, imageView, resource, tint);
    }

    @CheckResult @NonNull
    protected abstract T load(@NonNull Activity activity, @NonNull ImageView imageView,
        @DrawableRes int resource, @ColorRes int tint);
  }
}
