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

package com.pyamsoft.pydroid.ui.internal.changelog.dialog

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.pyamsoft.pydroid.ui.internal.app.AppViewState
import com.pyamsoft.pydroid.ui.internal.changelog.ChangeLogLine

internal interface ChangeLogDialogViewState : AppViewState {
  val changeLog: List<ChangeLogLine>
  val applicationVersionCode: Int
}

@Stable
internal class MutableChangeLogDialogViewState internal constructor() : ChangeLogDialogViewState {
  override var changeLog by mutableStateOf(emptyList<ChangeLogLine>())
  override var icon by mutableStateOf(0)
  override var applicationVersionCode by mutableStateOf(0)
  override var name by mutableStateOf("")
}
