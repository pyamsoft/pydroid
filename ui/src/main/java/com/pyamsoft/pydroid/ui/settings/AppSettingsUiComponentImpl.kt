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

import android.content.ActivityNotFoundException
import android.os.Bundle
import androidx.lifecycle.LifecycleOwner
import com.pyamsoft.pydroid.arch.BaseUiComponent
import com.pyamsoft.pydroid.arch.doOnDestroy
import com.pyamsoft.pydroid.ui.arch.InvalidIdException
import com.pyamsoft.pydroid.ui.navigation.NavigationViewModel
import com.pyamsoft.pydroid.ui.rating.RatingViewModel
import com.pyamsoft.pydroid.ui.settings.AppSettingsViewModel.SettingsState
import com.pyamsoft.pydroid.ui.version.VersionCheckViewModel
import com.pyamsoft.pydroid.util.HyperlinkIntent
import timber.log.Timber

internal class AppSettingsUiComponentImpl internal constructor(
  private val settingsView: AppSettingsView,
  private val versionViewModel: VersionCheckViewModel,
  private val ratingViewModel: RatingViewModel,
  private val settingsViewModel: AppSettingsViewModel,
  private val navigationViewModel: NavigationViewModel
) : BaseUiComponent<AppSettingsUiComponent.Callback>(),
    AppSettingsUiComponent {

  override fun id(): Int {
    throw InvalidIdException
  }

  override fun onBind(
    owner: LifecycleOwner,
    savedInstanceState: Bundle?,
    callback: AppSettingsUiComponent.Callback
  ) {
    owner.doOnDestroy {
      settingsView.teardown()
      versionViewModel.unbind()
      settingsViewModel.unbind()
    }

    settingsView.inflate(savedInstanceState)
    settingsViewModel.bind { state, oldState ->
      renderMoreApps(state, oldState)
      renderShowUpgrade(state, oldState)
      renderDarkTheme(state, oldState)
      renderNavigationLinks(state, oldState)
      renderClearData(state, oldState)
      renderCheckForUpdates(state, oldState)
      renderShowLicenses(state, oldState)
      renderRate(state, oldState)
    }
    versionViewModel.bind { _, _ ->
      Timber.d("VersionState render handled by VersionCheckActivity")
    }
  }

  override fun onSaveState(outState: Bundle) {
    settingsView.saveState(outState)
  }

  override fun failedNavigation(error: ActivityNotFoundException) {
    navigationViewModel.failedNavigation(error)
  }

  private fun renderMoreApps(
    state: SettingsState,
    oldState: SettingsState?
  ) {
    state.renderOnChange(oldState, value = { it.navigateMoreApps }) { navigate ->
      if (navigate) {
        callback.onViewMorePyamsoftApps()
      }
    }
  }

  private fun renderShowUpgrade(
    state: SettingsState,
    oldState: SettingsState?
  ) {
    state.renderOnChange(oldState, value = { it.showUpgradeInfo }) { show ->
      if (show) {
        ratingViewModel.load(true)
      }
    }
  }

  private fun renderDarkTheme(
    state: SettingsState,
    oldState: SettingsState?
  ) {
    state.renderOnChange(oldState, value = { it.darkTheme }) { dark ->
      if (dark != null) {
        callback.onDarkThemeChanged(dark.dark)
      }
    }
  }

  private fun renderNavigationLinks(
    state: SettingsState,
    oldState: SettingsState?
  ) {
    renderLink(state, oldState) { it.socialMediaLink }
    renderLink(state, oldState) { it.blogLink }
    renderLink(state, oldState) { it.bugReportLink }
  }

  private inline fun renderLink(
    state: SettingsState,
    oldState: SettingsState?,
    crossinline value: (state: SettingsState) -> HyperlinkIntent?
  ) {
    state.renderOnChange(oldState, value) { link ->
      if (link != null) {
        callback.onNavigateToLink(link)
      }
    }
  }

  private fun renderClearData(
    state: SettingsState,
    oldState: SettingsState?
  ) {
    state.renderOnChange(oldState, value = { it.clearAppData }) { clear ->
      if (clear) {
        callback.onClearAppData()
      }
    }
  }

  private fun renderCheckForUpdates(
    state: SettingsState,
    oldState: SettingsState?
  ) {
    state.renderOnChange(oldState, value = { it.checkForUpdate }) { check ->
      if (check) {
        versionViewModel.checkForUpdates(true)
      }
    }
  }

  private fun renderShowLicenses(
    state: SettingsState,
    oldState: SettingsState?
  ) {
    state.renderOnChange(oldState, value = { it.showLicenses }) { show ->
      if (show) {
        callback.onViewLicenses()
      }
    }
  }

  private fun renderRate(
    state: SettingsState,
    oldState: SettingsState?
  ) {
    state.renderOnChange(oldState, value = { it.navigateRateApp }) { rate ->
      if (rate) {
        callback.onRateApp()
      }
    }
  }

}
