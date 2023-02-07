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
import com.pyamsoft.pydroid.bus.EventConsumer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class DebugViewModeler
internal constructor(
    override val state: MutableDebugViewState,
    private val preferences: DebugPreferences,
    private val logLinesBus: EventConsumer<LogLine>,
) : AbstractViewModeler<DebugViewState>(state) {

  internal fun bind(scope: CoroutineScope) {
    val s = state

    scope.launch(context = Dispatchers.Main) {
      preferences.listenForInAppDebuggingEnabled().collectLatest { enabled ->
        s.isInAppDebuggingEnabled.value = enabled

        if (!enabled) {
          s.inAppDebuggingLogLines.update { it.apply { clear() } }
        }
      }
    }

    scope.launch(context = Dispatchers.Main) {
      logLinesBus.onEvent { line ->
        val isDebuggingEnabled = s.isInAppDebuggingEnabled.value
        s.inAppDebuggingLogLines.update { lines ->
          lines.apply {
            if (isDebuggingEnabled) {
              add(line)
            } else {
              clear()
            }
          }
        }
      }
    }
  }
}
