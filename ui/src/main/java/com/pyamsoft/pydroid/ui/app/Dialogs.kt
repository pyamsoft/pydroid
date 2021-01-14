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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent

/**
 * Remove the title from a dialog
 */
@CheckResult
public fun Dialog.noTitle(): Dialog {
    requestWindowFeature(Window.FEATURE_NO_TITLE)
    return this
}

/**
 * Call this from at least onCreate() but before onResume()
 */
public fun DialogFragment.makeFullscreen() {
    setSizes(fullHeight = true, fullWidth = true)
}

/**
 * Call this from at least onCreate() but before onResume()
 */
public fun DialogFragment.makeFullWidth() {
    setSizes(fullHeight = false, fullWidth = true)
}

/**
 * Call this from at least onCreate() but before onResume()
 */
public fun DialogFragment.makeFullHeight() {
    setSizes(fullHeight = true, fullWidth = false)
}

private fun DialogFragment.setSizes(fullWidth: Boolean, fullHeight: Boolean) {
    val self = this
    val owner = self.viewLifecycleOwner
    owner.lifecycle.addObserver(object : LifecycleObserver {

        @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
        fun onResume() {
            self.dialog?.window?.apply {
                val match = WindowManager.LayoutParams.MATCH_PARENT
                val wrap = WindowManager.LayoutParams.WRAP_CONTENT
                val width = if (fullWidth) match else wrap
                val height = if (fullHeight) match else wrap
                setLayout(width, height)
                setGravity(Gravity.CENTER)
            }
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        fun onDestroy() {
            owner.lifecycle.removeObserver(this)
        }
    })
}
