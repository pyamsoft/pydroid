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

import com.pyamsoft.pydroid.ui.app.BaseView
import com.pyamsoft.pydroid.util.HyperlinkIntent

interface SettingsPreferenceView : BaseView {

  fun onMoreAppsClicked(onClick: () -> Unit)

  fun onFollowsClicked(
    onBlogClicked: (blogLink: HyperlinkIntent) -> Unit,
    onSocialClicked: (socialLink: HyperlinkIntent) -> Unit
  )

  fun onRateAppClicked(onClick: () -> Unit)

  fun onBugReportClicked(onClick: (report: HyperlinkIntent) -> Unit)

  fun onLicensesClicked(onClick: () -> Unit)

  fun onCheckVersionClicked(onClick: () -> Unit)

  fun onClearAllClicked(onClick: () -> Unit)

  fun onUpgradeClicked(onClick: () -> Unit)

  fun onDarkThemeClicked(onClick: (dark: Boolean) -> Unit)

}