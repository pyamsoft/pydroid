package com.pyamsoft.pydroid.loader

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomViewTarget
import com.bumptech.glide.request.transition.Transition
import timber.log.Timber

internal class GlideLoader(
  private val context: Context,
  @DrawableRes private val resource: Int
) : GenericLoader<Drawable>() {

  override fun into(imageView: ImageView): Loaded {

    val needsCustomTarget = errorAction != null ||
        completeAction != null ||
        mutator != null

    val loadRequest = Glide.with(context)
        .load(resource)

    startAction?.invoke()
    if (needsCustomTarget) {
      val target = object : CustomViewTarget<ImageView, Drawable>(imageView) {
        override fun onLoadFailed(errorDrawable: Drawable?) {
          Timber.w("Error occured when loading $resource into $imageView")
          errorAction?.invoke(errorDrawable)
          getView().setImageDrawable(errorDrawable)
        }

        override fun onResourceCleared(placeholder: Drawable?) {
          getView().setImageDrawable(placeholder)
        }

        override fun onResourceReady(
          resource: Drawable,
          transition: Transition<in Drawable>?
        ) {
          val mutated = mutator?.invoke(resource.mutate()) ?: resource
          completeAction?.invoke(mutated)
          getView().setImageDrawable(mutated)
        }
      }

      loadRequest.into(target)
    } else {
      loadRequest.into(imageView)
    }
    return GlideLoaded(context.applicationContext, imageView)
  }

}
