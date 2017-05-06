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
 *
 */

package com.pyamsoft.pydroid.loader.targets;

import android.graphics.drawable.Drawable;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.widget.ImageView;
import com.pyamsoft.pydroid.helper.Checker;

/**
 * Target which loads Drawables into an ImageView
 */
public class DrawableImageTarget implements Target<Drawable> {

  @NonNull private final ImageView imageView;

  private DrawableImageTarget(@NonNull ImageView imageView) {
    this.imageView = Checker.checkNonNull(imageView);
  }

  @CheckResult @NonNull public static Target<Drawable> forImageView(@NonNull ImageView imageView) {
    return new DrawableImageTarget(imageView);
  }

  @Override public void loadImage(@NonNull Drawable image) {
    imageView.setImageDrawable(image);
  }
}
