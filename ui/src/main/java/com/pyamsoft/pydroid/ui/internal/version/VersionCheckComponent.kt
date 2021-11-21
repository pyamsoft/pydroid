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

package com.pyamsoft.pydroid.ui.internal.version

import androidx.annotation.CheckResult
import com.pyamsoft.pydroid.bootstrap.version.VersionModule
import com.pyamsoft.pydroid.ui.app.ComposeThemeFactory
import com.pyamsoft.pydroid.ui.internal.version.upgrade.MutableVersionUpgradeViewState
import com.pyamsoft.pydroid.ui.internal.version.upgrade.VersionUpgradeDialog
import com.pyamsoft.pydroid.ui.internal.version.upgrade.VersionUpgradeViewModeler

internal interface VersionCheckComponent {

  fun inject(dialog: VersionUpgradeDialog)

  interface Factory {

    @CheckResult fun create(): VersionCheckComponent

    data class Parameters
    internal constructor(
        internal val module: VersionModule,
        internal val composeTheme: ComposeThemeFactory,
    )
  }

  class Impl private constructor(private val params: Factory.Parameters) : VersionCheckComponent {

    override fun inject(dialog: VersionUpgradeDialog) {
      dialog.composeTheme = params.composeTheme
      dialog.viewModel =
          VersionUpgradeViewModeler(
              state = MutableVersionUpgradeViewState(),
              interactor = params.module.provideInteractor(),
          )
    }

    internal class FactoryImpl internal constructor(private val params: Factory.Parameters) :
        Factory {

      override fun create(): VersionCheckComponent {
        return Impl(params)
      }
    }
  }
}
