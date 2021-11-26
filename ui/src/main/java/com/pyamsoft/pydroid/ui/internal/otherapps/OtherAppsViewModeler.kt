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

import com.pyamsoft.highlander.highlander
import com.pyamsoft.pydroid.arch.AbstractViewModeler
import com.pyamsoft.pydroid.bootstrap.otherapps.OtherAppsInteractor
import com.pyamsoft.pydroid.bootstrap.otherapps.api.OtherApp
import com.pyamsoft.pydroid.core.ResultWrapper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

internal class OtherAppsViewModeler
internal constructor(
    private val state: MutableOtherAppsViewState,
    interactor: OtherAppsInteractor,
) : AbstractViewModeler<OtherAppsViewState>(state) {

  private val appsRunner =
      highlander<ResultWrapper<List<OtherApp>>, Boolean> { force -> interactor.getApps(force) }

  internal fun bind(scope: CoroutineScope) {
    // This may be cached in many cases
    state.isLoading = true

    scope.launch(context = Dispatchers.Main) {
      appsRunner
          .call(false)
          .onSuccess { state.apps = it }
          .onFailure { state.appsError = it }
          .onFinally { state.isLoading = false }
    }
  }

  internal fun handleNavigationFailed(throwable: Throwable) {
    state.navigationError = throwable
  }

  internal fun handleHideNavigation() {
    state.navigationError = null
  }
}
