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
import android.os.AsyncTask;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.widget.ImageView;
import com.pyamsoft.pydroid.model.AsyncDrawable;
import com.pyamsoft.pydroid.util.DrawableUtil;
import java.lang.ref.WeakReference;
import timber.log.Timber;

public final class AsyncDrawableTask extends AsyncTask<AsyncDrawable, Void, Drawable> {

  private final WeakReference<ImageView> weakImage;
  private final WeakReference<TabLayout.Tab> weakTab;
  private final int color;

  public AsyncDrawableTask(final @NonNull ImageView source) {
    this(source, 0);
  }

  public AsyncDrawableTask(final @NonNull ImageView source, final int c) {
    weakImage = new WeakReference<>(source);
    weakTab = null;
    color = c;
  }

  public AsyncDrawableTask(final @NonNull TabLayout.Tab source) {
    this(source, 0);
  }

  public AsyncDrawableTask(final @NonNull TabLayout.Tab source, final int c) {
    weakTab = new WeakReference<>(source);
    weakImage = null;
    color = c;
  }

  @Nullable @CheckResult @Override
  protected Drawable doInBackground(@Nullable AsyncDrawable... asyncDrawables) {
    Timber.d("doInBackground");
    if (asyncDrawables == null || asyncDrawables.length < 1) {
      Timber.e("AsyncVectorDrawable parameter not provided");
      return null;
    }

    final AsyncDrawable asyncDrawable = asyncDrawables[0];
    if (asyncDrawable == null) {
      Timber.e("AsyncVectorDrawable is NULL");
      return null;
    }

    final Context context = asyncDrawable.context();
    final int icon = asyncDrawable.icon();
    Timber.d("Load vector drawable compat for resource: %d", icon);
    Drawable drawable = ContextCompat.getDrawable(context.getApplicationContext(), icon);
    if (color != 0) {
      drawable = DrawableUtil.tintDrawableFromRes(context, drawable, color);
    }
    return drawable;
  }

  @Override protected void onPostExecute(@Nullable Drawable drawable) {
    super.onPostExecute(drawable);
    if (drawable == null) {
      Timber.e("doInBackground failed.");
      return;
    }

    Timber.d("onPostExecute");
    if (!loadImage(drawable)) {
      loadTab(drawable);
    }
  }

  private boolean loadImage(@NonNull Drawable drawable) {
    if (weakImage == null) {
      return false;
    } else {
      Timber.d("loadImage");
      final ImageView imageView = weakImage.get();
      if (imageView == null) {
        Timber.e("Image is NULL");
        return false;
      }

      Timber.d("Load drawable into ImageView");
      imageView.setImageDrawable(drawable);
      return true;
    }
  }

  private void loadTab(@NonNull Drawable drawable) {
    if (weakTab != null) {
      Timber.d("loadTab");
      final TabLayout.Tab tab = weakTab.get();
      if (tab == null) {
        Timber.e("Tab is NULL");
        return;
      }

      Timber.d("Load drawable into Tab");
      tab.setIcon(drawable);
    }
  }

  @Override protected void onCancelled(@NonNull Drawable drawable) {
    super.onCancelled(drawable);
    Timber.e("Vector loading task cancelled");
    if (weakImage != null) {
      Timber.d("Clear weakImage");
      weakImage.clear();
    }
    if (weakTab != null) {
      Timber.d("Clear weakTab");
      weakTab.clear();
    }
  }
}
