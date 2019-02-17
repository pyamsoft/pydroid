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

import com.pyamsoft.pydroid.core.bus.RxBus
import com.pyamsoft.pydroid.ui.arch.BasePresenter
import com.pyamsoft.pydroid.ui.theme.Theming
import com.pyamsoft.pydroid.util.HyperlinkIntent

internal class AppSettingsPresenterImpl internal constructor(
  private val theming: Theming
) : BasePresenter<Unit, AppSettingsPresenter.Callback>(RxBus.empty()),
    AppSettingsPresenter, AppSettingsView.Callback {

  override fun onBind() {
  }

  override fun onUnbind() {
  }

  override fun onMoreAppsClicked() {
    callback.onViewMorePyamsoftApps()
  }

  override fun onShowUpgradeInfoClicked() {
    callback.onShowUpgradeInfo()
  }

  override fun onDarkThemeToggled(dark: Boolean) {
    theming.setDarkTheme(dark)
  }

  override fun onFollowSocialClicked(link: HyperlinkIntent) {
    callback.onShowSocialMedia(link)
  }

  override fun onClearAppDataClicked() {
    callback.onClearAppData()
  }

  override fun onCheckUpgradeClicked() {
    callback.onCheckUpgrade()
  }

  override fun onViewLicensesClicked() {
    callback.onViewLicenses()
  }

  override fun onBugReportClicked(link: HyperlinkIntent) {
    callback.onOpenBugReport(link)
  }

  override fun onRateAppClicked() {
    callback.onRateApp()
  }

  override fun onFollowBlogClicked(link: HyperlinkIntent) {
    callback.onShowBlog(link)
  }

}
