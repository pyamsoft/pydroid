/*
 * Copyright 2019 Peter Kenji Yamanaka
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
 *
 */

package com.pyamsoft.pydroid.ui.settings

import com.pyamsoft.pydroid.arch.UiState
import com.pyamsoft.pydroid.arch.UiViewModel
import com.pyamsoft.pydroid.ui.settings.AppSettingsViewModel.SettingsState
import com.pyamsoft.pydroid.ui.settings.AppSettingsViewModel.SettingsState.DarkTheme
import com.pyamsoft.pydroid.ui.theme.Theming
import com.pyamsoft.pydroid.util.HyperlinkIntent

internal class AppSettingsViewModel internal constructor(
  private val handler: AppSettingsHandler,
  private val theming: Theming
) : UiViewModel<SettingsState>(
    initialState = SettingsState(
        blogLink = null,
        bugReportLink = null,
        navigateMoreApps = false,
        navigateRateApp = false,
        socialMediaLink = null,
        checkForUpdate = false,
        clearAppData = false,
        showLicenses = false,
        showUpgradeInfo = false,
        darkTheme = null
    )
), AppSettingsView.Callback {

  override fun onBind() {
    handler.handle(this)
        .disposeOnDestroy()
  }

  override fun onUnbind() {
  }

  override fun onMoreAppsClicked() {
    setUniqueState(true, old = { it.navigateMoreApps }) { state, value ->
      state.copy(navigateMoreApps = value)
    }
  }

  override fun onShowUpgradeInfoClicked() {
    setUniqueState(true, old = { it.showUpgradeInfo }) { state, value ->
      state.copy(showUpgradeInfo = value)
    }
  }

  override fun onDarkThemeToggled(dark: Boolean) {
    theming.setDarkTheme(dark) { value ->
      setState { copy(darkTheme = DarkTheme(value)) }
    }
  }

  override fun onFollowSocialClicked(link: HyperlinkIntent) {
    setUniqueState(link, old = { it.socialMediaLink }) { state, value ->
      state.copy(socialMediaLink = value)
    }
  }

  override fun onClearAppDataClicked() {
    setUniqueState(true, old = { it.clearAppData }) { state, value ->
      state.copy(clearAppData = value)
    }
  }

  override fun onCheckUpgradeClicked() {
    setUniqueState(true, old = { it.checkForUpdate }) { state, value ->
      state.copy(checkForUpdate = value)
    }
  }

  override fun onViewLicensesClicked() {
    setUniqueState(true, old = { it.showLicenses }) { state, value ->
      state.copy(showLicenses = value)
    }
  }

  override fun onBugReportClicked(link: HyperlinkIntent) {
    setUniqueState(link, old = { it.bugReportLink }) { state, value ->
      state.copy(bugReportLink = value)
    }
  }

  override fun onRateAppClicked() {
    setUniqueState(true, old = { it.navigateRateApp }) { state, value ->
      state.copy(navigateRateApp = value)
    }
  }

  override fun onFollowBlogClicked(link: HyperlinkIntent) {
    setUniqueState(link, old = { it.blogLink }) { state, value ->
      state.copy(blogLink = value)
    }
  }

  data class SettingsState(
    val navigateMoreApps: Boolean,
    val showUpgradeInfo: Boolean,
    val clearAppData: Boolean,
    val checkForUpdate: Boolean,
    val showLicenses: Boolean,
    val navigateRateApp: Boolean,
    val bugReportLink: HyperlinkIntent?,
    val blogLink: HyperlinkIntent?,
    val socialMediaLink: HyperlinkIntent?,
    val darkTheme: DarkTheme?
  ) : UiState {
    data class DarkTheme(val dark: Boolean)
  }

}
