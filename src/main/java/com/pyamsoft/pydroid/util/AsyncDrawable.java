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

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.CheckResult;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v7.content.res.AppCompatResources;
import android.widget.ImageView;
import com.pyamsoft.pydroid.tool.AsyncMap;
import com.pyamsoft.pydroid.tool.AsyncTaskMap;
import timber.log.Timber;

public final class AsyncDrawable {

  @NonNull private final Context appContext;

  private AsyncDrawable(@NonNull Context context) {
    this.appContext = context.getApplicationContext();
  }

  @CheckResult @NonNull public static AsyncDrawable with(@NonNull Context context) {
    return new AsyncDrawable(context.getApplicationContext());
  }

  @CheckResult @NonNull public final Loader load(@DrawableRes int drawableRes) {
    return load(drawableRes, new DefaultLoader());
  }

  @CheckResult @NonNull
  public final Loader load(@DrawableRes int drawableRes, @NonNull Loader loader) {
    loader.setContext(appContext);
    loader.setResource(drawableRes);
    return loader;
  }

  @SuppressWarnings("WeakerAccess") public static final class DefaultLoader
      extends Loader<AsyncTaskMap.TaskEntry> {

    @NonNull @Override
    public AsyncTaskMap.TaskEntry load(@NonNull Context context, @NonNull ImageView imageView,
        @DrawableRes int resource, @ColorRes int tint) {
      final AsyncTaskMap.TaskEntry<Void, Drawable> taskEntry =
          new AsyncTaskMap.TaskEntry<Void, Drawable>(imageView::setImageDrawable) {
            @Override protected Drawable doInBackground(Void... params) {
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
          };

      // Execute it
      AsyncTaskCompat.executeParallel(taskEntry);
      return taskEntry;
    }
  }

  /**
   * A map that makes it convenient to load AsyncDrawables
   */
  public static final class Mapper extends AsyncMap<AsyncMap.Entry> {

  }

  public static abstract class Loader<T extends AsyncMap.Entry> {

    private Context appContext;
    @DrawableRes private int resource;
    @ColorRes private int tint;

    protected Loader() {
      tint = 0;
    }

    void setContext(@NonNull Context context) {
      this.appContext = context.getApplicationContext();
    }

    void setResource(@DrawableRes int resource) {
      this.resource = resource;
    }

    @CheckResult @NonNull public final Loader tint(@ColorRes int color) {
      this.tint = color;
      return this;
    }

    @CheckResult @NonNull public T into(@NonNull ImageView imageView) {
      return load(appContext, imageView, resource, tint);
    }

    @CheckResult @NonNull
    protected abstract T load(@NonNull Context context, @NonNull ImageView imageView,
        @DrawableRes int resource, @ColorRes int tint);
  }
}
