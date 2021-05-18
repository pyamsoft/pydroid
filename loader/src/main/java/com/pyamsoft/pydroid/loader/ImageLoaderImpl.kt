/*
 * Copyright 2020 Peter Kenji Yamanaka
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

package com.pyamsoft.pydroid.loader

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.annotation.DrawableRes
import com.pyamsoft.pydroid.loader.glide.loader.GlideByteArrayBitmapLoader
import com.pyamsoft.pydroid.loader.glide.loader.GlideByteArrayDrawableLoader
import com.pyamsoft.pydroid.loader.glide.loader.GlideLocalBitmapLoader
import com.pyamsoft.pydroid.loader.glide.loader.GlideLocalDrawableLoader
import com.pyamsoft.pydroid.loader.glide.loader.GlideUriBitmapLoader
import com.pyamsoft.pydroid.loader.glide.loader.GlideUriDrawableLoader
import com.pyamsoft.pydroid.loader.glide.loader.GlideUrlBitmapLoader
import com.pyamsoft.pydroid.loader.glide.loader.GlideUrlDrawableLoader

internal class ImageLoaderImpl internal constructor(private val context: Context) : ImageLoader {

  private val drawables by lazy {
    object : ImageLoaderApi<Drawable> {

      override fun load(@DrawableRes resource: Int): Loader<Drawable> {
        return GlideLocalDrawableLoader(context.applicationContext, resource)
      }

      override fun load(url: String): Loader<Drawable> {
        return GlideUrlDrawableLoader(context.applicationContext, url)
      }

      override fun load(uri: Uri): Loader<Drawable> {
        return GlideUriDrawableLoader(context.applicationContext, uri)
      }

      override fun load(data: ByteArray): Loader<Drawable> {
        return GlideByteArrayDrawableLoader(context.applicationContext, data)
      }

      @Suppress("OverridingDeprecatedMember")
      override fun load(bitmap: Bitmap): Loader<Drawable> {
        // Full name to avoid deprecation warning
        @Suppress("DEPRECATION")
        return com.pyamsoft.pydroid.loader.glide.loader.GlideBitmapDrawableLoader(
            context.applicationContext, bitmap)
      }
    }
  }

  private val bitmaps by lazy {
    object : ImageLoaderApi<Bitmap> {
      override fun load(resource: Int): Loader<Bitmap> {
        return GlideLocalBitmapLoader(context.applicationContext, resource)
      }

      override fun load(uri: Uri): Loader<Bitmap> {
        return GlideUriBitmapLoader(context.applicationContext, uri)
      }

      override fun load(url: String): Loader<Bitmap> {
        return GlideUrlBitmapLoader(context.applicationContext, url)
      }

      override fun load(data: ByteArray): Loader<Bitmap> {
        return GlideByteArrayBitmapLoader(context.applicationContext, data)
      }

      @Suppress("OverridingDeprecatedMember")
      override fun load(bitmap: Bitmap): Loader<Bitmap> {
        // Full name to avoid deprecation warning
        @Suppress("DEPRECATION")
        return com.pyamsoft.pydroid.loader.glide.loader.GlideBitmapBitmapLoader(
            context.applicationContext, bitmap)
      }
    }
  }

  override fun load(@DrawableRes resource: Int): Loader<Drawable> {
    return drawables.load(resource)
  }

  override fun load(url: String): Loader<Drawable> {
    return drawables.load(url)
  }

  override fun load(uri: Uri): Loader<Drawable> {
    return drawables.load(uri)
  }

  override fun load(data: ByteArray): Loader<Drawable> {
    return drawables.load(data)
  }

  @Suppress("OverridingDeprecatedMember")
  override fun load(bitmap: Bitmap): Loader<Drawable> {
    @Suppress("DEPRECATION") return drawables.load(bitmap)
  }

  override fun asDrawable(): ImageLoaderApi<Drawable> {
    return drawables
  }

  override fun asBitmap(): ImageLoaderApi<Bitmap> {
    return bitmaps
  }
}
