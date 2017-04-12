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

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.CheckResult;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.widget.ImageView;
import com.pyamsoft.pydroid.function.ActionSingle;
import com.pyamsoft.pydroid.helper.Checker;
import com.pyamsoft.pydroid.ui.loader.rx.RxPathLoader;
import com.pyamsoft.pydroid.ui.loader.targets.BitmapImageTarget;
import com.pyamsoft.pydroid.ui.loader.targets.Target;

/**
 * Loads Images from Paths.
 *
 * Supports Bitmap resource types
 */
public final class PathLoader {

  private PathLoader() {
  }

  @CheckResult @NonNull public static Loader load(@NonNull String path) {
    return load(path, new RxPathLoader());
  }

  @SuppressWarnings("WeakerAccess") @CheckResult @NonNull
  public static Loader load(@NonNull String path, @NonNull Loader loader) {
    loader = Checker.checkNonNull(loader);
    loader.setPath(path);
    return loader;
  }

  public static abstract class Loader extends GenericLoader<Bitmap> {

    private static final int DEFAULT_HEIGHT = 40;
    private static final int DEFAULT_WIDTH = 40;
    @NonNull private String path = "";
    private int reqWidth = 0;
    private int reqHeight = 0;

    @CheckResult
    private static int calculateInSampleSize(@NonNull final BitmapFactory.Options options,
        final int reqWidth, final int reqHeight) {
      // Raw height and width of image
      final int height = options.outHeight;
      final int width = options.outWidth;
      int inSampleSize = 1;

      if (height > reqHeight || width > reqWidth) {

        final int halfHeight = height / 2;
        final int halfWidth = width / 2;

        // Calculate the largest inSampleSize value that is a power of 2 and keeps both
        // height and width larger than the requested height and width.
        while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
          inSampleSize <<= 1;
        }
      }

      return inSampleSize;
    }

    void setPath(@NonNull String path) {
      this.path = Checker.checkNonNull(path);
    }

    @NonNull @Override public Loader tint(@ColorRes int color) {
      super.tint(color);
      return this;
    }

    @NonNull @Override
    public Loader setCompleteAction(@NonNull ActionSingle<Target<Bitmap>> completeAction) {
      super.setCompleteAction(completeAction);
      return this;
    }

    @NonNull @Override
    public Loader setErrorAction(@NonNull ActionSingle<Target<Bitmap>> errorAction) {
      super.setErrorAction(errorAction);
      return this;
    }

    @NonNull @Override
    public GenericLoader setStartAction(@NonNull ActionSingle<Target<Bitmap>> startAction) {
      super.setStartAction(startAction);
      return this;
    }

    @CheckResult @NonNull public Loader setReqHeight(int reqHeight) {
      this.reqHeight = reqHeight;
      return this;
    }

    @CheckResult @NonNull public Loader setReqWidth(int reqWidth) {
      this.reqWidth = reqWidth;
      return this;
    }

    @CheckResult @NonNull public Loaded into(@NonNull ImageView imageView) {
      return into(BitmapImageTarget.forImageView(imageView));
    }

    @CheckResult @NonNull public Loaded into(@NonNull Target<Bitmap> target) {
      if (path.isEmpty()) {
        throw new IllegalStateException("No path to load");
      }

      return load(target, path);
    }

    @CheckResult @NonNull protected Bitmap loadPath() {
      // First decode with inJustDecodeBounds=true to check dimensions
      final BitmapFactory.Options options = new BitmapFactory.Options();
      options.inJustDecodeBounds = true;
      BitmapFactory.decodeFile(path, options);

      // Figure out the correct width
      final int width;
      if (reqWidth > 0) {
        width = reqWidth;
      } else {
        width = DEFAULT_WIDTH;
      }

      // Figure out the correct height
      final int height;
      if (reqHeight > 0) {
        height = reqHeight;
      } else {
        height = DEFAULT_HEIGHT;
      }

      // Calculate inSampleSize
      options.inSampleSize = calculateInSampleSize(options, width, height);

      // Decode bitmap with inSampleSize set
      options.inJustDecodeBounds = false;
      return BitmapFactory.decodeFile(path, options);
    }

    @CheckResult @NonNull
    protected abstract Loaded load(@NonNull Target<Bitmap> target, @NonNull String path);
  }
}
