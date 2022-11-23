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

package com.pyamsoft.pydroid.ui.internal.changelog.dialog

import androidx.annotation.CheckResult
import coil.ImageLoader
import com.pyamsoft.pydroid.bootstrap.changelog.ChangeLogModule
import com.pyamsoft.pydroid.ui.app.ComposeThemeFactory
import com.pyamsoft.pydroid.ui.internal.changelog.ChangeLogProvider

internal interface ChangeLogComponent {

  fun inject(dialog: ChangeLogDialog)

  interface Factory {

    @CheckResult fun create(provider: ChangeLogProvider): ChangeLogComponent

    data class Parameters
    internal constructor(
        internal val changeLogModule: ChangeLogModule,
        internal val composeTheme: ComposeThemeFactory,
        internal val imageLoader: ImageLoader,
        internal val version: Int,
    )
  }

  class Impl
  private constructor(
      private val provider: ChangeLogProvider,
      private val params: Factory.Parameters,
  ) : ChangeLogComponent {

    override fun inject(dialog: ChangeLogDialog) {
      dialog.composeTheme = params.composeTheme
      dialog.imageLoader = params.imageLoader
      dialog.viewModel =
          ChangeLogDialogViewModeler(
              state = MutableChangeLogDialogViewState(),
              interactor = params.changeLogModule.provideInteractor(),
              provider = provider,
              version = params.version,
          )
    }

    internal class FactoryImpl internal constructor(private val params: Factory.Parameters) :
        Factory {

      override fun create(provider: ChangeLogProvider): ChangeLogComponent {
        return Impl(provider, params)
      }
    }
  }
}
