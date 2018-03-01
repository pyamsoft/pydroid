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

package com.pyamsoft.pydroid.loader

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.annotation.DrawableRes
import com.pyamsoft.pydroid.cache.Cache
import com.pyamsoft.pydroid.loader.cache.ImageCache
import com.pyamsoft.pydroid.loader.resource.ResourceLoader
import com.pyamsoft.pydroid.loader.resource.RxResourceLoader
import io.reactivex.Scheduler

internal class ImageLoaderImpl internal constructor(
  private val context: Context,
  private val resourceImageCache: ImageCache<Int, Drawable>,
  private val mainThreadScheduler: Scheduler,
  private val ioScheduler: Scheduler
) : ImageLoader, Cache {

  override fun clearCache() {
    resourceImageCache.clearCache()
  }

  override fun fromResource(@DrawableRes resource: Int): ResourceLoader = fromResource(
      resource,
      0
  )

  override fun fromResource(@DrawableRes resource: Int, @DrawableRes errorResource: Int): ResourceLoader =
    RxResourceLoader(
        context.applicationContext, resource, errorResource, resourceImageCache,
        mainThreadScheduler, ioScheduler
    )
}
