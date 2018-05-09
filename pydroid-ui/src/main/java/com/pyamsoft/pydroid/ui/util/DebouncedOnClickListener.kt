/*
 * Copyright (C) 2018 Peter Kenji Yamanaka
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pyamsoft.pydroid.ui.util

import androidx.annotation.CheckResult
import android.view.View

/**
 * Click listener which debounces all other click events for the frame
 */
abstract class DebouncedOnClickListener protected constructor() : View.OnClickListener {

  final override fun onClick(view: View) {
    if (enabled) {
      enabled = false
      view.post(enableAgain)
      doClick(view)
    }
  }

  abstract fun doClick(view: View)

  companion object {
    private var enabled: Boolean = true
    private var enableAgain: Runnable = Runnable { enabled = true }

    @CheckResult
    @JvmStatic
    inline fun create(crossinline func: (View) -> Unit): View.OnClickListener {
      return object : DebouncedOnClickListener() {
        override fun doClick(view: View) {
          func(view)
        }
      }
    }
  }
}
