/*
 * Copyright 2020 Peter Kenji Yamanaka
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
 */

package com.pyamsoft.pydroid.ui.app

import android.app.Dialog
import android.view.Gravity
import android.view.Window
import android.view.WindowManager
import androidx.annotation.CheckResult
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.pyamsoft.pydroid.util.asPx

/** Remove the title from a dialog */
@CheckResult
public fun Dialog.noTitle(): Dialog {
  requestWindowFeature(Window.FEATURE_NO_TITLE)
  return this
}

/**
 * Call this from at least onCreate() but before onResume()
 *
 * By default, this attempts to measure the window size and create a slightly smaller than full
 * screen dialog to avoid the MATCH_PARENT side effect where the Navigation bar is not colored.
 */
@JvmOverloads
public fun DialogFragment.makeFullscreen(useMatchParent: Boolean = false) {
  setSizes(fullHeight = true, fullWidth = true, useMatchParent = useMatchParent)
}

/**
 * Call this from at least onCreate() but before onResume()
 *
 * By default, this attempts to measure the window size and create a slightly smaller than full
 * screen dialog to avoid the MATCH_PARENT side effect where the Navigation bar is not colored.
 */
@JvmOverloads
public fun DialogFragment.makeFullWidth(useMatchParent: Boolean = false) {
  setSizes(fullHeight = false, fullWidth = true, useMatchParent = useMatchParent)
}

/**
 * Call this from at least onCreate() but before onResume() By default, this attempts to measure the
 * window size and create a slightly smaller than full screen dialog to avoid the MATCH_PARENT side
 * effect where the Navigation bar is not colored.
 */
@JvmOverloads
public fun DialogFragment.makeFullHeight(useMatchParent: Boolean = false) {
  setSizes(fullHeight = true, fullWidth = false, useMatchParent = useMatchParent)
}

private const val MATCH = WindowManager.LayoutParams.MATCH_PARENT
private const val WRAP = WindowManager.LayoutParams.WRAP_CONTENT

private fun DialogFragment.setSizes(
    fullWidth: Boolean,
    fullHeight: Boolean,
    useMatchParent: Boolean
) {
  val self = this
  self.viewLifecycleOwner.lifecycle.addObserver(
      object : DefaultLifecycleObserver {

        override fun onResume(owner: LifecycleOwner) {
          self.dialog?.window?.apply {
            val width: Int
            val height: Int
            if (useMatchParent) {
              width = if (fullWidth) MATCH else WRAP
              height = if (fullHeight) MATCH else WRAP
            } else {
              val activity = self.requireActivity()
              val configuration = activity.resources.configuration
              val screenHeightDp = configuration.screenHeightDp
              val screenWidthDp = configuration.screenWidthDp

              // Subtract 2 just to slightly avoid the MATCH_PARENT size
              val screenHeight = screenHeightDp.asPx(activity) - 2
              val screenWidth = screenWidthDp.asPx(activity) - 2

              width = if (fullWidth) screenWidth else WRAP
              height = if (fullHeight) screenHeight else WRAP
            }

            setLayout(width, height)
            setGravity(Gravity.CENTER)
          }
        }

        override fun onDestroy(owner: LifecycleOwner) {
          owner.lifecycle.removeObserver(this)
        }
      },
  )
}
