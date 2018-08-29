/*
 * Copyright (C) 2018 Peter Kenji Yamanaka
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pyamsoft.pydroid.ui

import androidx.annotation.CheckResult
import androidx.lifecycle.LifecycleOwner
import com.pyamsoft.pydroid.core.threads.Enforcer
import com.pyamsoft.pydroid.ui.about.AboutComponent
import com.pyamsoft.pydroid.ui.app.fragment.AppComponent
import com.pyamsoft.pydroid.ui.rating.RatingComponent
import com.pyamsoft.pydroid.ui.social.SocialMediaPreference
import com.pyamsoft.pydroid.ui.version.VersionCheckComponent
import com.pyamsoft.pydroid.ui.version.VersionUpgradeDialog

internal interface PYDroidComponent {

  @CheckResult
  fun enforcer(): Enforcer

  fun inject(layout: SocialMediaPreference)

  fun inject(versionUpgradeDialog: VersionUpgradeDialog)

  @CheckResult
  fun plusVersionCheckComponent(
    owner: LifecycleOwner,
    currentVersion: Int
  ): VersionCheckComponent

  @CheckResult
  fun plusAppComponent(
    owner: LifecycleOwner,
    currentVersion: Int
  ): AppComponent

  @CheckResult
  fun plusRatingComponent(
    owner: LifecycleOwner,
    currentVersion: Int
  ): RatingComponent

  @CheckResult
  fun plusAboutComponent(owner: LifecycleOwner): AboutComponent

}
