@file:JvmName("Enforcer")

package com.pyamsoft.pydroid.core.threads

import android.os.Looper
import androidx.annotation.CheckResult

class Enforcer(private val debug: Boolean) {

  private val mainLooper = Looper.getMainLooper()

  @CheckResult
  fun isMainThread(): Boolean {
    return mainLooper.thread == Thread.currentThread()
  }

  fun assertNotOnMainThread() {
    // No enforcement in production mode - we will deal with things being slow instead
    // of flat out crashing
    if (!debug) {
      return
    }

    if (isMainThread()) {
      throw AssertionError("Should be off main thread!")
    }
  }

}


