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

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import androidx.annotation.CheckResult
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.target.CustomViewTarget
import com.bumptech.glide.request.transition.Transition

abstract class GlideLoader<T : Any> protected constructor() : GenericLoader<T>() {

    @CheckResult
    protected abstract fun createRequest(request: RequestManager): RequestBuilder<T>

    @CheckResult
    protected abstract fun mutateImage(resource: T): T

    protected abstract fun setImage(
        view: ImageView,
        image: T
    )

    final override fun into(imageView: ImageView): Loaded {
        return loadTarget(object : ImageTarget<T> {
            override fun view(): View {
                return imageView
            }

            override fun clear() {
                imageView.setImageDrawable(null)
            }

            override fun setImage(image: T) {
                setImage(imageView, image)
            }
        })
    }

    final override fun into(target: ImageTarget<T>): Loaded {
        return loadTarget(target)
    }

    @CheckResult
    private fun loadTarget(target: ImageTarget<T>): Loaded {
        val loadTarget = object : CustomViewTarget<View, T>(target.view()) {
            override fun onLoadFailed(error: Drawable?) {
                target.clear()
                errorAction?.invoke()
            }

            override fun onResourceCleared(placeholder: Drawable?) {
                target.clear()
            }

            override fun onResourceReady(
                resource: T,
                transition: Transition<in T>?
            ) {
                val mutated = mutator?.invoke(mutateImage(resource)) ?: resource
                target.setImage(mutated)
                completeAction?.invoke(mutated)
            }
        }

        startAction?.invoke()
        val context = target.view()
            .context.applicationContext
        createRequest(Glide.with(context)).into(loadTarget)
        return GlideLoaded(target)
    }
}
