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

import com.pyamsoft.highlander.highlander
import com.pyamsoft.pydroid.arch.AbstractViewModeler
import com.pyamsoft.pydroid.bootstrap.otherapps.OtherAppsInteractor
import com.pyamsoft.pydroid.bootstrap.otherapps.api.OtherApp
import com.pyamsoft.pydroid.core.Logger
import com.pyamsoft.pydroid.core.ResultWrapper
import com.pyamsoft.pydroid.ui.theme.Theming
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

internal class SettingsViewModeler
internal constructor(
    private val state: MutableSettingsViewState,
    private val bugReportUrl: String,
    private val viewSourceUrl: String,
    private val privacyPolicyUrl: String,
    private val termsConditionsUrl: String,
    private val theming: Theming,
    private val interactor: OtherAppsInteractor,
) : AbstractViewModeler<SettingsViewState>(state) {

  private val otherAppsRunner =
      highlander<ResultWrapper<List<OtherApp>>, Boolean> { force -> interactor.getApps(force) }

  internal fun bind(scope: CoroutineScope) {
    scope.launch(context = Dispatchers.Main) {
      otherAppsRunner
          .call(false)
          .onSuccess { state.otherApps = it }
          .onFailure { Logger.e(it, "Failed to fetch other apps from network") }
          .onFailure { state.otherApps = emptyList() }
    }

    scope.launch(context = Dispatchers.Main) {
      val name = interactor.getDisplayName()
      state.applicationName = name
    }
  }

  internal fun handleViewMoreApps(
      onOpenDeveloperPage: () -> Unit,
      onOpenOtherApps: (List<OtherApp>) -> Unit,
  ) {
    state.otherApps.let { others ->
      if (others.isEmpty()) {
        Logger.w("Other apps list is empty, fallback to developer store page")
        onOpenDeveloperPage()
      } else {
        Logger.w("We have a list of Other apps, show them")
        onOpenOtherApps(others)
      }
    }
  }

  internal fun handleLoadPreferences(scope: CoroutineScope) {
    scope.launch(context = Dispatchers.Main) {
      state.isLoading = true
      state.darkMode = theming.getMode()
      state.isLoading = false
    }
  }

  internal fun handleChangeDarkMode(
      scope: CoroutineScope,
      mode: Theming.Mode,
      onDarkThemeChanged: () -> Unit,
  ) {
    scope.launch(context = Dispatchers.Main) {
      state.darkMode = mode
      theming.setDarkTheme(mode)
      onDarkThemeChanged()
    }
  }

  internal fun handleClearNavigationError() {
    state.navigationError = null
  }

  internal fun handleNavigationFailed(error: Throwable) {
    state.navigationError = error
  }

  internal fun handleViewSocialMedia(
      onOpenUrl: (String) -> Unit,
  ) {
    onOpenUrl(FACEBOOK)
  }

  internal fun handleViewBlog(onOpenUrl: (String) -> Unit) {
    onOpenUrl(BLOG)
  }

  internal fun handleViewTermsOfService(onOpenUrl: (String) -> Unit) {
    onOpenUrl(termsConditionsUrl)
  }

  internal fun handleViewPrivacyPolicy(onOpenUrl: (String) -> Unit) {
    onOpenUrl(privacyPolicyUrl)
  }

  internal fun handleViewSourceCode(onOpenUrl: (String) -> Unit) {
    onOpenUrl(viewSourceUrl)
  }

  internal fun handleReportBug(onOpenUrl: (String) -> Unit) {
    onOpenUrl(bugReportUrl)
  }

  companion object {

    private const val FACEBOOK = "https://www.facebook.com/pyamsoftware"
    private const val BLOG = "https://pyamsoft.blogspot.com/"
  }
}
