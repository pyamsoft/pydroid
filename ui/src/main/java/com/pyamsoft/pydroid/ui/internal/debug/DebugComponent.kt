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

package com.pyamsoft.pydroid.ui.internal.debug

import androidx.annotation.CheckResult

internal interface DebugComponent {

  fun inject(injector: DebugInjector)

  interface Factory {

    @CheckResult fun create(): DebugComponent

    data class Parameters
    internal constructor(
        internal val state: MutableDebugViewState,
        internal val preferences: DebugPreferences,
        internal val interactor: DebugInteractor,
    )
  }

  class Impl
  private constructor(
      private val params: Factory.Parameters,
  ) : DebugComponent {

    override fun inject(injector: DebugInjector) {
      injector.viewModel =
          DebugViewModeler(
              state = params.state,
              preferences = params.preferences,
              interactor = params.interactor,
          )
    }

    internal class FactoryImpl
    internal constructor(
        private val params: Factory.Parameters,
    ) : Factory {

      override fun create(): DebugComponent {
        return Impl(params)
      }
    }
  }
}
