package com.pyamsoft.pydroid.loader

import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.RequestManager

internal class GlideResourceLoader(
  @DrawableRes private val resId: Int
) : GlideLoader<Drawable>() {

  override fun createRequest(request: RequestManager): RequestBuilder<Drawable> {
    return request.asDrawable()
        .load(resId)
  }

  override fun mutateResource(resource: Drawable): Drawable {
    return resource.mutate()
  }

  override fun setImage(
    view: ImageView,
    image: Drawable
  ) {
    view.setImageDrawable(image)
  }

}
