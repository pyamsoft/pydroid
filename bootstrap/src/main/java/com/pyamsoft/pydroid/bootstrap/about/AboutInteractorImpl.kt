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

package com.pyamsoft.pydroid.bootstrap.about

import androidx.annotation.CheckResult
import com.pyamsoft.pydroid.bootstrap.libraries.OssLibraries
import com.pyamsoft.pydroid.bootstrap.libraries.OssLibrary
import com.pyamsoft.pydroid.core.threads.Enforcer

internal class AboutInteractorImpl internal constructor(
  private val enforcer: Enforcer
) : AboutInteractor {

  @CheckResult
  private fun createLicenseStream(): Set<OssLibrary> {
    enforcer.assertNotOnMainThread()
    return OssLibraries.libraries()
  }

  override suspend fun loadLicenses(bypass: Boolean): List<OssLibrary> {
    return createLicenseStream().sortedBy { it.name.toLowerCase() }
  }
}
