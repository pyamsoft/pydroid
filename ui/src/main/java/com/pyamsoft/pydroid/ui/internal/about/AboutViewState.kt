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

package com.pyamsoft.pydroid.ui.internal.about

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.pyamsoft.pydroid.arch.UiViewState
import com.pyamsoft.pydroid.bootstrap.libraries.OssLibrary

internal interface AboutViewState : UiViewState {
  val isLoading: Boolean
  val query: String
  val licenses: List<OssLibrary>
  val navigationError: Throwable?
}

internal class MutableAboutViewState : AboutViewState {
  override var isLoading by mutableStateOf(false)
  override var navigationError by mutableStateOf<Throwable?>(null)

  override var query by mutableStateOf("")

  internal var allLicenses by mutableStateOf<List<OssLibrary>>(emptyList())
  override var licenses by mutableStateOf<List<OssLibrary>>(emptyList())
}
