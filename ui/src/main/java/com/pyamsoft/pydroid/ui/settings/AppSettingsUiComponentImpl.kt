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
import com.pyamsoft.pydroid.ui.navigation.FailedNavigationPresenter
import com.pyamsoft.pydroid.ui.rating.RatingPresenter
import com.pyamsoft.pydroid.ui.version.VersionCheckPresenter
import com.pyamsoft.pydroid.util.HyperlinkIntent
import timber.log.Timber

internal class AppSettingsUiComponentImpl internal constructor(
  private val settingsView: AppSettingsView,
  private val versionPresenter: VersionCheckPresenter,
  private val ratingPresenter: RatingPresenter,
  private val settingsPresenter: AppSettingsPresenter,
  private val failedNavPresenter: FailedNavigationPresenter
) : BaseUiComponent<AppSettingsUiComponent.Callback>(),
    AppSettingsUiComponent,
    AppSettingsPresenter.Callback,
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
      settingsPresenter.unbind()
    }

    settingsView.inflate(savedInstanceState)
    versionPresenter.bind(this)
    settingsPresenter.bind(this)
  }

  override fun onSaveState(outState: Bundle) {
    settingsView.saveState(outState)
  }

  override fun failedNavigation(error: ActivityNotFoundException) {
    failedNavPresenter.failedNavigation(error)
  }

  override fun onViewMorePyamsoftApps() {
    callback.onViewMorePyamsoftApps()
  }

  override fun onShowUpgradeInfo() {
    ratingPresenter.load(true)
  }

  override fun onDarkThemeChanged(dark: Boolean) {
    callback.onDarkThemeChanged(dark)
  }

  override fun onShowSocialMedia(link: HyperlinkIntent) {
    callback.onNavigateToLink(link)
  }

  override fun onClearAppData() {
    callback.onClearAppData()
  }

  override fun onCheckUpgrade() {
    versionPresenter.checkForUpdates(true)
  }

  override fun onViewLicenses() {
    callback.onViewLicenses()
  }

  override fun onOpenBugReport(link: HyperlinkIntent) {
    callback.onNavigateToLink(link)
  }

  override fun onRateApp() {
    callback.onRateApp()
  }

  override fun onShowBlog(link: HyperlinkIntent) {
    callback.onNavigateToLink(link)
  }

  override fun onVersionCheckBegin(forced: Boolean) {
    Timber.d("onVersionCheckBegin handled by VersionActivity")
  }

  override fun onVersionCheckFound(
    currentVersion: Int,
    newVersion: Int
  ) {
    Timber.d("onVersionCheckFound handled by VersionActivity")
  }

  override fun onVersionCheckError(throwable: Throwable) {
    Timber.d("onVersionCheckError handled by VersionActivity")
  }

  override fun onVersionCheckComplete() {
    Timber.d("onVersionCheckComplete handled by VersionActivity")
  }

}
