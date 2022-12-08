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

import com.pyamsoft.pydroid.arch.AbstractViewModeler
import com.pyamsoft.pydroid.bootstrap.changelog.ChangeLogInteractor
import com.pyamsoft.pydroid.ui.changelog.ChangeLogProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

internal class ChangeLogDialogViewModeler
internal constructor(
    private val state: MutableChangeLogDialogViewState,
    private val interactor: ChangeLogInteractor,
    private val provider: ChangeLogProvider,
    private val version: Int,
) : AbstractViewModeler<ChangeLogDialogViewState>(state) {

  fun bind(scope: CoroutineScope) {
    scope.launch(context = Dispatchers.Main) {
      val displayName = interactor.getDisplayName()
      state.apply {
        name = displayName
        icon = provider.applicationIcon
        applicationVersionCode = version
        changeLog = provider.changelog.build()
      }
    }
  }
}
