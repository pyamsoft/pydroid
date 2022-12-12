/*
 * Copyright 2022 Peter Kenji Yamanaka
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

package com.pyamsoft.pydroid.ui.internal.datapolicy

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.pyamsoft.pydroid.core.requireNotNull
import com.pyamsoft.pydroid.ui.internal.datapolicy.dialog.DataPolicyDisclosureDialog
import com.pyamsoft.pydroid.util.doOnCreate
import com.pyamsoft.pydroid.util.doOnDestroy

/** Handles Data Policy related work in an Activity */
internal class DataPolicyDelegate(
    activity: FragmentActivity,
    viewModel: DataPolicyViewModeler,
) {

  private var activity: FragmentActivity? = activity
  private var viewModel: DataPolicyViewModeler? = viewModel

  /** Bind Activity for related DataPolicy events */
  fun bindEvents() {
    val a = activity.requireNotNull()
    a.doOnDestroy {
      viewModel = null
      activity = null
    }

    a.doOnCreate {
      viewModel
          .requireNotNull()
          .bind(
              scope = a.lifecycleScope,
              onShowPolicy = { DataPolicyDisclosureDialog.show(a) },
          )
    }
  }

  /**
   * Show if needed
   *
   * If somehow the dialog was dismissed without interaction, this will check and re-fire the show
   * event
   */
  fun attemptReShowIfNeeded() {
    val a = activity.requireNotNull()
    viewModel
        .requireNotNull()
        .handleShowDataPolicyDialogIfPossible(
            scope = a.lifecycleScope,
            onNeedsToShow = {
              // This will not show again if one is already displayed
              DataPolicyDisclosureDialog.show(a)
            },
        )
  }
}
