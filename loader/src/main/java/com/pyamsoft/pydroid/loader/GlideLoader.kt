package com.pyamsoft.pydroid.loader

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide

internal class GlideLoader(
  private val context: Context,
  @DrawableRes private val resource: Int
) : GenericLoader<Drawable>() {

  override fun into(imageView: ImageView): Loaded {
    Glide.with(context.applicationContext)
        .load(resource)
        .into(imageView)

    return GlideLoaded(context.applicationContext, imageView)
  }

}
