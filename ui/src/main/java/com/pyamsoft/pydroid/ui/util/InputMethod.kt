/*
 * Copyright 2021 Peter Kenji Yamanaka
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

package com.pyamsoft.pydroid.ui.util

import android.content.Context
import android.view.View
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import androidx.annotation.CheckResult
import androidx.core.content.getSystemService
import com.pyamsoft.pydroid.core.requireNotNull

@CheckResult
private fun getInputMethodManager(context: Context): InputMethodManager {
  return context.applicationContext.getSystemService<InputMethodManager>().requireNotNull()
}

/** Show the soft keyboard once the view already has focus */
@JvmOverloads
public fun View.showKeyboard(
    inputMethodManager: InputMethodManager = getInputMethodManager(this.context)
) {
  if (isFocused) {
    post {
      // Post just in case the view is not yet fully set up
      inputMethodManager.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
    }
  }
}

/** Focus the keyboard and then show the keyboard once it is focused. */
@JvmOverloads
public fun View.focusAndShowKeyboard(
    inputMethodManager: InputMethodManager = getInputMethodManager(this.context)
) {
  this.requestFocus()
  if (this.hasWindowFocus()) {
    this.showKeyboard(inputMethodManager)
  } else {
    val self = this
    self.viewTreeObserver.addOnWindowFocusChangeListener(
        object : ViewTreeObserver.OnWindowFocusChangeListener {

          override fun onWindowFocusChanged(hasFocus: Boolean) {
            if (hasFocus) {
              self.showKeyboard(inputMethodManager)
              self.viewTreeObserver.removeOnWindowFocusChangeListener(this)
            }
          }
        })
  }
}
