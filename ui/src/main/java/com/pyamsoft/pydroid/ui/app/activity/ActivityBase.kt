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

package com.pyamsoft.pydroid.ui.app.activity

import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.preference.PreferenceManager
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.app.fragment.BackPressDelegate

abstract class ActivityBase : AppCompatActivity(), ToolbarActivity {

  private var capturedToolbar: Toolbar? = null

  @CallSuper
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    PreferenceManager.setDefaultValues(this, R.xml.pydroid, false)
  }

  @CallSuper
  override fun onBackPressed() {
    if (!BackPressDelegate.onBackPressed(supportFragmentManager)) {
      super.onBackPressed()
    }
  }

  @CallSuper
  override fun onDestroy() {
    super.onDestroy()

    // Clear captured Toolbar
    capturedToolbar = null
  }

  override fun withToolbar(func: (Toolbar) -> Unit) {
    capturedToolbar?.let(func)
  }

  override fun requireToolbar(func: (Toolbar) -> Unit) {
    capturedToolbar!!.let(func)
  }

  protected fun setToolbar(toolbar: Toolbar?) {
    capturedToolbar = toolbar
  }
}
