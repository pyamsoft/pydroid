/*
 * Copyright 2025 pyamsoft
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

package com.pyamsoft.pydroid.ui.internal.billing

import androidx.annotation.CheckResult
import com.pyamsoft.pydroid.ui.billing.BillingUpsell

internal interface BillingComponent {

  fun inject(component: BillingUpsell)

  interface Factory {

    @CheckResult fun create(): BillingComponent

    @ConsistentCopyVisibility
    data class Parameters
    internal constructor(
        internal val preferences: BillingPreferences,
        internal val state: MutableBillingViewState,
        internal val isFakeBillingUpsell: Boolean,
    )
  }

  class Impl
  private constructor(
      private val params: Factory.Parameters,
  ) : BillingComponent {

    override fun inject(component: BillingUpsell) {
      component.viewModel =
          BillingViewModeler(
              preferences = params.preferences,
              state = params.state,
              isFakeUpsell = params.isFakeBillingUpsell,
          )
    }

    internal class FactoryImpl
    internal constructor(
        private val params: Factory.Parameters,
    ) : Factory {

      override fun create(): BillingComponent {
        return Impl(params)
      }
    }
  }
}
