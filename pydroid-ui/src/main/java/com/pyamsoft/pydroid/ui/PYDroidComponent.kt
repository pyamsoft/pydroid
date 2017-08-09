/*
 * Copyright 2017 Peter Kenji Yamanaka
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
 */

package com.pyamsoft.pydroid.ui

import android.support.annotation.CheckResult
import android.support.annotation.RestrictTo
import com.pyamsoft.pydroid.ui.about.AboutLibrariesFragment
import com.pyamsoft.pydroid.ui.about.AboutPagerFragment
import com.pyamsoft.pydroid.ui.app.fragment.AppComponent
import com.pyamsoft.pydroid.ui.rating.RatingDialog
import com.pyamsoft.pydroid.ui.social.Linker
import com.pyamsoft.pydroid.ui.util.AnimUtil
import com.pyamsoft.pydroid.ui.version.VersionCheckComponent

@RestrictTo(RestrictTo.Scope.LIBRARY) internal interface PYDroidComponent {

  fun inject(fragment: AboutPagerFragment)

  fun inject(fragment: AboutLibrariesFragment)

  fun inject(animUtil: AnimUtil)

  fun inject(linker: Linker)

  fun inject(launcher: RatingDialog.Launcher)

  fun inject(ratingDialog: RatingDialog)

  @CheckResult fun plusVersionCheckComponent(packageName: String,
      currentVersion: Int): VersionCheckComponent

  @CheckResult fun plusAppComponent(packageName: String,
      currentVersion: Int): AppComponent
}
