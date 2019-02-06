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

import com.pyamsoft.pydroid.util.HyperlinkIntent

internal sealed class AppSettingsViewEvent : ViewEvent {

  object RateAppClicked : AppSettingsViewEvent()

  object MoreAppsClicked : AppSettingsViewEvent()

  data class FollowSocialClicked(val link: HyperlinkIntent) : AppSettingsViewEvent()

  data class FollowBlogClicked(val link: HyperlinkIntent) : AppSettingsViewEvent()

  data class BugReportClicked(val link: HyperlinkIntent) : AppSettingsViewEvent()

  object LicenseClicked : AppSettingsViewEvent()

  object CheckUpgrade : AppSettingsViewEvent()

  object ClearAppData : AppSettingsViewEvent()

  object ShowUpgradeInfo : AppSettingsViewEvent()

  data class DarkTheme(val dark: Boolean) : AppSettingsViewEvent()
}