package com.pyamsoft.pydroid.loader

import androidx.lifecycle.Lifecycle.Event.ON_DESTROY
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.bumptech.glide.Glide
import timber.log.Timber

class GlideLoaded(
  private val target: ImageTarget<*>
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

    val view = target.view()
    Glide.with(view.context.applicationContext)
        .clear(view)
    target.clear()
  }

}
