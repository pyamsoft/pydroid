/*
 * Copyright 2021 Peter Kenji Yamanaka
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

package com.pyamsoft.pydroid.ui.internal.test

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.annotation.CheckResult
import androidx.compose.runtime.Composable
import coil.ImageLoader
import coil.annotation.ExperimentalCoilApi
import coil.bitmap.BitmapPool
import coil.decode.DataSource
import coil.memory.MemoryCache
import coil.request.DefaultRequestOptions
import coil.request.Disposable
import coil.request.ImageRequest
import coil.request.ImageResult
import coil.request.SuccessResult

/** Only use for tests/previews */
private class TestImageLoader(context: Context) : ImageLoader {

  private val context = context.applicationContext
  private val loadingDrawable by lazy(LazyThreadSafetyMode.NONE) { ColorDrawable(Color.BLACK) }
  private val successDrawable by lazy(LazyThreadSafetyMode.NONE) { ColorDrawable(Color.GREEN) }

  private val disposable =
      object : Disposable {
        override val isDisposed: Boolean = true

        @ExperimentalCoilApi override suspend fun await() {}

        override fun dispose() {}
      }

  override val bitmapPool: BitmapPool = BitmapPool(0)
  override val defaults: DefaultRequestOptions = DefaultRequestOptions()
  override val memoryCache: MemoryCache =
      object : MemoryCache {
        override val maxSize: Int = 1
        override val size: Int = 0

        override fun clear() {}

        override fun get(key: MemoryCache.Key): Bitmap? {
          return null
        }

        override fun remove(key: MemoryCache.Key): Boolean {
          return false
        }

        override fun set(key: MemoryCache.Key, bitmap: Bitmap) {}
      }

  override fun enqueue(request: ImageRequest): Disposable {
    request.apply {
      target?.onStart(placeholder = loadingDrawable)
      target?.onSuccess(result = successDrawable)
    }
    return disposable
  }

  override suspend fun execute(request: ImageRequest): ImageResult {
    return SuccessResult(
        drawable = successDrawable,
        request = request,
        metadata =
            ImageResult.Metadata(
                memoryCacheKey = MemoryCache.Key(""),
                isSampled = false,
                dataSource = DataSource.MEMORY_CACHE,
                isPlaceholderMemoryCacheKeyPresent = false,
            ))
  }

  override fun newBuilder(): ImageLoader.Builder {
    return ImageLoader.Builder(context)
  }

  override fun shutdown() {}
}

/** Only use for tests/previews */
@Composable
@CheckResult
internal fun createNewTestImageLoader(context: Context): ImageLoader {
  return TestImageLoader(context)
}
