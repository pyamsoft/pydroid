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

import com.pyamsoft.pydroid.arch.UiBinder
import com.pyamsoft.pydroid.ui.theme.Theming
import com.pyamsoft.pydroid.util.HyperlinkIntent

internal class AppSettingsBinder internal constructor(
  private val theming: Theming
) : UiBinder<AppSettingsBinder.Callback>(),
    AppSettingsView.Callback {

  override fun onBind() {
  }

  override fun onUnbind() {
  }

  override fun onMoreAppsClicked() {
    callback.handleViewMorePyamsoftApps()
  }

  override fun onShowUpgradeInfoClicked() {
    callback.handleShowUpgradeInfo()
  }

  override fun onDarkThemeToggled(dark: Boolean) {
    theming.setDarkTheme(dark) { callback.handleDarkThemeChanged(it) }
  }

  override fun onFollowSocialClicked(link: HyperlinkIntent) {
    callback.handleShowSocialMedia(link)
  }

  override fun onClearAppDataClicked() {
    callback.handleClearAppData()
  }

  override fun onCheckUpgradeClicked() {
    callback.handleCheckUpgrade()
  }

  override fun onViewLicensesClicked() {
    callback.handleViewLicenses()
  }

  override fun onBugReportClicked(link: HyperlinkIntent) {
    callback.handleOpenBugReport(link)
  }

  override fun onRateAppClicked() {
    callback.handleRateApp()
  }

  override fun onFollowBlogClicked(link: HyperlinkIntent) {
    callback.handleShowBlog(link)
  }

  interface Callback : UiBinder.Callback {

    fun handleViewMorePyamsoftApps()

    fun handleShowUpgradeInfo()

    fun handleDarkThemeChanged(dark: Boolean)

    fun handleShowSocialMedia(link: HyperlinkIntent)

    fun handleClearAppData()

    fun handleCheckUpgrade()

    fun handleViewLicenses()

    fun handleOpenBugReport(link: HyperlinkIntent)

    fun handleRateApp()

    fun handleShowBlog(link: HyperlinkIntent)
  }

}
