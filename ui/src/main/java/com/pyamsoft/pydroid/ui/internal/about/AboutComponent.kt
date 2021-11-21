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

package com.pyamsoft.pydroid.ui.internal.about

import androidx.annotation.CheckResult
import com.pyamsoft.pydroid.bootstrap.about.AboutInteractor
import com.pyamsoft.pydroid.bootstrap.libraries.OssLibraries
import com.pyamsoft.pydroid.ui.app.ComposeThemeFactory

internal interface AboutComponent {

  fun inject(dialog: AboutDialog)

  interface Factory {

    @CheckResult fun create(): AboutComponent

    data class Parameters
    internal constructor(
        internal val composeTheme: ComposeThemeFactory,
        internal val interactor: AboutInteractor,
    )
  }

  class Impl private constructor(private val params: Factory.Parameters) : AboutComponent {

    override fun inject(dialog: AboutDialog) {
      dialog.composeTheme = params.composeTheme
      dialog.viewModel =
          AboutViewModeler(
              state = MutableAboutViewState(),
              interactor = params.interactor,
          )
    }

    class FactoryImpl internal constructor(private val params: Factory.Parameters) : Factory {

      override fun create(): AboutComponent {
        OssLibraries.usingUi = true
        return Impl(params)
      }
    }
  }
}
