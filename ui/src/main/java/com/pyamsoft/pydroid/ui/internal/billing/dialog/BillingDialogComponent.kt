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

package com.pyamsoft.pydroid.ui.internal.billing.dialog

import androidx.annotation.CheckResult
import coil.ImageLoader
import com.pyamsoft.pydroid.billing.BillingModule
import com.pyamsoft.pydroid.bootstrap.changelog.ChangeLogModule
import com.pyamsoft.pydroid.ui.app.AppProvider
import com.pyamsoft.pydroid.ui.internal.app.ComposeThemeFactory

internal interface BillingDialogComponent {

  fun inject(injector: BillingDialogInjector)

  interface Factory {

    @CheckResult fun create(provider: AppProvider): BillingDialogComponent

    data class Parameters
    internal constructor(
        internal val state: MutableBillingDialogViewState,
        internal val changeLogModule: ChangeLogModule,
        internal val billingModule: BillingModule,
        internal val composeTheme: ComposeThemeFactory,
        internal val imageLoader: ImageLoader,
    )
  }

  class Impl
  private constructor(
      private val params: Factory.Parameters,
      private val provider: AppProvider,
  ) : BillingDialogComponent {

    override fun inject(injector: BillingDialogInjector) {
      injector.imageLoader = params.imageLoader
      injector.purchaseClient = params.billingModule.provideLauncher()
      injector.viewModel =
          BillingDialogViewModeler(
              state = params.state,
              changeLogInteractor = params.changeLogModule.provideInteractor(),
              interactor = params.billingModule.provideInteractor(),
              provider = provider,
          )
    }

    internal class FactoryImpl
    internal constructor(
        private val params: Factory.Parameters,
    ) : Factory {

      override fun create(provider: AppProvider): BillingDialogComponent {
        return Impl(params, provider)
      }
    }
  }
}
