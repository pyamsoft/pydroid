/*
 * Copyright 2025 pyamsoft
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

package com.pyamsoft.pydroid.ui.internal.version

import androidx.compose.runtime.Stable
import com.pyamsoft.pydroid.bootstrap.version.update.AppUpdateLauncher
import com.pyamsoft.pydroid.ui.version.VersionCheckViewState
import kotlinx.coroutines.flow.MutableStateFlow

@Stable
internal class MutableVersionCheckViewState internal constructor() : VersionCheckViewState {
  override val isCheckingForUpdate =
      MutableStateFlow<VersionCheckViewState.CheckingState>(
          VersionCheckViewState.CheckingState.None)
  override val launcher = MutableStateFlow<AppUpdateLauncher?>(null)
  override val isUpdateReadyToInstall = MutableStateFlow(false)
  override val updateProgressPercent = MutableStateFlow(0F)
  override val isUpgraded = MutableStateFlow(false)
}
