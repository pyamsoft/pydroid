package com.pyamsoft.pydroid.ui.util

import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Lifecycle.Event.ON_START
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent

fun FragmentTransaction.commit(owner: LifecycleOwner) {
  commit(owner.lifecycle)
}

fun FragmentTransaction.commit(lifecycle: Lifecycle) {
  val observer = object : LifecycleObserver {

    @Suppress("unused")
    @OnLifecycleEvent(ON_START)
    fun safeCommit() {
      lifecycle.removeObserver(this)
      commit()
    }
  }

  lifecycle.addObserver(observer)
}
