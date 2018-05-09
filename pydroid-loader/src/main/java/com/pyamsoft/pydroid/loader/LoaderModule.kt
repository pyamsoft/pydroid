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
import androidx.annotation.CheckResult
import com.pyamsoft.pydroid.cache.Cache
import com.pyamsoft.pydroid.loader.cache.ImageCache
import com.pyamsoft.pydroid.loader.cache.ResourceImageCache

class LoaderModule(context: Context) {

  private val imageLoader: ImageLoaderImpl

  init {
    val resourceImageCache: ImageCache<Int, Drawable> = ResourceImageCache()
    imageLoader = ImageLoaderImpl(context.applicationContext, resourceImageCache)
  }

  // Singleton
  @CheckResult
  fun provideImageLoader(): ImageLoader = imageLoader

  // Singleton
  @CheckResult
  fun provideImageLoaderCache(): Cache = imageLoader
}
