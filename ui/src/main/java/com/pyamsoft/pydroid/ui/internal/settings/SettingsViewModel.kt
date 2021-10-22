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

package com.pyamsoft.pydroid.ui.internal.settings

import androidx.lifecycle.viewModelScope
import com.pyamsoft.highlander.highlander
import com.pyamsoft.pydroid.arch.UiViewModel
import com.pyamsoft.pydroid.bootstrap.otherapps.OtherAppsInteractor
import com.pyamsoft.pydroid.bootstrap.otherapps.api.OtherApp
import com.pyamsoft.pydroid.core.Logger
import com.pyamsoft.pydroid.core.ResultWrapper
import com.pyamsoft.pydroid.ui.theme.Theming
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

internal class SettingsViewModel
internal constructor(
    private val bugReportUrl: String,
    private val viewSourceUrl: String,
    private val privacyPolicyUrl: String,
    private val termsConditionsUrl: String,
    hideClearAll: Boolean,
    hideUpgradeInformation: Boolean,
    private val theming: Theming,
    interactor: OtherAppsInteractor,
) :
    UiViewModel<SettingsViewState, SettingsControllerEvent>(
        initialState =
            SettingsViewState(
                hideClearAll = hideClearAll,
                hideUpgradeInformation = hideUpgradeInformation,
                applicationName = "",
                darkMode = Theming.Mode.SYSTEM,
                otherApps = emptyList(),
                navigationError = null,
                isLoading = false,
            ),
    ) {

  private val otherAppsRunner =
      highlander<ResultWrapper<List<OtherApp>>, Boolean> { force -> interactor.getApps(force) }

  init {
    viewModelScope.launch(context = Dispatchers.Default) {
      otherAppsRunner
          .call(false)
          .onSuccess { setState { copy(otherApps = it) } }
          .onFailure { Logger.e(it, "Failed to fetch other apps from network") }
          .onFailure { setState { copy(otherApps = emptyList()) } }
    }

    viewModelScope.launch(context = Dispatchers.Default) {
      val name = interactor.getDisplayName()
      setState { copy(applicationName = name) }
    }
  }

  internal fun handleViewMoreApps() {
    state.otherApps.let { others ->
      if (others.isEmpty()) {
        Logger.w("Other apps list is empty, fallback to developer store page")
        publish(SettingsControllerEvent.NavigateDeveloperPage)
      } else {
        Logger.w("We have a list of Other apps, show them")
        publish(SettingsControllerEvent.OpenOtherAppsScreen(others))
      }
    }
  }

  internal fun handleLoadPreferences(scope: CoroutineScope) {
    scope.setState(
        stateChange = { copy(isLoading = true) },
        andThen = {
          setState {
            copy(
                darkMode = theming.getMode(),
                isLoading = false,
            )
          }
        })
  }

  internal fun handleChangeDarkMode(scope: CoroutineScope, mode: Theming.Mode) {
    scope.setState(
        stateChange = { copy(darkMode = mode) },
        andThen = { theming.setDarkTheme(mode) },
    )
  }

  internal fun handleClearNavigationError() {
    setState { copy(navigationError = null) }
  }

  internal fun handleNavigationFailed(error: Throwable) {
    setState { copy(navigationError = error) }
  }

  internal fun handleNavigationSuccess() {
    setState { copy(navigationError = null) }
  }

  internal fun handleViewSocialMedia() {
    publish(SettingsControllerEvent.NavigateHyperlink(FACEBOOK))
  }

  internal fun handleViewBlog() {
    publish(SettingsControllerEvent.NavigateHyperlink(BLOG))
  }

  internal fun handleViewTermsOfService() {
    publish(SettingsControllerEvent.NavigateHyperlink(termsConditionsUrl))
  }

  internal fun handleViewPrivacyPolicy() {
    publish(SettingsControllerEvent.NavigateHyperlink(privacyPolicyUrl))
  }

  internal fun handleViewSourceCode() {
    publish(SettingsControllerEvent.NavigateHyperlink(viewSourceUrl))
  }

  internal fun handleReportBug() {
    publish(SettingsControllerEvent.NavigateHyperlink(bugReportUrl))
  }

  companion object {

    private const val FACEBOOK = "https://www.facebook.com/pyamsoftware"
    private const val BLOG = "https://pyamsoft.blogspot.com/"
  }
}
