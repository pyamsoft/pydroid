/*
 * Copyright 2023 Peter Kenji Yamanaka
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

package com.pyamsoft.pydroid.ui.internal.changelog

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.pyamsoft.pydroid.ui.changelog.ChangeLogViewState
import kotlinx.coroutines.flow.MutableStateFlow

@Stable
internal class MutableChangeLogViewState internal constructor() : ChangeLogViewState {
  override val isShowingDialog = MutableStateFlow(false)
  override val isShowUpsell = MutableStateFlow(false)
}
