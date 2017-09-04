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
 */

package com.pyamsoft.pydroid.loader.resource

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.annotation.CheckResult
import android.support.annotation.DrawableRes
import android.support.v7.content.res.AppCompatResources
import android.widget.ImageView
import com.pyamsoft.pydroid.loader.GenericLoader
import com.pyamsoft.pydroid.loader.loaded.Loaded
import com.pyamsoft.pydroid.loader.targets.DrawableImageTarget
import com.pyamsoft.pydroid.loader.targets.Target
import com.pyamsoft.pydroid.util.DrawableUtil

/**
 * Loads Images from Resources.
 *
 * Supports Drawable resource types, is not threaded
 */
abstract class ResourceLoader protected constructor(context: Context,
    @param:DrawableRes private val resource: Int) : GenericLoader<Drawable>() {

  private val appContext: Context = context.applicationContext

  init {
    if (this.resource == 0) {
      throw IllegalStateException("No resource to load")
    }
  }

  final override fun into(imageView: ImageView): Loaded =
      into(DrawableImageTarget.forImageView(imageView))

  final override fun into(target: Target<Drawable>): Loaded = load(target, resource)

  @CheckResult protected fun loadResource(): Drawable {
    val possiblyLoaded: Drawable? = AppCompatResources.getDrawable(appContext, resource)
    if (possiblyLoaded == null) {
      throw NullPointerException("Could not load drawable for resource: " + resource)
    } else {
      return if (tint != 0) {
        // Return
        DrawableUtil.tintDrawableFromRes(appContext, possiblyLoaded, tint)
      } else {
        // Return
        possiblyLoaded
      }
    }
  }

  @CheckResult protected abstract fun load(target: Target<Drawable>,
      @DrawableRes resource: Int): Loaded
}
