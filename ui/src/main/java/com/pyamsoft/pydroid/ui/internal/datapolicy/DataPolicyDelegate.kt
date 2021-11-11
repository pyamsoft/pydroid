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
import com.pyamsoft.pydroid.util.doOnResume

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
      showDataPolicyDisclosure(act)
    }

    act.doOnDestroy {
      viewModel = null
      activity = null
    }
  }

  /** Attempts to load and secure the application */
  private fun showDataPolicyDisclosure(activity: PYDroidActivity) {
    Logger.d("Prepare data policy disclosure")
    activity.doOnResume {
      Logger.d("Attempt show DPD")
      val vm = viewModel
      if (vm == null) {
        val msg = "DataPolicy is not initialized!"
        val error = IllegalStateException(msg)
        Logger.e(error, msg)
        throw error
      } else {
        Logger.d("Application is created, protect")
        vm.handleShowDisclosure(false)
      }
    }
  }
}
