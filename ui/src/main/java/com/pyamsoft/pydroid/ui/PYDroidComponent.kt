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

package com.pyamsoft.pydroid.ui

import android.text.SpannedString
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CheckResult
import androidx.lifecycle.LifecycleOwner
import androidx.preference.PreferenceScreen
import com.pyamsoft.pydroid.ui.about.AboutComponent
import com.pyamsoft.pydroid.ui.about.dialog.UrlComponent
import com.pyamsoft.pydroid.ui.about.listitem.AboutItemComponent
import com.pyamsoft.pydroid.ui.rating.RatingActivity
import com.pyamsoft.pydroid.ui.rating.dialog.RatingDialogComponent
import com.pyamsoft.pydroid.ui.settings.AppSettingsComponent
import com.pyamsoft.pydroid.ui.version.VersionComponent
import com.pyamsoft.pydroid.ui.version.upgrade.VersionUpgradeComponent

internal interface PYDroidComponent {

  fun inject(activity: RatingActivity)

  @CheckResult
  fun plusVersionComponent(
    owner: LifecycleOwner,
    view: View
  ): VersionComponent

  @CheckResult
  fun plusVersionUpgradeComponent(
    parent: ViewGroup,
    newVersion: Int
  ): VersionUpgradeComponent

  @CheckResult
  fun plusAboutItemComponent(parent: ViewGroup): AboutItemComponent

  @CheckResult
  fun plusAboutComponent(
    owner: LifecycleOwner,
    parent: ViewGroup
  ): AboutComponent

  @CheckResult
  fun plusSettingsComponent(
    preferenceScreen: PreferenceScreen,
    hideClearAll: Boolean,
    hideUpgradeInformation: Boolean
  ): AppSettingsComponent

  @CheckResult
  fun plusViewLicenseComponent(
    owner: LifecycleOwner,
    parent: ViewGroup,
    link: String,
    name: String
  ): UrlComponent

  @CheckResult
  fun plusRatingDialogComponent(
    parent: ViewGroup,
    rateLink: String,
    changelogIcon: Int,
    changelog: SpannedString
  ): RatingDialogComponent
}
