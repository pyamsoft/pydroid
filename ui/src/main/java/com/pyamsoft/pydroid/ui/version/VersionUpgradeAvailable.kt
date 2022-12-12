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

package com.pyamsoft.pydroid.ui.version

import androidx.annotation.CheckResult
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
import com.pyamsoft.pydroid.core.requireNotNull
import com.pyamsoft.pydroid.ui.internal.pydroid.PYDroidActivityInstallTracker
import com.pyamsoft.pydroid.ui.internal.version.VersionCheckViewModeler
import com.pyamsoft.pydroid.ui.internal.version.VersionUpgradeAvailableScreen
import com.pyamsoft.pydroid.ui.internal.version.upgrade.VersionUpgradeDialog
import com.pyamsoft.pydroid.util.doOnCreate
import com.pyamsoft.pydroid.util.doOnDestroy

/** Upon upgrade action started, this callback will run */
public typealias OnUpgradeStartedCallback = () -> Unit

/** A Composable that can display version upgrade availability */
public typealias VersionUpgradeWidget =
    (
        VersionCheckViewState,
        OnUpgradeStartedCallback,
    ) -> Unit

/**
 * A self contained class which is able to check for updates and prompt the user to install them
 * in-app. Adopts the theme from whichever composable it is rendered into
 */
public class VersionUpgradeAvailable
internal constructor(
    activity: FragmentActivity,
) {
  private var hostingActivity: FragmentActivity? = activity

  internal var viewModel: VersionCheckViewModeler? = null

  init {
    // Need to wait until after onCreate so that the PYDroidActivityInstallTracker is
    // correctly set up otherwise we crash.
    activity.doOnCreate {
      PYDroidActivityInstallTracker.retrieve(activity)
          .injector()
          .plusVersionCheck()
          .create()
          .inject(this)
    }

    activity.doOnDestroy {
      hostingActivity = null
      viewModel = null
    }
  }

  private fun handleUpgrade(
      newVersionCode: Int,
  ) {
    val act = hostingActivity.requireNotNull()
    VersionUpgradeDialog.show(
        activity = act,
        newVersionCode = newVersionCode,
    )
  }

  /**
   * Render into a composable the version check screen upsell
   *
   * Using custom UI
   */
  @Composable
  public fun Render(content: @Composable VersionUpgradeWidget) {
    val state = viewModel.requireNotNull().state()
    content(state) {
      handleUpgrade(
          newVersionCode = state.availableUpdateVersionCode,
      )
    }
  }

  /** Render into a composable the default version check screen upsell */
  @Composable
  public fun RenderVersionCheckWidget(
      modifier: Modifier = Modifier,
  ) {
    Render { state, onUpgradeStarted ->
      VersionUpgradeAvailableScreen(
          modifier = modifier,
          state = state,
          onUpgrade = onUpgradeStarted,
      )
    }
  }

  public companion object {

    /** Create a new version upgrade available UI component */
    @JvmStatic
    @CheckResult
    public fun create(
        activity: FragmentActivity,
    ): VersionUpgradeAvailable {
      return VersionUpgradeAvailable(activity)
    }
  }
}
