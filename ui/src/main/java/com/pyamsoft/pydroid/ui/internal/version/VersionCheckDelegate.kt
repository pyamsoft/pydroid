/*
 * Copyright 2021 Peter Kenji Yamanaka
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

package com.pyamsoft.pydroid.ui.internal.version

import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.pyamsoft.pydroid.bootstrap.version.AppUpdateLauncher
import com.pyamsoft.pydroid.core.Logger
import com.pyamsoft.pydroid.core.requireNotNull
import com.pyamsoft.pydroid.ui.app.PYDroidActivity
import com.pyamsoft.pydroid.util.MarketLinker
import com.pyamsoft.pydroid.util.doOnDestroy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

internal class VersionCheckDelegate(activity: PYDroidActivity, viewModel: VersionCheckViewModeler) {

  private var activity: PYDroidActivity? = activity
  private var viewModel: VersionCheckViewModeler? = viewModel

  /** Bind Activity for related VersionCheck events */
  fun bindEvents() {
    activity.requireNotNull().doOnDestroy {
      viewModel = null
      activity = null
    }
  }

  /** Check for in-app updates */
  fun checkUpdates() {
    val act = activity.requireNotNull()
    viewModel
        .requireNotNull()
        .handleCheckForUpdates(
            scope = act.lifecycleScope,
            force = false,
            onLaunchUpdate = { isFallback, launcher ->
              showVersionUpgrade(
                  activity = act,
                  isFallbackEnabled = isFallback,
                  launcher = launcher,
              )
            },
        )
  }

  /**
   * Version Check screen
   *
   * All UI and function related to checking for new updates to Applications
   */
  @Composable
  fun VersionCheck(
      scaffoldState: ScaffoldState,
  ) {
    VersionCheck(
        snackbarHostState = scaffoldState.snackbarHostState,
        addSnackbarHost = false,
    )
  }

  /**
   * Version Check screen
   *
   * All UI and function related to checking for new updates to Applications
   */
  @Composable
  @JvmOverloads
  fun VersionCheck(
      modifier: Modifier = Modifier,
      snackbarHostState: SnackbarHostState,
  ) {
    VersionCheck(
        modifier = modifier,
        snackbarHostState = snackbarHostState,
        addSnackbarHost = true,
    )
  }

  @Composable
  private fun VersionCheck(
      modifier: Modifier = Modifier,
      snackbarHostState: SnackbarHostState,
      addSnackbarHost: Boolean,
  ) {
    val vm = viewModel.requireNotNull()

    vm.Render { state ->
      VersionCheckScreen(
          modifier = modifier,
          state = state,
          addSnackbarHost = addSnackbarHost,
          snackbarHostState = snackbarHostState,
          onNavigationErrorDismissed = { vm.handleHideNavigation() },
          onVersionCheckErrorDismissed = { vm.handleClearError() },
      )
    }
  }

  private fun showVersionUpgrade(
      activity: PYDroidActivity,
      isFallbackEnabled: Boolean,
      launcher: AppUpdateLauncher
  ) {
    // Enforce that we do this on the Main thread
    activity.lifecycleScope.launch(context = Dispatchers.Main) {
      launcher.update(activity, RC_APP_UPDATE).onFailure { err ->
        Logger.e(err, "Unable to launch in-app update flow")
        if (isFallbackEnabled) {
          val vm = viewModel.requireNotNull()
          MarketLinker.linkToMarketPage(activity)
              .onSuccess { vm.handleHideNavigation() }
              .onFailure { vm.handleNavigationFailed(it) }
        }
      }
    }
  }

  companion object {

    // Only bottom 16 bits.
    private const val RC_APP_UPDATE = 146
  }
}
