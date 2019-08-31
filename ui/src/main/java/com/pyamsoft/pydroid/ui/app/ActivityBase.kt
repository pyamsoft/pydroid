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
