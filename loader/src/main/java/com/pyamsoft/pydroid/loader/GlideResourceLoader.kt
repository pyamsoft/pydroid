/*
 * Copyright 2019 Peter Kenji Yamanaka
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

package com.pyamsoft.pydroid.loader

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.RequestManager

internal class GlideResourceLoader internal constructor(
    private val context: Context,
    @DrawableRes private val resId: Int
) : GlideLoader<Drawable>() {

    override fun createRequest(request: RequestManager): RequestBuilder<Drawable> {
        return request
            .asDrawable()
            .load(resId)
    }

    override fun mutateImage(resource: Drawable): Drawable {
        return resource.mutate()
    }

    override fun setImage(
        view: ImageView,
        image: Drawable
    ) {
        view.setImageDrawable(image)
    }

    override fun immediate(): Drawable {
        return requireNotNull(AppCompatResources.getDrawable(context, resId))
    }
}
