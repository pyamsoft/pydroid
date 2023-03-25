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

package com.pyamsoft.pydroid.ui.internal.about

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.pyamsoft.pydroid.arch.UiViewState
import com.pyamsoft.pydroid.bootstrap.libraries.OssLibrary
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Stable
internal interface AboutViewState : UiViewState {
  val loadingState: StateFlow<LoadingState>
  val licenses: StateFlow<List<OssLibrary>>
  val navigationError: StateFlow<Throwable?>

  @Stable
  @Immutable
  enum class LoadingState {
    NONE,
    LOADING,
    DONE
  }
}

@Stable
internal class MutableAboutViewState internal constructor() : AboutViewState {
  override val loadingState = MutableStateFlow(AboutViewState.LoadingState.NONE)
  override val navigationError = MutableStateFlow<Throwable?>(null)
  override val licenses = MutableStateFlow(emptyList<OssLibrary>())
}
