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
import androidx.annotation.CheckResult
import com.pyamsoft.pydroid.bootstrap.SchedulerProvider
import com.pyamsoft.pydroid.core.bus.EventBus
import com.pyamsoft.pydroid.ui.navigation.FailedNavigationEvent
import com.pyamsoft.pydroid.ui.navigation.NavigationViewModel
import com.pyamsoft.pydroid.ui.version.upgrade.VersionUpgradeHandler.VersionHandlerEvent

internal interface VersionUpgradeComponent {

  fun inject(dialog: VersionUpgradeDialog)

  interface Factory {

    @CheckResult
    fun create(
      parent: ViewGroup,
      newVersion: Int
    ): VersionUpgradeComponent

  }

  class Impl private constructor(
    private val parent: ViewGroup,
    private val applicationName: String,
    private val currentVersion: Int,
    private val newVersion: Int,
    private val schedulerProvider: SchedulerProvider,
    private val bus: EventBus<VersionHandlerEvent>,
    private val navigationBus: EventBus<FailedNavigationEvent>
  ) : VersionUpgradeComponent {

    override fun inject(dialog: VersionUpgradeDialog) {
      val handler = VersionUpgradeHandler(schedulerProvider, bus)
      val viewModel = VersionUpgradeViewModel(handler)
      val navigationViewModel = NavigationViewModel(schedulerProvider, navigationBus)
      val contentView = VersionUpgradeContentView(
          applicationName, currentVersion,
          newVersion, parent
      )
      val controlsView = VersionUpgradeControlView(parent, handler)
      val component = VersionUpgradeUiComponentImpl(
          controlsView, contentView,
          navigationViewModel, viewModel
      )
      dialog._component = component
    }

    internal class FactoryImpl internal constructor(
      private val applicationName: String,
      private val currentVersion: Int,
      private val schedulerProvider: SchedulerProvider,
      private val bus: EventBus<VersionHandlerEvent>,
      private val navigationBus: EventBus<FailedNavigationEvent>
    ) : Factory {

      override fun create(
        parent: ViewGroup,
        newVersion: Int
      ): VersionUpgradeComponent {
        return Impl(
            parent, applicationName, currentVersion,
            newVersion, schedulerProvider, bus,
            navigationBus
        )
      }

    }

  }
}
