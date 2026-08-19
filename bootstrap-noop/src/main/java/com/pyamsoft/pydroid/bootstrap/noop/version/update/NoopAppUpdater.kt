/*
 * Copyright 2026 pyamsoft
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pyamsoft.pydroid.bootstrap.noop.version.update

import com.pyamsoft.pydroid.bootstrap.version.update.AppUpdateLauncher
import com.pyamsoft.pydroid.bootstrap.version.update.AppUpdater
import com.pyamsoft.pydroid.core.LintIgnoreEmptyFunctionBlock

internal object NoopAppUpdater : AppUpdater {
  @LintIgnoreEmptyFunctionBlock
  override suspend fun watchDownloadStatus(
      onDownloadProgress: (Float) -> Unit,
      onDownloadCompleted: () -> Unit,
      onDownloadCancelled: () -> Unit,
      onDownloadFailed: () -> Unit,
  ) {}

  override suspend fun checkForUpdate(): AppUpdateLauncher {
    return NoopAppUpdateLauncher
  }

  @LintIgnoreEmptyFunctionBlock override suspend fun completeUpgrade() {}
}
