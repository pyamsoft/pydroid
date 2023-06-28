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

package com.pyamsoft.pydroid.ui.internal.about

import com.pyamsoft.pydroid.arch.AbstractViewModeler
import com.pyamsoft.pydroid.bootstrap.about.AboutInteractor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

internal class AboutViewModeler
internal constructor(
    state: MutableAboutViewState,
    private val interactor: AboutInteractor,
) : AbstractViewModeler<AboutViewState>(state) {

  private val vmState = state

  internal fun bind(scope: CoroutineScope) {
    if (vmState.loadingState.value != AboutViewState.LoadingState.NONE) {
      return
    }

    vmState.loadingState.value = AboutViewState.LoadingState.LOADING
    scope.launch(context = Dispatchers.Default) {
      vmState.apply {
        try {
          licenses.value = interactor.loadLicenses()
        } finally {
          loadingState.value = AboutViewState.LoadingState.DONE
        }
      }
    }
  }

  internal fun handleFailedNavigation(e: Throwable) {
    vmState.navigationError.value = e
  }

  internal fun handleDismissFailedNavigation() {
    vmState.navigationError.value = null
  }
}
