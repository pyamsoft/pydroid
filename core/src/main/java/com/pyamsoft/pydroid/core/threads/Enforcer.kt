@file:JvmName("Enforcer")

package com.pyamsoft.pydroid.core.threads

import android.os.Looper
import androidx.annotation.CheckResult

private val mainLooper = Looper.getMainLooper()

@CheckResult
fun isMainThread(): Boolean {
  return mainLooper.thread == Thread.currentThread()
}

fun assertNotOnMainThread() {
  if (isMainThread()) {
    throw AssertionError("Should be off main thread!")
  }
}

