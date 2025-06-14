/*
 * Copyright 2024 pyamsoft
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pyamsoft.pydroid.core

import android.os.Looper
import androidx.annotation.CheckResult

/** Enforces that operations are taking place on expected threads, or throws Assertion errors */
public interface ThreadEnforcer {

  /** Throws an exception if the current thread is the Main or UI thread */
  public fun assertOffMainThread()

  /** Throws an exception if the current thread is not the Main or UI thread */
  public fun assertOnMainThread()
}

/** A thread looper to be used in Debug mode */
private object DebugThreadEnforcer : ThreadEnforcer {

  private val mainLooper by lazy { Looper.getMainLooper().requireNotNull() }

  @CheckResult
  private fun isMainThread(): Boolean {
    return mainLooper.thread == Thread.currentThread()
  }

  override fun assertOffMainThread() {
    if (isMainThread()) {
      throw AssertionError("This operation must be OFF the Main/UI thread!")
    }
  }

  override fun assertOnMainThread() {
    if (!isMainThread()) {
      throw AssertionError("This operation must be ON the Main/UI thread!")
    }
  }
}

/** A noop thread looper to be used in Production mode */
private object NoopThreadEnforcer : ThreadEnforcer {
  override fun assertOffMainThread() {
    // Noop
  }

  override fun assertOnMainThread() {
    // Noop
  }
}

/** Returns a ThreadEnforcer for use in an application */
@CheckResult
public fun createThreadEnforcer(debug: Boolean): ThreadEnforcer {
  return if (debug) DebugThreadEnforcer else NoopThreadEnforcer
}
