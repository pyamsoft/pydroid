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
import com.pyamsoft.pydroid.bootstrap.about.AboutLibrariesModule
import com.pyamsoft.pydroid.bootstrap.rating.RatingModule
import com.pyamsoft.pydroid.bootstrap.version.VersionCheckModule
import com.pyamsoft.pydroid.core.threads.Enforcer
import com.pyamsoft.pydroid.loader.LoaderModule

interface ModuleProvider {

  @CheckResult
  fun enforcer(): Enforcer

  @CheckResult
  fun loaderModule(): LoaderModule

  @CheckResult
  fun ratingModule(): RatingModule

  @CheckResult
  fun aboutLibrariesModule(): AboutLibrariesModule

  @CheckResult
  fun versionCheckModule(): VersionCheckModule

}
