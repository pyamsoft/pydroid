/*
 * Copyright 2020 Peter Kenji Yamanaka
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

package com.pyamsoft.pydroid.bootstrap.version

import androidx.annotation.CheckResult
import com.pyamsoft.pydroid.core.ResultWrapper

/** An interactor for version checking related code */
public interface VersionInteractor {

  /** Watch for a completed download */
  public suspend fun watchForDownloadComplete(onDownloadCompleted: () -> Unit)

  /** Check for a new version update */
  @CheckResult public suspend fun checkVersion(force: Boolean): ResultWrapper<AppUpdateLauncher>

  /** Complete the update, which will restart the application */
  public suspend fun completeUpdate()
}
