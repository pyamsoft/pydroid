/*
 * Copyright 2019 Peter Kenji Yamanaka
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.pyamsoft.pydroid.ui.widget

import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.annotation.CheckResult
import androidx.lifecycle.Lifecycle.Event.ON_DESTROY
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import timber.log.Timber
import kotlin.LazyThreadSafetyMode.NONE

class RefreshLatch private constructor(
  owner: LifecycleOwner,
  private val delay: Long = 300L,
  private val minShowTime: Long = 700L,
  private val onRefreshed: (Boolean) -> Unit
) : LifecycleObserver {

  private val delayedShow: Runnable by lazy(NONE) { Runnable { show() } }
  private val delayedHide: Runnable by lazy(NONE) { Runnable { hide() } }
  private val handler: Handler by lazy(NONE) {
    Handler(Looper.getMainLooper())
  }

  private var lifecycleOwner: LifecycleOwner? = null
  private var lastShownTime: Long = 0L
  private var refreshState: Boolean = false

  init {
    owner.lifecycle.addObserver(this)
    lifecycleOwner = owner
  }

  @OnLifecycleEvent(ON_DESTROY)
  internal fun onDestroy() {
    clearOldCommands()
    lifecycleOwner?.lifecycle?.removeObserver(this)
    lifecycleOwner = null
  }

  private fun show() {
    Timber.d("Start isRefreshing")
    onRefreshed(true)
    lastShownTime = SystemClock.uptimeMillis()
    refreshState = true
  }

  private fun hide() {
    Timber.d("Stop isRefreshing")
    onRefreshed(false)
    lastShownTime = 0L
    refreshState = false
  }

  private fun clearOldCommands() {
    handler.removeCallbacksAndMessages(null)
  }

  fun forceRefresh() {
    refreshState = true
    clearOldCommands()
    show()
  }

  var isRefreshing: Boolean
    get() = refreshState
    set(value) {
      if (refreshState != value) {
        refreshState = value

        // Clear old commands
        clearOldCommands()

        if (value) {
          // Asked to show
          handler.postDelayed(delayedShow, delay)
        } else if (lastShownTime > 0L) {
          // Asked to hide when already shown
          val shownTime: Long = SystemClock.uptimeMillis() - lastShownTime

          // Have we hit minimum time to avoid flicker?
          if (shownTime < minShowTime) {
            // No, delay the hide until min time
            handler.postDelayed(delayedHide, minShowTime - shownTime)
          } else {
            // Yes we have, hide now
            hide()
          }
        } else {
          // Asked to hide
          hide()
        }
      }
    }

  companion object {

    @JvmStatic
    @CheckResult
    fun create(
      owner: LifecycleOwner,
      onRefreshed: (Boolean) -> Unit
    ): RefreshLatch {
      return RefreshLatch(owner, onRefreshed = onRefreshed)
    }

    @JvmStatic
    @CheckResult
    fun create(
      owner: LifecycleOwner,
      delay: Long,
      onRefreshed: (Boolean) -> Unit
    ): RefreshLatch {
      return RefreshLatch(owner, delay = delay, onRefreshed = onRefreshed)
    }

    @JvmStatic
    @CheckResult
    fun create(
      owner: LifecycleOwner,
      delay: Long,
      minShowTime: Long,
      onRefreshed: (Boolean) -> Unit
    ): RefreshLatch {
      return RefreshLatch(owner, delay, minShowTime, onRefreshed)
    }
  }
}
