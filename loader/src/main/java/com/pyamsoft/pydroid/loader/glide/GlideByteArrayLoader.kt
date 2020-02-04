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
 *
 */

package com.pyamsoft.pydroid.loader.glide

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.ImageView
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.RequestManager

internal class GlideByteArrayLoader internal constructor(
    context: Context,
    private val data: ByteArray
) : GlideLoader<Bitmap>(context) {

    override fun createRequest(request: RequestManager): RequestBuilder<Bitmap> {
        return request
            .asBitmap()
            .load(data)
    }

    override fun mutateImage(resource: Bitmap): Bitmap {
        return resource.copy(resource.config, true)
    }

    override fun setImage(view: ImageView, image: Bitmap) {
        view.setImageBitmap(image)
    }

    override fun immediateResource(): Bitmap? {
        return BitmapFactory.decodeByteArray(data, 0, data.size)
    }
}
