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

package com.pyamsoft.pydroid.ui.internal.datapolicy

import com.pyamsoft.pydroid.core.Logger
import com.pyamsoft.pydroid.core.requireNotNull
import com.pyamsoft.pydroid.ui.app.PYDroidActivity
import com.pyamsoft.pydroid.ui.internal.datapolicy.dialog.DataPolicyDisclosureDialog
import com.pyamsoft.pydroid.util.doOnCreate
import com.pyamsoft.pydroid.util.doOnDestroy

/** Handles Billing related work in an Activity */
internal class DataPolicyDelegate(activity: PYDroidActivity, viewModel: DataPolicyViewModel) {

  private var activity: PYDroidActivity? = activity
  private var viewModel: DataPolicyViewModel? = viewModel

  /** Bind Activity for related DataPolicy events */
  fun bindEvents() {
    val act = activity.requireNotNull()

    act.doOnCreate {
      viewModel.requireNotNull().bindController(act) { event ->
        return@bindController when (event) {
          is DataPolicyControllerEvent.ShowPolicy -> DataPolicyDisclosureDialog.show(act)
        }
      }
    }

    act.doOnDestroy {
      viewModel = null
      activity = null
    }
  }

  /** Show Data policy */
  fun showDataPolicyDisclosure() {
    val vm = viewModel
    if (vm == null) {
      Logger.w("Cannot show data policy, ViewModel is null")
    } else {
      vm.handleShowDisclosure(false)
    }
  }
}
