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
import com.pyamsoft.pydroid.loader.glide.GlideByteArrayLoader
import com.pyamsoft.pydroid.loader.glide.GlideLocalBitmapLoader
import com.pyamsoft.pydroid.loader.glide.GlideLocalDrawableLoader
import com.pyamsoft.pydroid.loader.glide.GlideUriLoader
import com.pyamsoft.pydroid.loader.glide.GlideUrlLoader

internal class ImageLoaderImpl internal constructor(
    private val context: Context
) : ImageLoader {

    override fun load(@DrawableRes resource: Int): Loader<Drawable> {
        return GlideLocalDrawableLoader(context.applicationContext, resource)
    }

    override fun load(url: String): Loader<Drawable> {
        return GlideUrlLoader(context.applicationContext, url)
    }

    override fun load(uri: Uri): Loader<Drawable> {
        return GlideUriLoader(context.applicationContext, uri)
    }

    override fun load(data: ByteArray): Loader<Bitmap> {
        return GlideByteArrayLoader(context.applicationContext, data)
    }

    override fun load(bitmap: Bitmap): Loader<Bitmap> {
        return GlideLocalBitmapLoader(context.applicationContext, bitmap)
    }
}
