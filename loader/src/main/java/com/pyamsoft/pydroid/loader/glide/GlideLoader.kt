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
import android.widget.ImageView
import androidx.annotation.CheckResult
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.CustomViewTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.pyamsoft.pydroid.loader.GenericLoader
import com.pyamsoft.pydroid.loader.ImageTarget
import com.pyamsoft.pydroid.loader.Loaded

internal abstract class GlideLoader<T : Any> protected constructor(
    protected val context: Context
) : GenericLoader<T>() {

    @CheckResult
    protected abstract fun createRequest(request: RequestManager): RequestBuilder<T>

    final override fun into(imageView: ImageView): Loaded {
        return glideLoad(object : CustomViewTarget<ImageView, T>(imageView) {

            override fun onResourceCleared(placeholder: Drawable?) {
                imageView.setImageDrawable(null)
            }

            override fun onLoadFailed(errorDrawable: Drawable?) {
                imageView.setImageDrawable(null)
                notifyError()
            }

            override fun onResourceLoading(placeholder: Drawable?) {
                notifyLoading()
            }

            override fun onResourceReady(resource: T, transition: Transition<in T>?) {
                val mutated: T = executeMutator(mutateImage(resource))
                setImage(imageView, mutated)
                notifySuccess(mutated)
            }
        })
    }

    final override fun into(target: ImageTarget<T>): Loaded {
        return glideLoad(object : CustomTarget<T>() {

            override fun onLoadCleared(placeholder: Drawable?) {
                target.clear()
            }

            override fun onLoadFailed(errorDrawable: Drawable?) {
                target.clear()
                notifyError()
            }

            override fun onLoadStarted(placeholder: Drawable?) {
                notifyLoading()
            }

            override fun onResourceReady(resource: T, transition: Transition<in T>?) {
                val mutated: T = executeMutator(mutateImage(resource))
                target.setImage(mutated)
                notifySuccess(mutated)
            }
        })
    }

    @CheckResult
    private fun glideLoad(target: Target<T>): Loaded {
        val manager = Glide.with(context.applicationContext)
        createRequest(manager).into(target)
        return GlideLoaded(manager, target)
    }
}
