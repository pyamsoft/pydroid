package com.pyamsoft.pydroid.loader

import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.annotation.CheckResult
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.target.CustomViewTarget
import com.bumptech.glide.request.transition.Transition

abstract class GlideLoader<T : Any> : GenericLoader<T>() {

  @CheckResult
  protected abstract fun createRequest(request: RequestManager): RequestBuilder<T>

  @CheckResult
  protected abstract fun mutateResource(resource: T): T

  protected abstract fun setImage(
    view: ImageView,
    image: T
  )

  final override fun into(imageView: ImageView): Loaded {
    val context = imageView.context.applicationContext

    val needsCustomTarget = errorAction != null ||
        completeAction != null ||
        mutator != null

    val loadRequest = createRequest(Glide.with(context))

    startAction?.invoke()
    if (needsCustomTarget) {
      val target = object : CustomViewTarget<ImageView, T>(imageView) {
        override fun onLoadFailed(error: Drawable?) {
          errorAction?.invoke(error)
          getView().setImageDrawable(error)
        }

        override fun onResourceCleared(placeholder: Drawable?) {
          getView().setImageDrawable(placeholder)
        }

        override fun onResourceReady(
          resource: T,
          transition: Transition<in T>?
        ) {
          val mutated = mutator?.invoke(mutateResource(resource)) ?: resource
          completeAction?.invoke(mutated)
          setImage(getView(), mutated)
        }
      }

      loadRequest.into(target)
    } else {
      loadRequest.into(imageView)
    }
    return GlideLoaded(imageView)
  }

}
