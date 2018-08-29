package com.pyamsoft.pydroid.core.lifecycle

import androidx.lifecycle.Lifecycle.Event.ON_CREATE
import androidx.lifecycle.Lifecycle.Event.ON_DESTROY
import androidx.lifecycle.Lifecycle.Event.ON_PAUSE
import androidx.lifecycle.Lifecycle.Event.ON_RESUME
import androidx.lifecycle.Lifecycle.Event.ON_START
import androidx.lifecycle.Lifecycle.Event.ON_STOP
import androidx.lifecycle.LifecycleRegistry

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
