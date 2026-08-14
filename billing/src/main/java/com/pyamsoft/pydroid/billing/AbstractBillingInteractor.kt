/*
 * Copyright 2026 pyamsoft
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pyamsoft.pydroid.billing

import androidx.activity.ComponentActivity
import androidx.annotation.CheckResult
import androidx.lifecycle.lifecycleScope
import com.pyamsoft.pydroid.bus.EventBus
import com.pyamsoft.pydroid.core.LintIgnoreTooManyFunctions
import com.pyamsoft.pydroid.util.AppDispatchers
import com.pyamsoft.pydroid.util.Logger
import com.pyamsoft.pydroid.util.doOnCreate
import com.pyamsoft.pydroid.util.doOnDestroy
import kotlinx.coroutines.launch

@LintIgnoreTooManyFunctions
internal abstract class AbstractBillingInteractor
protected constructor(
    private val errorBus: EventBus<Throwable>,
    private val purchaseBus: EventBus<BillingPurchase>,
    protected val dispatchers: AppDispatchers,
) : BillingConnector {

  final override fun bind(activity: ComponentActivity): ConnectedBillingInteractor {
    val connected =
        connect(
            activity = activity,
            errorBus = errorBus,
            purchaseBus = purchaseBus,
        )

    activity.lifecycle.doOnCreate {
      Logger.d { "Attempt to connect Billing on Activity create" }
      activity.lifecycleScope.launch(context = dispatchers.default) { connected.onClientConnect() }
    }

    activity.lifecycle.doOnDestroy {
      Logger.d { "Attempt disconnect Billing on Activity destroy" }
      connected.onClientDisconnect()
    }

    return connected
  }

  @CheckResult
  protected abstract fun connect(
      activity: ComponentActivity,
      errorBus: EventBus<Throwable>,
      purchaseBus: EventBus<BillingPurchase>,
  ): ConnectedBillingInteractor
}
