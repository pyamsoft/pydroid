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
import android.os.AsyncTask;
import android.support.annotation.CheckResult;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v7.content.res.AppCompatResources;
import android.widget.ImageView;
import java.util.HashMap;
import java.util.Map;
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
    return new Loader(appContext, drawableRes);
  }

  public static final class Loader {

    @NonNull final Context appContext;
    @DrawableRes final int resource;
    @ColorRes int tint;

    Loader(@NonNull Context context, int resource) {
      this.appContext = context.getApplicationContext();
      this.resource = resource;
      tint = 0;
    }

    @CheckResult @NonNull public final Loader tint(@ColorRes int color) {
      this.tint = color;
      return this;
    }

    @CheckResult @NonNull public final AsyncTask into(@NonNull ImageView imageView) {
      return AsyncTaskCompat.executeParallel(new AsyncTask<Void, Void, Drawable>() {
        @Override protected Drawable doInBackground(Void... params) {
          Drawable loaded = AppCompatResources.getDrawable(appContext, resource);
          if (loaded == null) {
            Timber.e("Could not load drawable for resource: %d", resource);
            return null;
          }

          if (tint != 0) {
            loaded = DrawableUtil.tintDrawableFromRes(appContext, loaded, tint);
          }

          return loaded;
        }

        @Override protected void onPostExecute(Drawable drawable) {
          super.onPostExecute(drawable);
          if (drawable != null) {
            imageView.setImageDrawable(drawable);
          }
        }
      });
    }
  }

  /**
   * A map that makes it convenient to load AsyncDrawables
   */
  public static final class Mapper {

    @NonNull private final HashMap<String, AsyncTask> map;

    public Mapper() {
      this.map = new HashMap<>();
    }

    /**
     * Puts a new element into the map
     *
     * If an old element exists, its task is cancelled first before adding the new one
     */
    public final void put(@NonNull String tag, @NonNull AsyncTask subscription) {
      if (map.containsKey(tag)) {
        final AsyncTask old = map.get(tag);
        cancelSubscription(tag, old);
      }

      Timber.d("Insert new subscription for tag: %s", tag);
      map.put(tag, subscription);
    }

    /**
     * Clear all elements in the map
     *
     * If the elements have not been cancelled yet, cancel them before removing them
     */
    public final void clear() {
      for (final Map.Entry<String, AsyncTask> entry : map.entrySet()) {
        cancelSubscription(entry.getKey(), entry.getValue());
      }

      Timber.d("Clear AsyncDrawableMap");
      map.clear();
    }

    /**
     * Cancels a task
     */
    private void cancelSubscription(@NonNull String tag, @Nullable AsyncTask subscription) {
      if (subscription != null) {
        if (!subscription.isCancelled()) {
          Timber.d("Cancel for tag: %s", tag);
          subscription.cancel(true);
        }
      }
    }
  }
}
