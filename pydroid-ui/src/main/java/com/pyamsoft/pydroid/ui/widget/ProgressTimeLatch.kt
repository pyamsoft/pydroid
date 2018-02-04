/*
 *     Copyright (C) 2017 Peter Kenji Yamanaka
 *
 *     This program is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation; either version 2 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License along
 *     with this program; if not, write to the Free Software Foundation, Inc.,
 *     51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.pyamsoft.pydroid.ui.widget

import android.arch.lifecycle.Lifecycle.Event.ON_DESTROY
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.OnLifecycleEvent
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.support.annotation.CheckResult
import kotlin.LazyThreadSafetyMode.NONE

class ProgressTimeLatch private constructor(
  owner: LifecycleOwner,
  var delay: Long = 500L,
  var minShowTime: Long = 500L,
  private val onRefreshed: (Boolean) -> Unit
) : LifecycleObserver {

  private val delayedShow: Runnable by lazy(NONE) { Runnable { show() } }
  private val delayedHide: Runnable by lazy(NONE) { Runnable { hide() } }
  private val handler: Handler by lazy(NONE) {
    Handler(Looper.getMainLooper())
  }

  private var lifecycleOwner: LifecycleOwner? = null
  private var lastShownTime: Long = 0L

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
    onRefreshed(true)
    lastShownTime = SystemClock.uptimeMillis()
  }

  private fun hide() {
    onRefreshed(false)
    lastShownTime = 0L
  }

  private fun clearOldCommands() {
    handler.removeCallbacksAndMessages(null)
  }

  var refreshing: Boolean = false
    set(value) {
      if (field != value) {
        field = value

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
    ): ProgressTimeLatch {
      return ProgressTimeLatch(owner, onRefreshed = onRefreshed)
    }

    @JvmStatic
    @CheckResult
    fun create(
      owner: LifecycleOwner,
      delay: Long,
      onRefreshed: (Boolean) -> Unit
    ): ProgressTimeLatch {
      return ProgressTimeLatch(owner, delay = delay, onRefreshed = onRefreshed)
    }

    @JvmStatic
    @CheckResult
    fun create(
      owner: LifecycleOwner,
      delay: Long,
      minShowTime: Long,
      onRefreshed: (Boolean) -> Unit
    ): ProgressTimeLatch {
      return ProgressTimeLatch(owner, delay, minShowTime, onRefreshed)
    }
  }
}
