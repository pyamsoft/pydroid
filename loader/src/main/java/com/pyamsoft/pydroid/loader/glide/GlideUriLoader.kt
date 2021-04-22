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

package com.pyamsoft.pydroid.loader.glide

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.RequestManager

internal class GlideUriLoader internal constructor(
    context: Context,
    private val uri: Uri
) : GlideDrawableLoader(context) {

    override fun createRequest(request: RequestManager): RequestBuilder<Drawable> {
        return request.asDrawable().load(uri)
    }

    override fun immediateResource(): Drawable? {
        return null
    }
}
