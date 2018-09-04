package com.pyamsoft.pydroid.loader

import android.content.Context
import android.widget.ImageView
import androidx.lifecycle.Lifecycle.Event.ON_DESTROY
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.bumptech.glide.Glide
import timber.log.Timber

class GlideLoaded(
  private val context: Context,
  private val view: ImageView
) : Loaded, LifecycleObserver {

  private var lifeCycleOwner: LifecycleOwner? = null

  override fun bind(owner: LifecycleOwner) {
    Timber.d("Bind to lifecycle")
    owner.lifecycle.addObserver(this)
    lifeCycleOwner = owner
  }

  @OnLifecycleEvent(ON_DESTROY)
  internal fun unbindOnDestroy() {
    Timber.d("Unbind on destroy")
    lifeCycleOwner?.lifecycle?.removeObserver(this)
    lifeCycleOwner = null

    Glide.with(context.applicationContext)
        .clear(view)
    view.setImageDrawable(null)
  }

}
