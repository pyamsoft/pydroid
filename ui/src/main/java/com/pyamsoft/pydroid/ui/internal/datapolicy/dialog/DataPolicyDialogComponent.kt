/*
 * Copyright 2020 Peter Kenji Yamanaka
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
import com.pyamsoft.pydroid.arch.createViewModelFactory
import com.pyamsoft.pydroid.bootstrap.datapolicy.DataPolicyInteractor
import com.pyamsoft.pydroid.ui.app.ComposeThemeFactory
import com.pyamsoft.pydroid.ui.internal.app.AppProvider

internal interface DataPolicyDialogComponent {

  fun inject(dialog: DataPolicyDisclosureDialog)

  interface Factory {

    @CheckResult
    fun create(
        provider: AppProvider,
    ): DataPolicyDialogComponent

    data class Parameters
    internal constructor(
        internal val privacyPolicyUrl: String,
        internal val termsConditionsUrl: String,
        internal val interactor: DataPolicyInteractor,
        internal val composeTheme: ComposeThemeFactory,
        internal val imageLoader: ImageLoader,
    )
  }

  class Impl
  private constructor(
      private val provider: AppProvider,
      private val params: Factory.Parameters,
  ) : DataPolicyDialogComponent {

    private val factory = createViewModelFactory {
      DataPolicyDialogViewModel(
          params.privacyPolicyUrl,
          params.termsConditionsUrl,
          provider,
          params.interactor,
      )
    }

    override fun inject(dialog: DataPolicyDisclosureDialog) {
      dialog.composeTheme = params.composeTheme
      dialog.imageLoader = params.imageLoader
      dialog.factory = factory
    }

    internal class FactoryImpl internal constructor(private val params: Factory.Parameters) :
        Factory {

      override fun create(provider: AppProvider): DataPolicyDialogComponent {
        return Impl(provider, params)
      }
    }
  }
}
