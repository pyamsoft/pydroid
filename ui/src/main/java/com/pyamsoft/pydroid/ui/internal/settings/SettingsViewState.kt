/*
 * Copyright 2022 Peter Kenji Yamanaka
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

package com.pyamsoft.pydroid.ui.internal.settings

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import com.pyamsoft.pydroid.arch.UiViewState
import com.pyamsoft.pydroid.ui.theme.Theming
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Stable
internal interface SettingsViewState : UiViewState {
  val loadingState: StateFlow<LoadingState>
  val applicationName: StateFlow<CharSequence>

  val darkMode: StateFlow<Theming.Mode>

  val isInAppDebuggingEnabled: StateFlow<Boolean>

  val isShowingResetDialog: StateFlow<Boolean>
  val isShowingAboutDialog: StateFlow<Boolean>
  val isShowingDataPolicyDialog: StateFlow<Boolean>
  val isShowingInAppDebugDialog: StateFlow<Boolean>

  @Stable
  @Immutable
  enum class LoadingState {
    NONE,
    LOADING,
    DONE
  }
}

@Stable
internal class MutableSettingsViewState internal constructor() : SettingsViewState {
  override val loadingState = MutableStateFlow(SettingsViewState.LoadingState.NONE)
  override val applicationName = MutableStateFlow("")

  override val darkMode = MutableStateFlow(Theming.Mode.SYSTEM)

  override val isInAppDebuggingEnabled = MutableStateFlow(false)

  override val isShowingResetDialog = MutableStateFlow(false)
  override val isShowingAboutDialog = MutableStateFlow(false)
  override val isShowingDataPolicyDialog = MutableStateFlow(false)
  override val isShowingInAppDebugDialog = MutableStateFlow(false)
}
