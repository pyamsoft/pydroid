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

package com.pyamsoft.pydroid.ui.internal.settings.reset

import com.pyamsoft.pydroid.arch.AbstractViewModeler
import com.pyamsoft.pydroid.bootstrap.settings.SettingsInteractor
import com.pyamsoft.pydroid.core.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

internal class ResetViewModeler
internal constructor(
    state: MutableResetViewState,
    private val interactor: SettingsInteractor,
) : AbstractViewModeler<ResetViewState>(state) {

  private val vmState = state

  internal fun handleFullReset(scope: CoroutineScope) {
    if (state.reset.value) {
      Logger.w("Already reset, do nothing")
      return
    }

    vmState.reset.value = true
    scope.launch(context = Dispatchers.Default) {
      Logger.d("Completely reset application")
      interactor.wipeData()
    }
  }
}
