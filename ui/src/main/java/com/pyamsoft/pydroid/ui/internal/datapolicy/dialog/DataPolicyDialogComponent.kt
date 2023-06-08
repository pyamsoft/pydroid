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

package com.pyamsoft.pydroid.ui.internal.datapolicy.dialog

import androidx.annotation.CheckResult
import coil.ImageLoader
import com.pyamsoft.pydroid.bootstrap.datapolicy.DataPolicyModule
import com.pyamsoft.pydroid.ui.app.AppProvider

internal interface DataPolicyDialogComponent {

  fun inject(injector: DataPolicyInjector)

  interface Factory {

    @CheckResult
    fun create(
        provider: AppProvider,
    ): DataPolicyDialogComponent

    data class Parameters
    internal constructor(
        internal val privacyPolicyUrl: String,
        internal val termsConditionsUrl: String,
        internal val imageLoader: ImageLoader,
        internal val module: DataPolicyModule,
    )
  }

  class Impl
  private constructor(
      private val provider: AppProvider,
      private val params: Factory.Parameters,
  ) : DataPolicyDialogComponent {

    override fun inject(injector: DataPolicyInjector) {
      injector.imageLoader = params.imageLoader
      injector.viewModel =
          DataPolicyDialogViewModeler(
              state = MutableDataPolicyDialogViewState(),
              interactor = params.module.provideInteractor(),
              provider = provider,
              privacyPolicyUrl = params.privacyPolicyUrl,
              termsConditionsUrl = params.termsConditionsUrl,
          )
    }

    internal class FactoryImpl
    internal constructor(
        private val params: Factory.Parameters,
    ) : Factory {

      override fun create(provider: AppProvider): DataPolicyDialogComponent {
        return Impl(provider, params)
      }
    }
  }
}
