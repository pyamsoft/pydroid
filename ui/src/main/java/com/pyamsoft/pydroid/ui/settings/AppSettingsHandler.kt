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

import androidx.annotation.CheckResult
import com.pyamsoft.pydroid.arch.UiEventHandler
import com.pyamsoft.pydroid.bootstrap.SchedulerProvider
import com.pyamsoft.pydroid.core.bus.EventBus
import com.pyamsoft.pydroid.ui.settings.AppSettingsHandler.AppSettingsEvent
import com.pyamsoft.pydroid.ui.settings.AppSettingsHandler.AppSettingsEvent.BugReport
import com.pyamsoft.pydroid.ui.settings.AppSettingsHandler.AppSettingsEvent.CheckUpgrade
import com.pyamsoft.pydroid.ui.settings.AppSettingsHandler.AppSettingsEvent.ClearData
import com.pyamsoft.pydroid.ui.settings.AppSettingsHandler.AppSettingsEvent.DarkTheme
import com.pyamsoft.pydroid.ui.settings.AppSettingsHandler.AppSettingsEvent.FollowBlog
import com.pyamsoft.pydroid.ui.settings.AppSettingsHandler.AppSettingsEvent.FollowSocial
import com.pyamsoft.pydroid.ui.settings.AppSettingsHandler.AppSettingsEvent.MoreApps
import com.pyamsoft.pydroid.ui.settings.AppSettingsHandler.AppSettingsEvent.RateApp
import com.pyamsoft.pydroid.ui.settings.AppSettingsHandler.AppSettingsEvent.ShowInfo
import com.pyamsoft.pydroid.ui.settings.AppSettingsHandler.AppSettingsEvent.ViewLicenses
import com.pyamsoft.pydroid.util.HyperlinkIntent
import io.reactivex.disposables.Disposable

internal class AppSettingsHandler internal constructor(
  private val schedulerProvider: SchedulerProvider,
  bus: EventBus<AppSettingsEvent>
) : UiEventHandler<AppSettingsEvent, AppSettingsView.Callback>(bus),
    AppSettingsView.Callback {

  override fun onMoreAppsClicked() {
    publish(MoreApps)
  }

  override fun onShowUpgradeInfoClicked() {
    publish(ShowInfo)
  }

  override fun onDarkThemeToggled(dark: Boolean) {
    publish(DarkTheme(dark))
  }

  override fun onFollowSocialClicked(link: HyperlinkIntent) {
    publish(FollowSocial(link))
  }

  override fun onClearAppDataClicked() {
    publish(ClearData)
  }

  override fun onCheckUpgradeClicked() {
    publish(CheckUpgrade)
  }

  override fun onViewLicensesClicked() {
    publish(ViewLicenses)
  }

  override fun onBugReportClicked(link: HyperlinkIntent) {
    publish(BugReport(link))
  }

  override fun onRateAppClicked() {
    publish(RateApp)
  }

  override fun onFollowBlogClicked(link: HyperlinkIntent) {
    publish(FollowBlog(link))
  }

  @CheckResult
  override fun handle(delegate: AppSettingsView.Callback): Disposable {
    return listen()
        .subscribeOn(schedulerProvider.backgroundScheduler)
        .observeOn(schedulerProvider.backgroundScheduler)
        .subscribe {
          return@subscribe when (it) {
            is MoreApps -> delegate.onMoreAppsClicked()
            is RateApp -> delegate.onRateAppClicked()
            is ViewLicenses -> delegate.onViewLicensesClicked()
            is CheckUpgrade -> delegate.onCheckUpgradeClicked()
            is ClearData -> delegate.onClearAppDataClicked()
            is ShowInfo -> delegate.onShowUpgradeInfoClicked()
            is FollowBlog -> delegate.onFollowBlogClicked(it.link)
            is BugReport -> delegate.onBugReportClicked(it.link)
            is FollowSocial -> delegate.onFollowSocialClicked(it.link)
            is DarkTheme -> delegate.onDarkThemeToggled(it.dark)
          }
        }
  }

  sealed class AppSettingsEvent {
    object MoreApps : AppSettingsEvent()
    object RateApp : AppSettingsEvent()
    object ViewLicenses : AppSettingsEvent()
    object CheckUpgrade : AppSettingsEvent()
    object ClearData : AppSettingsEvent()
    object ShowInfo : AppSettingsEvent()
    data class FollowBlog(val link: HyperlinkIntent) : AppSettingsEvent()
    data class BugReport(val link: HyperlinkIntent) : AppSettingsEvent()
    data class FollowSocial(val link: HyperlinkIntent) : AppSettingsEvent()
    data class DarkTheme(val dark: Boolean) : AppSettingsEvent()
  }

}
