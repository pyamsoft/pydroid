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
  protected abstract fun mutateResource(resource: T): T

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

      override fun setError(error: Drawable?) {
        imageView.setImageDrawable(error)
      }

      override fun setPlaceholder(placeholder: Drawable?) {
        imageView.setImageDrawable(placeholder)
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
        target.setError(error)
        errorAction?.invoke(error)
      }

      override fun onResourceCleared(placeholder: Drawable?) {
        target.setPlaceholder(placeholder)
      }

      override fun onResourceReady(
        resource: T,
        transition: Transition<in T>?
      ) {
        val mutated = mutator?.invoke(mutateResource(resource)) ?: resource
        target.setImage(resource)
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
