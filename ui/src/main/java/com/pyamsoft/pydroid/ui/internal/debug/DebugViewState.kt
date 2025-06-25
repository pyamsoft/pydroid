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

package com.pyamsoft.pydroid.ui.internal.debug

import androidx.compose.runtime.Stable
import com.pyamsoft.pydroid.arch.UiViewState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Stable
internal interface DebugViewState : UiViewState {
  val isInAppDebuggingEnabled: StateFlow<Boolean>
  val inAppDebuggingLogLines: StateFlow<List<InAppDebugLogLine>>
}

@Stable
internal class MutableDebugViewState
internal constructor(
    logLinesBus: StateFlow<List<InAppDebugLogLine>>,
) : DebugViewState {
  override val isInAppDebuggingEnabled = MutableStateFlow(false)
  override val inAppDebuggingLogLines = logLinesBus
}
