/*
 * Copyright 2017 Peter Kenji Yamanaka
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

package com.pyamsoft.pydroid.ui.app.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.support.annotation.CallSuper
import android.support.annotation.CheckResult
import android.widget.Toast
import com.pyamsoft.pydroid.ui.helper.Toasty

abstract class BackPressConfirmActivity : ActivityBase() {
  @JvmField protected var backBeenPressed: Boolean = false
  private lateinit var handler: Handler
  private lateinit var backBeenPressedToast: Toast

  /**
   * Override this if you want normal back button behavior
   */
  protected open val shouldConfirmBackPress: Boolean
    @CheckResult get() = true

  @CallSuper override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    if (shouldConfirmBackPress) {
      enableBackBeenPressedConfirmation()
    }
  }

  @CallSuper override fun onBackPressed() {
    if (backBeenPressed || !shouldConfirmBackPress) {
      backBeenPressed = false
      handler.removeCallbacksAndMessages(null)
      super.onBackPressed()
    } else {
      backBeenPressed = true
      backBeenPressedToast.show()
      handler.postDelayed({ backBeenPressed = false }, BACK_PRESSED_DELAY)
    }
  }

  @SuppressLint("ShowToast") private fun enableBackBeenPressedConfirmation() {
    backBeenPressed = false
    handler = Handler()
    backBeenPressedToast = Toasty.makeText(this, "Press Again to Exit", Toasty.LENGTH_SHORT)
    handler.removeCallbacksAndMessages(null)
  }

  companion object {

    private const val BACK_PRESSED_DELAY = 1600L
  }
}

