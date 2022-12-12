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

import androidx.fragment.app.FragmentActivity
import com.pyamsoft.pydroid.billing.BillingConnector
import com.pyamsoft.pydroid.core.Logger
import com.pyamsoft.pydroid.util.doOnCreate

/** Handles Billing related work in an Activity */
internal class BillingDelegate(
    activity: FragmentActivity,
    connector: BillingConnector,
    disabled: Boolean,
) {

  init {
    if (disabled) {
      Logger.w("Application has disabled the billing component")
    } else {
      activity.doOnCreate { connectBilling(activity, connector) }
    }
  }

  /** Attempts to connect to in-app billing */
  private fun connectBilling(
      activity: FragmentActivity,
      connector: BillingConnector,
  ) {
    Logger.d("In-App billing is created, connect")
    connector.start(activity)
  }
}
