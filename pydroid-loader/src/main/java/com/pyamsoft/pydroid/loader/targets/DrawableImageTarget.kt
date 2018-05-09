/*
 * Copyright (C) 2018 Peter Kenji Yamanaka
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pyamsoft.pydroid.loader.targets

import android.graphics.drawable.Drawable
import androidx.annotation.CheckResult
import android.widget.ImageView

/**
 * Target which loads Drawables into an ImageView
 */
class DrawableImageTarget private constructor(private val imageView: ImageView) : Target<Drawable> {

  override fun loadImage(image: Drawable) {
    imageView.setImageDrawable(image)
  }

  override fun loadError(error: Drawable?) {
    if (error != null) {
      imageView.setImageDrawable(error)
    }
  }

  companion object {

    @CheckResult
    @JvmStatic
    fun forImageView(imageView: ImageView): Target<Drawable> =
      DrawableImageTarget(imageView)
  }
}
