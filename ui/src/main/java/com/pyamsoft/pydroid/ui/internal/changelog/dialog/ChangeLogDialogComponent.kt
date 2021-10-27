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

package com.pyamsoft.pydroid.ui.internal.changelog.dialog

import androidx.annotation.CheckResult
import coil.ImageLoader
import com.pyamsoft.pydroid.arch.createViewModelFactory
import com.pyamsoft.pydroid.bootstrap.changelog.ChangeLogInteractor
import com.pyamsoft.pydroid.ui.app.ComposeThemeFactory
import com.pyamsoft.pydroid.ui.internal.changelog.ChangeLogProvider

internal interface ChangeLogDialogComponent {

  fun inject(dialog: ChangeLogDialog)

  interface Factory {

    @CheckResult fun create(provider: ChangeLogProvider): ChangeLogDialogComponent

    data class Parameters
    internal constructor(
        internal val interactor: ChangeLogInteractor,
        internal val composeTheme: ComposeThemeFactory,
        internal val imageLoader: ImageLoader,
    )
  }

  class Impl
  private constructor(
      private val provider: ChangeLogProvider,
      private val params: Factory.Parameters,
  ) : ChangeLogDialogComponent {

    private val factory = createViewModelFactory {
      ChangeLogDialogViewModel(params.interactor, provider)
    }

    override fun inject(dialog: ChangeLogDialog) {
      dialog.composeTheme = params.composeTheme
      dialog.imageLoader = params.imageLoader
      dialog.factory = factory
    }

    internal class FactoryImpl internal constructor(private val params: Factory.Parameters) :
        Factory {

      override fun create(provider: ChangeLogProvider): ChangeLogDialogComponent {
        return Impl(provider, params)
      }
    }
  }
}
