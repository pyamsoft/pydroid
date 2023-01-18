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

package com.pyamsoft.pydroid.ui.internal.about

import com.pyamsoft.highlander.highlander
import com.pyamsoft.pydroid.arch.AbstractViewModeler
import com.pyamsoft.pydroid.bootstrap.about.AboutInteractor
import com.pyamsoft.pydroid.bootstrap.libraries.OssLibrary
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

internal class AboutViewModeler
internal constructor(
    override val state: MutableAboutViewState,
    interactor: AboutInteractor,
) : AbstractViewModeler<AboutViewState>(state) {

  private val licenseRunner = highlander<List<OssLibrary>> { interactor.loadLicenses() }

  internal fun handleLoadLicenses(scope: CoroutineScope) {
    val s = state

    if (s.loadingState.value != AboutViewState.LoadingState.NONE) {
      return
    }

    s.loadingState.value = AboutViewState.LoadingState.LOADING
    scope.launch(context = Dispatchers.Main) {
      s.apply {
        licenses.value = licenseRunner.call()
        loadingState.value = AboutViewState.LoadingState.DONE
      }
    }
  }

  internal fun handleFailedNavigation(e: Throwable) {
    state.navigationError.value = e
  }

  internal fun handleDismissFailedNavigation() {
    state.navigationError.value = null
  }
}
