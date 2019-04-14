/*
 * Copyright 2019 Peter Kenji Yamanaka
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
 *
 */

package com.pyamsoft.pydroid.ui.version.upgrade

import com.pyamsoft.pydroid.arch.UiState
import com.pyamsoft.pydroid.arch.UiViewModel
import com.pyamsoft.pydroid.ui.version.upgrade.VersionUpgradeViewModel.VersionState
import com.pyamsoft.pydroid.ui.version.upgrade.VersionUpgradeViewModel.VersionState.Upgrade
import javax.inject.Inject

internal class VersionUpgradeViewModel @Inject internal constructor(
  private val handler: VersionUpgradeHandler
) : UiViewModel<VersionState>(
    initialState = VersionState(upgrade = null)
), VersionUpgradeControlView.Callback {

  override fun onBind() {
    handler.handle(this)
        .destroy()
  }

  override fun onUnbind() {
  }

  override fun onUpgradeClicked() {
    setState { copy(upgrade = Upgrade(true)) }
  }

  override fun onCancelClicked() {
    setState { copy(upgrade = Upgrade(false)) }
  }

  data class VersionState(val upgrade: Upgrade?) : UiState {

    data class Upgrade(val upgrade: Boolean)

  }
}
