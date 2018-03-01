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

package com.pyamsoft.pydroid.loader.cache

import android.graphics.drawable.Drawable
import android.support.annotation.DrawableRes
import com.pyamsoft.pydroid.cache.CacheTimeout
import com.pyamsoft.pydroid.loader.cache.ImageCache.ImageCacheKey
import timber.log.Timber

internal class ResourceImageCache internal constructor() : ImageCache<@DrawableRes Int, Drawable> {

  private val cacheTimeout = CacheTimeout(this)
  private val cache: MutableMap<Int, Drawable> = LinkedHashMap()

  init {
    Timber.d("New ${this::class.java.simpleName}")
  }

  override fun clearCache() {
    cache.clear()
    cacheTimeout.reset()
  }

  override fun cache(
    key: ImageCacheKey<Int>,
    entry: Drawable
  ) {
    cache[key.data] = entry
    cacheTimeout.queue()
  }

  override fun retrieve(key: ImageCacheKey<Int>): Drawable? = cache[key.data].also {
    cacheTimeout.queue()
  }
}
