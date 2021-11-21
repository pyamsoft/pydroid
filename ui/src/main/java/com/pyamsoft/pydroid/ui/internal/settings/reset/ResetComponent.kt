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

package com.pyamsoft.pydroid.ui.internal.settings.reset

import androidx.annotation.CheckResult
import com.pyamsoft.pydroid.bootstrap.settings.SettingsModule
import com.pyamsoft.pydroid.ui.app.ComposeThemeFactory

internal interface ResetComponent {

  fun inject(dialog: ResetDialog)

  interface Factory {

    @CheckResult fun create(): ResetComponent

    data class Parameters
    internal constructor(
        internal val module: SettingsModule,
        internal val composeTheme: ComposeThemeFactory,
    )
  }

  class Impl internal constructor(private val params: Factory.Parameters) : ResetComponent {

    override fun inject(dialog: ResetDialog) {
      dialog.composeTheme = params.composeTheme
      dialog.viewModel =
          ResetViewModeler(
              state = MutableResetViewState(),
              interactor = params.module.provideInteractor(),
          )
    }

    class FactoryImpl internal constructor(private val params: Factory.Parameters) : Factory {

      override fun create(): ResetComponent {
        return Impl(params)
      }
    }
  }
}
