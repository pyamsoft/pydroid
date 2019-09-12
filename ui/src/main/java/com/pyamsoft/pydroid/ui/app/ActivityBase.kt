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

package com.pyamsoft.pydroid.ui.app

import android.os.Build
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

abstract class ActivityBase : AppCompatActivity(), ToolbarActivity, ToolbarActivityProvider {

    /**
     * The main view container for all page level fragment transactions
     */
    abstract val fragmentContainerId: Int

    /**
     * Activity level toolbar, similar to ActionBar
     */
    private var capturedToolbar: Toolbar? = null

    @CallSuper
    override fun onDestroy() {
        super.onDestroy()

        // Clear captured Toolbar
        capturedToolbar = null
    }

    @CallSuper
    override fun onBackPressed() {
        onAndroid10BackPressed()
    }

    /**
     * Android 10 leaks Activity on back pressed....
     * https://twitter.com/Piwai/status/1169274622614704129
     */
    private fun onAndroid10BackPressed() {
        // Copied from FragmentActivity
        val fragmentManager = supportFragmentManager
        val isStateSaved = fragmentManager.isStateSaved
        if (isStateSaved && Build.VERSION.SDK_INT <= Build.VERSION_CODES.N_MR1) {
            // Older versions will throw an exception from the framework
            // FragmentManager.popBackStackImmediate(), so we'll just
            // return here. The Activity is likely already on its way out
            // since the fragmentManager has already been saved.
            return
        }

        if (isStateSaved || !fragmentManager.popBackStackImmediate()) {
            // Using finishAfterTransition instead of onBackPressed() should fix leak
            if (isTaskRoot) {
                supportFinishAfterTransition()
            } else {
                super.onBackPressed()
            }
        }
    }

    final override fun withToolbar(func: (Toolbar) -> Unit) {
        capturedToolbar?.let(func)
    }

    final override fun requireToolbar(func: (Toolbar) -> Unit) {
        requireNotNull(capturedToolbar).let(func)
    }

    final override fun setToolbar(toolbar: Toolbar?) {
        capturedToolbar = toolbar
    }
}
