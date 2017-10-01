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

package com.pyamsoft.pydroid.ui.app.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.support.annotation.CallSuper
import android.widget.Toast
import com.pyamsoft.pydroid.ui.helper.Toasty

abstract class BackPressConfirmActivity : ActivityBase() {

  private var backBeenPressed: Boolean = false
  private lateinit var handler: Handler
  private lateinit var backBeenPressedToast: Toast

  /**
   * Override this if you want normal back button behavior
   */
  protected open val shouldConfirmBackPress: Boolean = true

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

