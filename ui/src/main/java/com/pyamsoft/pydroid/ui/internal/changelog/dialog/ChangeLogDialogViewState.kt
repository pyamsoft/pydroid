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

package com.pyamsoft.pydroid.ui.internal.changelog.dialog

import androidx.compose.runtime.Stable
import com.pyamsoft.pydroid.ui.internal.app.AppViewState
import com.pyamsoft.pydroid.ui.internal.changelog.ChangeLogLine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Stable
internal interface ChangeLogDialogViewState : AppViewState {
  val changeLog: StateFlow<List<ChangeLogLine>>
  val applicationVersionCode: StateFlow<Int>
}

@Stable
internal class MutableChangeLogDialogViewState internal constructor() : ChangeLogDialogViewState {
  override val changeLog = MutableStateFlow(emptyList<ChangeLogLine>())
  override val icon = MutableStateFlow(0)
  override val applicationVersionCode = MutableStateFlow(0)
  override val name = MutableStateFlow("")
}
