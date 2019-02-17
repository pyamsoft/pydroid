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

import android.view.ViewGroup
import com.pyamsoft.pydroid.core.bus.EventBus
import com.pyamsoft.pydroid.ui.navigation.FailedNavigationEvent
import com.pyamsoft.pydroid.ui.navigation.FailedNavigationPresenterImpl

internal class VersionUpgradeComponentImpl internal constructor(
  private val parent: ViewGroup,
  private val name: String,
  private val currentVersion: Int,
  private val newVersion: Int,
  private val failedNavBus: EventBus<FailedNavigationEvent>
) : VersionUpgradeComponent {

  override fun inject(dialog: VersionUpgradeDialog) {
    val presenter = VersionUpgradePresenterImpl()
    val controls = VersionUpgradeControlView(parent, presenter)
    val content = VersionUpgradeContentView(name, currentVersion, newVersion, parent)

    dialog.apply {
      this.failedNavigationPresenter = FailedNavigationPresenterImpl(failedNavBus)
      this.contentView = content
      this.controlsView = controls
      this.presenter = presenter
    }
  }
}
