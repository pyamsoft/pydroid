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

import androidx.annotation.CheckResult
import androidx.compose.runtime.Composable
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.pyamsoft.pydroid.bootstrap.version.update.AppUpdateLauncher
import com.pyamsoft.pydroid.core.Logger
import com.pyamsoft.pydroid.core.requireNotNull
import com.pyamsoft.pydroid.ui.internal.version.upgrade.VersionUpgradeDialog
import com.pyamsoft.pydroid.ui.version.VersionCheckViewState
import com.pyamsoft.pydroid.util.doOnCreate
import com.pyamsoft.pydroid.util.doOnDestroy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

internal class VersionCheckDelegate(
    activity: FragmentActivity,
    viewModel: VersionCheckViewModeler,
    private val disabled: Boolean,
) {

  private var hostingActivity: FragmentActivity? = activity
  private var versionViewModel: VersionCheckViewModeler? = viewModel

  init {
    if (disabled) {
      Logger.w("Application has disabled the VersionCheck component")
    } else {
      activity.doOnCreate {
        viewModel.bind(
            scope = activity.lifecycleScope,
            onUpgradeReady = { handleConfirmUpgrade(viewModel, activity) },
        )
      }
    }

    activity.doOnDestroy {
      versionViewModel = null
      hostingActivity = null
    }
  }

  private fun showVersionUpgrade(
      activity: FragmentActivity,
      launcher: AppUpdateLauncher,
  ) {
    // Enforce that we do this on the Main thread
    activity.lifecycleScope.launch(context = Dispatchers.Main) {
      launcher
          .update(activity, RC_APP_UPDATE)
          .onSuccess { Logger.d("Call was made for in-app update request") }
          .onFailure { err -> Logger.e(err, "Unable to launch in-app update flow") }
    }
  }

  /** Returns the view state to be rendered */
  @Composable
  @CheckResult
  internal fun state(): VersionCheckViewState {
    return versionViewModel.requireNotNull().state()
  }

  private fun handleConfirmUpgrade(
      vm: VersionCheckViewModeler,
      act: FragmentActivity,
  ) {
    vm.handleConfirmUpgrade(
        scope = act.lifecycleScope,
    ) { newVersionCode ->
      VersionUpgradeDialog.show(
          activity = act,
          newVersionCode = newVersionCode,
      )
    }
  }

  /** Attempt to confirm an upgrade if one is possible */
  fun handleConfirmUpgrade() {
    if (disabled) {
      Logger.w("Application has disabled the VersionCheck component")
      return
    }

    val vm = versionViewModel
    if (vm == null) {
      Logger.w("Cannot confirm upgrade with null ViewModel")
      return
    }

    val act = hostingActivity
    if (act == null) {
      Logger.w("Cannot confirm upgrade with null Activity")
      return
    }

    handleConfirmUpgrade(vm, act)
  }

  /** Check for in-app updates */
  fun checkUpdates() {
    if (disabled) {
      Logger.w("Application has disabled the VersionCheck component")
      return
    }

    val act = hostingActivity.requireNotNull()
    versionViewModel
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

  companion object {

    // Only bottom 16 bits.
    private const val RC_APP_UPDATE = 146
  }
}
