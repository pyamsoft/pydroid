/*
 * Copyright 2023 pyamsoft
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

package com.pyamsoft.pydroid.billing

import android.content.Context
import androidx.annotation.CheckResult
import com.pyamsoft.pydroid.billing.store.PlayStoreBillingInteractor
import com.pyamsoft.pydroid.bus.EventBus

/** Billing module */
public class BillingModule(params: Parameters) {

  private val impl =
      PlayStoreBillingInteractor(
          context = params.context.applicationContext,
          errorBus = params.errorBus,
      )

  /** Provide a billing instance */
  @CheckResult
  public fun provideInteractor(): BillingInteractor {
    return impl
  }

  /** Provide a launcher instance */
  @CheckResult
  public fun provideLauncher(): BillingLauncher {
    return impl
  }

  /** Provide a connector instance */
  @CheckResult
  public fun provideConnector(): BillingConnector {
    return impl
  }

  /** Module parameters */
  public data class Parameters(
      internal val context: Context,
      internal val errorBus: EventBus<Throwable>,
  )
}
