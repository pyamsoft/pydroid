/*
 * Copyright 2023 pyamsoft
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

package com.pyamsoft.pydroid.ui.version

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import com.pyamsoft.pydroid.arch.UiViewState
import com.pyamsoft.pydroid.bootstrap.version.update.AppUpdateLauncher
import kotlinx.coroutines.flow.StateFlow

/** Version Checking UI state */
@Stable
public interface VersionCheckViewState : UiViewState {

  /** Are we currently checking for an update? */
  public val isCheckingForUpdate: StateFlow<CheckingState>

  /** The launcher that will start the In-App update process */
  public val launcher: StateFlow<AppUpdateLauncher?>

  /** Is the update downloaded and ready to install? */
  public val isUpdateReadyToInstall: StateFlow<Boolean>

  /** If an upload is downloading, how close are we to done? */
  public val updateProgressPercent: StateFlow<Float>

  /**
   * Has the upgrade completion been triggered (in production, the app closes, this is just to guard
   * double-clicks)
   */
  public val isUpgraded: StateFlow<Boolean>

  @Stable
  @Immutable
  public enum class CheckingState {
    NONE,
    CHECKING,
    DONE
  }
}
