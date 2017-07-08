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
import android.support.annotation.ColorRes
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

 * Supports Drawable resource types
 */
abstract class ResourceLoader protected constructor(context: Context,
    @param:DrawableRes private val resource: Int) : GenericLoader<ResourceLoader, Drawable>() {

  protected val appContext: Context = context.applicationContext

  init {
    if (this.resource == 0) {
      throw IllegalStateException("No resource to load")
    }
  }

  final override fun tint(@ColorRes color: Int): ResourceLoader {
    this.tint = color
    return this
  }

  final override fun withStartAction(startAction: (Target<Drawable>) -> Unit): ResourceLoader {
    this.startAction = startAction
    return this
  }

  final override fun withErrorAction(errorAction: (Target<Drawable>) -> Unit): ResourceLoader {
    this.errorAction = errorAction
    return this
  }

  final override fun withCompleteAction(
      completeAction: (Target<Drawable>) -> Unit): ResourceLoader {
    this.completeAction = completeAction
    return this
  }

  override fun into(imageView: ImageView): Loaded {
    return into(DrawableImageTarget.forImageView(imageView))
  }

  override fun into(target: Target<Drawable>): Loaded {
    return load(target, resource)
  }

  @CheckResult protected fun loadResource(context: Context): Drawable {
    val possiblyLoaded: Drawable? = AppCompatResources.getDrawable(context, resource)
    if (possiblyLoaded == null) {
      throw NullPointerException("Could not load drawable for resource: " + resource)
    } else {
      if (tint != 0) {
        return DrawableUtil.tintDrawableFromRes(context, possiblyLoaded, tint)
      } else {
        return possiblyLoaded
      }
    }
  }

  @CheckResult protected abstract fun load(target: Target<Drawable>,
      @DrawableRes resource: Int): Loaded
}
