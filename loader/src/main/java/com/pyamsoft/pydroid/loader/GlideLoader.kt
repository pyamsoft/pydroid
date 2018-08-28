package com.pyamsoft.pydroid.loader

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.pyamsoft.pydroid.loader.GenericLoader
import com.pyamsoft.pydroid.loader.GlideLoaded
import com.pyamsoft.pydroid.loader.Loaded

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
