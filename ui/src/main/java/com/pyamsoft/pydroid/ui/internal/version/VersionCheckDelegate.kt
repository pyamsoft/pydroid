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

package com.pyamsoft.pydroid.ui.internal.version

import androidx.compose.runtime.Composable
import androidx.lifecycle.lifecycleScope
import com.pyamsoft.pydroid.bootstrap.version.AppUpdateLauncher
import com.pyamsoft.pydroid.core.Logger
import com.pyamsoft.pydroid.core.requireNotNull
import com.pyamsoft.pydroid.ui.app.PYDroidActivity
import com.pyamsoft.pydroid.ui.internal.version.upgrade.VersionUpgradeDialog
import com.pyamsoft.pydroid.ui.version.VersionCheckViewState
import com.pyamsoft.pydroid.util.doOnCreate
import com.pyamsoft.pydroid.util.doOnDestroy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

internal class VersionCheckDelegate(activity: PYDroidActivity, viewModel: VersionCheckViewModeler) {

  private var activity: PYDroidActivity? = activity
  private var viewModel: VersionCheckViewModeler? = viewModel

  /** Bind Activity for related VersionCheck events */
  fun bindEvents() {
    activity.requireNotNull().also { a ->
      a.doOnDestroy {
        viewModel = null
        activity = null
      }

      a.doOnCreate {
        viewModel
            .requireNotNull()
            .bind(
                scope = a.lifecycleScope,
                onUpgradeReady = { handleConfirmUpgrade() },
            )
      }
    }
  }

  /** Attempt to confirm an upgrade if one is possible */
  fun handleConfirmUpgrade() {
    val vm = viewModel
    if (vm == null) {
      Logger.w("Cannot confirm upgrade with null ViewModel")
      return
    }

    val act = activity
    if (act == null) {
      Logger.w("Cannot confirm upgrade with null Activity")
      return
    }

    vm.handleConfirmUpgrade(
        scope = act.lifecycleScope,
    ) {
      VersionUpgradeDialog.show(act)
    }
  }

  /** Render a composable by watching the ViewModel state */
  @Composable
  fun Render(content: @Composable (VersionCheckViewState) -> Unit) {
    viewModel.requireNotNull().Render(content)
  }

  /** Check for in-app updates */
  fun checkUpdates() {
    val act = activity.requireNotNull()
    viewModel
        .requireNotNull()
        .handleCheckForUpdates(
            scope = act.lifecycleScope,
            force = false,
            onLaunchUpdate = {
              showVersionUpgrade(
                  activity = act,
                  launcher = it,
              )
            },
        )
  }

  private fun showVersionUpgrade(activity: PYDroidActivity, launcher: AppUpdateLauncher) {
    // Enforce that we do this on the Main thread
    activity.lifecycleScope.launch(context = Dispatchers.Main) {
      launcher
          .update(activity, RC_APP_UPDATE)
          .onSuccess { Logger.d("Call was made for in-app update request") }
          .onFailure { err -> Logger.e(err, "Unable to launch in-app update flow") }
    }
  }

  companion object {

    // Only bottom 16 bits.
    private const val RC_APP_UPDATE = 146
  }
}
