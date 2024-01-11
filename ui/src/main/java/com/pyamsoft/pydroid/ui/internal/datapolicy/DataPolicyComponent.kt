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

package com.pyamsoft.pydroid.ui.internal.datapolicy

import androidx.annotation.CheckResult
import com.pyamsoft.pydroid.bootstrap.datapolicy.DataPolicyModule
import com.pyamsoft.pydroid.ui.datapolicy.ShowDataPolicy

internal interface DataPolicyComponent {

  fun inject(delegate: ShowDataPolicy)

  interface Factory {

    @CheckResult fun create(): DataPolicyComponent

    data class Parameters
    internal constructor(
        internal val state: MutableDataPolicyViewState,
        internal val module: DataPolicyModule,
    )
  }

  class Impl
  private constructor(
      private val params: Factory.Parameters,
  ) : DataPolicyComponent {

    override fun inject(delegate: ShowDataPolicy) {
      delegate.viewModel =
          DataPolicyViewModeler(
              state = params.state,
              interactor = params.module.provideInteractor(),
          )
    }

    internal class FactoryImpl
    internal constructor(
        private val params: Factory.Parameters,
    ) : Factory {

      override fun create(): DataPolicyComponent {
        return Impl(params)
      }
    }
  }
}
