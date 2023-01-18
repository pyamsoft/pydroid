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

package com.pyamsoft.pydroid.ui.internal.version.upgrade

import com.pyamsoft.pydroid.arch.AbstractViewModeler
import com.pyamsoft.pydroid.bootstrap.version.VersionInteractor
import com.pyamsoft.pydroid.core.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

internal class VersionUpgradeViewModeler
internal constructor(
    override val state: MutableVersionUpgradeViewState,
    private val interactor: VersionInteractor,
) : AbstractViewModeler<VersionUpgradeViewState>(state) {

  internal fun completeUpgrade(
      scope: CoroutineScope,
      onUpgradeComplete: () -> Unit,
  ) {
    if (state.upgraded.value) {
      Logger.w("Already upgraded, do nothing")
      return
    }

    state.upgraded.value = true
    scope.launch(context = Dispatchers.Main) {
      Logger.d("Updating app, restart via update manager!")
      interactor.completeUpdate()

      Logger.d("App update completed, publish finish!")
      onUpgradeComplete()
    }
  }
}
