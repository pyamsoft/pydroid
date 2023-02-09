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

package com.pyamsoft.pydroid.ui.internal.debug

import com.pyamsoft.pydroid.arch.AbstractViewModeler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

internal class DebugViewModeler
internal constructor(
    override val state: MutableDebugViewState,
    private val interactor: DebugInteractor,
    private val preferences: DebugPreferences,
) : AbstractViewModeler<DebugViewState>(state) {

  internal fun bind(scope: CoroutineScope) {
    val s = state

    scope.launch(context = Dispatchers.Main) {
      preferences.listenForInAppDebuggingEnabled().collectLatest { enabled ->
        s.isInAppDebuggingEnabled.value = enabled
      }
    }
  }

  internal fun handleCopy(scope: CoroutineScope) {
    scope.launch(context = Dispatchers.Main) {
      val lines = state.inAppDebuggingLogLines.value
      interactor.copyInAppDebugMessagesToClipboard(lines)
    }
  }
}