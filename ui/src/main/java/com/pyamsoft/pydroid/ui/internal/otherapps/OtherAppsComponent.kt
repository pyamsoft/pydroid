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

package com.pyamsoft.pydroid.ui.internal.otherapps

import androidx.annotation.CheckResult
import coil.ImageLoader
import com.pyamsoft.pydroid.bootstrap.otherapps.OtherAppsModule
import com.pyamsoft.pydroid.ui.app.ComposeThemeFactory

internal interface OtherAppsComponent {

  fun inject(dialog: OtherAppsDialog)

  interface Factory {

    @CheckResult fun create(): OtherAppsComponent

    data class Parameters
    internal constructor(
        internal val module: OtherAppsModule,
        internal val composeTheme: ComposeThemeFactory,
        internal val imageLoader: ImageLoader,
    )
  }

  class Impl private constructor(private val params: Factory.Parameters) : OtherAppsComponent {

    override fun inject(dialog: OtherAppsDialog) {
      dialog.composeTheme = params.composeTheme
      dialog.imageLoader = params.imageLoader
      dialog.viewModel =
          OtherAppsViewModeler(
              state = MutableOtherAppsViewState(),
              interactor = params.module.provideInteractor(),
          )
    }

    class FactoryImpl internal constructor(private val params: Factory.Parameters) : Factory {

      override fun create(): OtherAppsComponent {
        return Impl(params)
      }
    }
  }
}
