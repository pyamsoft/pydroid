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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.pyamsoft.highlander.highlander
import com.pyamsoft.pydroid.arch.ViewModel
import com.pyamsoft.pydroid.bootstrap.about.AboutInteractor
import com.pyamsoft.pydroid.bootstrap.libraries.OssLibrary
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

internal class AboutViewModel
internal constructor(
    interactor: AboutInteractor,
) : ViewModel<AboutViewState>, AboutViewState {

  private var _isLoading: Boolean by mutableStateOf(false)
  private var _licenses: List<OssLibrary> by mutableStateOf(emptyList())
  private var _navigationError: Throwable? by mutableStateOf(null)

  override val isLoading: Boolean = _isLoading
  override val licenses: List<OssLibrary> = _licenses
  override val navigationError: Throwable? = _navigationError

  private val licenseRunner = highlander<List<OssLibrary>, Boolean> { interactor.loadLicenses(it) }

  @Composable
  override fun state(): AboutViewState {
    return this
  }

  internal fun handleLoadLicenses(scope: CoroutineScope) {
    scope.launch(context = Dispatchers.Main) {
      _isLoading = true
      _licenses = licenseRunner.call(false)
      _isLoading = false
    }
  }

  internal fun handleFailedNavigation(e: Throwable) {
    _navigationError = e
  }

  internal fun handleDismissFailedNavigation() {
    _navigationError = null
  }
}
