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

package com.pyamsoft.pydroid.ui.internal.datapolicy.dialog

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.pyamsoft.pydroid.ui.internal.app.AppViewState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Stable
internal interface DataPolicyDialogViewState : AppViewState {
  val navigationError: StateFlow<Throwable?>
}

@Stable
internal class MutableDataPolicyDialogViewState internal constructor() : DataPolicyDialogViewState {
  override val name = MutableStateFlow("")
  override val icon = MutableStateFlow(0)
  override val navigationError = MutableStateFlow<Throwable?>(null)
}
