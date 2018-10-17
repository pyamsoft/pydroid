package com.pyamsoft.pydroid.ui.util

import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.pyamsoft.pydroid.util.runWhenReady

fun FragmentTransaction.commit(owner: LifecycleOwner) {
  commit(owner.lifecycle)
}

fun FragmentTransaction.commit(lifecycle: Lifecycle) {
  runWhenReady(lifecycle) { commit() }
}
