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

package com.pyamsoft.pydroid.loader.resource

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.annotation.CheckResult
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import com.pyamsoft.pydroid.loader.GenericLoader
import com.pyamsoft.pydroid.loader.cache.ImageCache
import com.pyamsoft.pydroid.loader.cache.ImageCache.ImageCacheKey
import com.pyamsoft.pydroid.loader.loaded.Loaded
import com.pyamsoft.pydroid.loader.targets.DrawableImageTarget
import com.pyamsoft.pydroid.loader.targets.Target
import com.pyamsoft.pydroid.util.tintWith

/**
 * Loads Images from Resources.
 *
 * Supports Drawable resource types, is not threaded
 */
abstract class ResourceLoader protected constructor(
  context: Context,
  @param:DrawableRes private val resource: Int, @param:DrawableRes private val errorResource: Int,
  private val resourceImageCache: ImageCache<Int, Drawable>
) : GenericLoader<Drawable>() {

  @CheckResult
  private fun Int.toKey(): ImageCacheKey<Int> = ImageCacheKey(this)

  private val appContext: Context = context.applicationContext

  init {
    if (this.resource == 0) {
      throw IllegalStateException("No resource to load")
    }
  }

  final override fun into(imageView: ImageView): Loaded =
    into(DrawableImageTarget.forImageView(imageView))

  final override fun into(target: Target<Drawable>): Loaded = load(target, resource)

  @CheckResult
  protected fun loadResource(): Drawable {
    val key: ImageCacheKey<Int> = resource.toKey()
    val cached: Drawable? = resourceImageCache.retrieve(key)
    if (cached == null) {
      val result = loadFreshResource()
      resourceImageCache.cache(key, result)
      return result
    } else {
      return cached
    }
  }

  @CheckResult
  private fun loadFreshResource(): Drawable {
    val possiblyLoaded: Drawable? = AppCompatResources.getDrawable(appContext, resource)
    if (possiblyLoaded == null) {
      throw NullPointerException("AppCompatResources failed to find drawable: $resource")
    } else {
      if (tint != 0) {
        return possiblyLoaded.tintWith(appContext, tint)
      } else {
        return possiblyLoaded
      }
    }
  }

  @CheckResult
  protected fun loadErrorResource(): Drawable? {
    val key: ImageCacheKey<Int> = errorResource.toKey()
    val cached: Drawable? = resourceImageCache.retrieve(key)
    if (cached == null) {
      val result: Drawable? = loadFreshErrorResource()
      if (result != null) {
        resourceImageCache.cache(key, result)
      }
      return result
    } else {
      return cached
    }
  }

  @CheckResult
  private fun loadFreshErrorResource(): Drawable? {
    errorResource.let {
      if (it == 0) {
        return null
      } else {
        return AppCompatResources.getDrawable(appContext, it)
      }
    }
  }

  @CheckResult
  protected abstract fun load(
    target: Target<Drawable>,
    @DrawableRes resource: Int
  ): Loaded
}
