package com.pyamsoft.pydroid.ui.util

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Lifecycle.Event.ON_START
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent

inline fun runWhenReady(
  owner: LifecycleOwner,
  crossinline func: () -> Unit
) {
  runWhenReady(owner.lifecycle, func)
}

inline fun runWhenReady(
  lifecycle: Lifecycle,
  crossinline func: () -> Unit
) {
  val observer = object : LifecycleObserver {

    @Suppress("unused")
    @OnLifecycleEvent(ON_START)
    fun onReady() {
      lifecycle.removeObserver(this)
      func()
    }

  }

  lifecycle.addObserver(observer)
}
