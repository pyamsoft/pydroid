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
import com.pyamsoft.pydroid.ui.navigation.FailedNavigationBinder
import com.pyamsoft.pydroid.ui.rating.RatingBinder
import com.pyamsoft.pydroid.ui.version.VersionCheckPresenter
import com.pyamsoft.pydroid.ui.version.VersionCheckPresenter.VersionState
import com.pyamsoft.pydroid.util.HyperlinkIntent
import timber.log.Timber

internal class AppSettingsUiComponentImpl internal constructor(
  private val settingsView: AppSettingsView,
  private val versionPresenter: VersionCheckPresenter,
  private val ratingBinder: RatingBinder,
  private val settingsBinder: AppSettingsBinder,
  private val failedNavBinder: FailedNavigationBinder
) : BaseUiComponent<AppSettingsUiComponent.Callback>(),
    AppSettingsUiComponent,
    AppSettingsBinder.Callback,
    VersionCheckPresenter.Callback {

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
      versionPresenter.unbind()
      settingsBinder.unbind()
    }

    settingsView.inflate(savedInstanceState)
    versionPresenter.bind(this)
    settingsBinder.bind(this)
  }

  override fun onSaveState(outState: Bundle) {
    settingsView.saveState(outState)
  }

  override fun failedNavigation(error: ActivityNotFoundException) {
    failedNavBinder.failedNavigation(error)
  }

  override fun handleViewMorePyamsoftApps() {
    callback.onViewMorePyamsoftApps()
  }

  override fun handleShowUpgradeInfo() {
    ratingBinder.load(true)
  }

  override fun handleDarkThemeChanged(dark: Boolean) {
    callback.onDarkThemeChanged(dark)
  }

  override fun handleShowSocialMedia(link: HyperlinkIntent) {
    callback.onNavigateToLink(link)
  }

  override fun handleClearAppData() {
    callback.onClearAppData()
  }

  override fun handleCheckUpgrade() {
    versionPresenter.checkForUpdates(true)
  }

  override fun handleViewLicenses() {
    callback.onViewLicenses()
  }

  override fun handleOpenBugReport(link: HyperlinkIntent) {
    callback.onNavigateToLink(link)
  }

  override fun handleRateApp() {
    callback.onRateApp()
  }

  override fun handleShowBlog(link: HyperlinkIntent) {
    callback.onNavigateToLink(link)
  }

  override fun onRender(
    state: VersionState,
    oldState: VersionState?
  ) {
    Timber.d("VersionState onRender handled by VersionCheckActivity")
  }

}
