package com.pyamsoft.pydroid.util

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Lifecycle.Event.ON_CREATE
import androidx.lifecycle.Lifecycle.Event.ON_DESTROY
import androidx.lifecycle.Lifecycle.Event.ON_PAUSE
import androidx.lifecycle.Lifecycle.Event.ON_RESUME
import androidx.lifecycle.Lifecycle.Event.ON_START
import androidx.lifecycle.Lifecycle.Event.ON_STOP
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
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

fun LifecycleRegistry.fakeBind() {
  handleLifecycleEvent(ON_CREATE)
  handleLifecycleEvent(ON_START)
  handleLifecycleEvent(ON_RESUME)
}

fun LifecycleRegistry.fakeUnbind() {
  handleLifecycleEvent(ON_PAUSE)
  handleLifecycleEvent(ON_STOP)
  handleLifecycleEvent(ON_DESTROY)
}
