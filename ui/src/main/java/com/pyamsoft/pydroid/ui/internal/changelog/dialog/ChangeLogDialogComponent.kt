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

package com.pyamsoft.pydroid.ui.internal.changelog.dialog

import androidx.annotation.CheckResult
import coil3.ImageLoader
import com.pyamsoft.pydroid.bootstrap.changelog.ChangeLogModule
import com.pyamsoft.pydroid.ui.changelog.ChangeLogProvider

internal interface ChangeLogDialogComponent {

  fun inject(injector: ChangeLogDialogInjector)

  interface Factory {

    @CheckResult fun create(provider: ChangeLogProvider): ChangeLogDialogComponent

    @ConsistentCopyVisibility
    data class Parameters
    internal constructor(
        internal val state: MutableChangeLogDialogViewState,
        internal val changeLogModule: ChangeLogModule,
        internal val imageLoader: ImageLoader,
        internal val version: Int,
    )
  }

  class Impl
  private constructor(
      private val provider: ChangeLogProvider,
      private val params: Factory.Parameters,
  ) : ChangeLogDialogComponent {

    override fun inject(injector: ChangeLogDialogInjector) {
      injector.imageLoader = params.imageLoader
      injector.viewModel =
          ChangeLogDialogViewModeler(
              state = params.state,
              interactor = params.changeLogModule.provideInteractor(),
              provider = provider,
              version = params.version,
          )
    }

    internal class FactoryImpl
    internal constructor(
        private val params: Factory.Parameters,
    ) : Factory {

      override fun create(provider: ChangeLogProvider): ChangeLogDialogComponent {
        return Impl(provider, params)
      }
    }
  }
}
