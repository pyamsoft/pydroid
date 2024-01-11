/*
 * Copyright 2024 pyamsoft
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

package com.pyamsoft.pydroid.ui.internal.changelog

import androidx.annotation.CheckResult
import com.pyamsoft.pydroid.bootstrap.changelog.ChangeLogModule
import com.pyamsoft.pydroid.ui.changelog.ShowUpdateChangeLog

internal interface ChangeLogComponent {

  fun inject(component: ShowUpdateChangeLog)

  interface Factory {

    @CheckResult fun create(): ChangeLogComponent

    data class Parameters
    internal constructor(
        internal val changeLogModule: ChangeLogModule,
        internal val state: MutableChangeLogViewState,
    )
  }

  class Impl
  private constructor(
      private val params: Factory.Parameters,
  ) : ChangeLogComponent {

    override fun inject(component: ShowUpdateChangeLog) {
      component.viewModel =
          ChangeLogViewModeler(
              interactor = params.changeLogModule.provideInteractor(),
              state = params.state,
          )
    }

    internal class FactoryImpl
    internal constructor(
        private val params: Factory.Parameters,
    ) : Factory {

      override fun create(): ChangeLogComponent {
        return Impl(params)
      }
    }
  }
}
