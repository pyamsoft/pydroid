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

package com.pyamsoft.pydroid.ui.changelog

import android.os.Bundle
import androidx.activity.viewModels
import androidx.annotation.CallSuper
import androidx.lifecycle.ViewModelProvider
import com.pyamsoft.pydroid.core.Logger

import com.pyamsoft.pydroid.core.requireNotNull
import com.pyamsoft.pydroid.inject.Injector
import com.pyamsoft.pydroid.ui.PYDroidComponent
import com.pyamsoft.pydroid.ui.internal.changelog.ChangeLogControllerEvent
import com.pyamsoft.pydroid.ui.internal.changelog.ChangeLogProvider
import com.pyamsoft.pydroid.ui.internal.changelog.ChangeLogViewModel
import com.pyamsoft.pydroid.ui.internal.changelog.dialog.ChangeLogDialog
import com.pyamsoft.pydroid.ui.rating.RatingActivity

/** An activity which displays an in-app changelog */
public abstract class ChangeLogActivity : RatingActivity(), ChangeLogProvider {

  internal var changeLogFactory: ViewModelProvider.Factory? = null
  private val viewModel by viewModels<ChangeLogViewModel> { changeLogFactory.requireNotNull() }

  /** Version name of application */
  protected abstract val versionName: String

  /** On activity create */
  @CallSuper
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    Injector.obtainFromApplication<PYDroidComponent>(this).plusChangeLog().create().inject(this)

    viewModel.bindController(this) { event ->
      return@bindController when (event) {
        is ChangeLogControllerEvent.ShowChangeLog -> showChangeLog()
      }
    }
  }

  private fun showChangeLog() {
    Logger.d("Show Change log")
    ChangeLogDialog.open(this)
  }

  /** On destroy */
  @CallSuper
  override fun onDestroy() {
    super.onDestroy()
    changeLogFactory = null
  }

  /** On post resume, we can show Dialogs */
  @CallSuper
  override fun onPostResume() {
    super.onPostResume()

    // Called in onPostResume so that the DialogFragment can be shown correctly.
    viewModel.handleShow(false)
  }
}
