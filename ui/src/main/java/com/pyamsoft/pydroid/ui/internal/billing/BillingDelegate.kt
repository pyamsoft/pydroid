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

package com.pyamsoft.pydroid.ui.internal.billing

import androidx.appcompat.app.AppCompatActivity
import com.pyamsoft.pydroid.billing.BillingConnector
import com.pyamsoft.pydroid.core.Logger
import com.pyamsoft.pydroid.core.requireNotNull
import com.pyamsoft.pydroid.ui.app.PYDroidActivity
import com.pyamsoft.pydroid.util.doOnCreate
import com.pyamsoft.pydroid.util.doOnDestroy

/** Handles Billing related work in an Activity */
internal class BillingDelegate(
    activity: PYDroidActivity,
    connector: BillingConnector,
) {

  private var activity: PYDroidActivity? = activity
  private var connector: BillingConnector? = connector

  /** Connect to the billing service */
  fun connect() {
    val act = activity.requireNotNull()

    connectBilling(act)

    act.doOnDestroy {
      connector = null
      activity = null
    }
  }

  /** Attempts to connect to in-app billing */
  private fun connectBilling(activity: AppCompatActivity) {
    Logger.d("Prepare application billing connection on create callback")
    activity.doOnCreate {
      Logger.d("Attempt Connect Billing")
      val billing = connector
      if (billing == null) {
        val msg = "In-App Billing is not initialized!"
        val error = IllegalStateException(msg)
        Logger.e(error, msg)
        throw error
      } else {
        Logger.d("In-App billing is created, connect")
        billing.start(activity)
      }
    }
  }
}
