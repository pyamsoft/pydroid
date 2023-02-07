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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.pyamsoft.pydroid.core.Logger
import com.pyamsoft.pydroid.core.requireNotNull
import com.pyamsoft.pydroid.ui.internal.pydroid.ObjectGraph
import com.pyamsoft.pydroid.ui.internal.version.VersionCheckViewModeler
import com.pyamsoft.pydroid.ui.internal.version.VersionUpgradeAvailableScreen
import com.pyamsoft.pydroid.ui.internal.version.upgrade.VersionUpgradeDialog
import com.pyamsoft.pydroid.util.doOnCreate
import com.pyamsoft.pydroid.util.doOnDestroy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
    private val disabled: Boolean,
) {
  private var hostingActivity: FragmentActivity? = activity
  internal var viewModel: VersionCheckViewModeler? = null

  init {
    if (disabled) {
      Logger.w("Application has disabled the VersionCheck component")
    } else {
      // Need to wait until after onCreate so that the ObjectGraph.ActivityScope is
      // correctly set up otherwise we crash.
      activity.doOnCreate {
        ObjectGraph.ActivityScope.retrieve(activity)
            .injector()
            .plusVersionCheck()
            .create()
            .inject(this)

        val observer =
            object : DefaultLifecycleObserver {

              override fun onStart(owner: LifecycleOwner) {
                viewModel
                    .requireNotNull()
                    .handleCheckForUpdates(
                        scope = owner.lifecycleScope,
                        force = false,
                        onLaunchUpdate = { launcher ->
                          // This will pop the in-app update dialog open. We should instead set a
                          // flag
                          // which displays some non-intrusive in-app UI via InterruptCard

                          // Don't use scope since if this leaves Composition it would die
                          // Enforce that we do this on the Main thread
                          activity.lifecycleScope.launch(context = Dispatchers.Main) {
                            launcher
                                .update(activity, VersionCheckViewModeler.RC_APP_UPDATE)
                                .onSuccess { Logger.d("Launched an in-app update flow") }
                                .onFailure { Logger.e(it, "Unable to launch in-app update flow") }
                          }
                        },
                    )
              }
            }
        val lifecycle = activity.lifecycle
        lifecycle.addObserver(observer)
        activity.doOnDestroy { lifecycle.removeObserver(observer) }
      }
    }

    activity.doOnDestroy {
      hostingActivity = null
      viewModel = null
    }
  }

  @Composable
  private fun RenderContent(
      state: VersionCheckViewState,
      onDismissDialog: () -> Unit,
      content: @Composable () -> Unit,
  ) {
    val showDialog by state.isUpgradeDialogShowing.collectAsState()
    val isUpdateReady by state.isUpdateReadyToInstall.collectAsState()

    content()

    if (showDialog && isUpdateReady) {
      val newVersionCode by state.availableUpdateVersionCode.collectAsState()
      VersionUpgradeDialog(
          newVersionCode = newVersionCode,
          onDismiss = onDismissDialog,
      )
    }
  }

  /**
   * Render into a composable the version check screen upsell
   *
   * Using custom UI
   */
  @Composable
  public fun Render(content: @Composable VersionUpgradeWidget) {
    if (disabled) {
      // Log in a LE so that we only log once per lifecycle instead of per-render
      LaunchedEffect(Unit) { Logger.w("Application has disabled the VersionCheck component") }
      return
    }

    val vm = viewModel.requireNotNull()
    val state = vm.state

    RenderContent(
        state = state,
        onDismissDialog = { vm.handleCloseDialog() },
    ) {
      content(state) { vm.handleOpenDialog() }
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
    @JvmOverloads
    public fun create(
        activity: FragmentActivity,
        disabled: Boolean = false,
    ): VersionUpgradeAvailable {
      return VersionUpgradeAvailable(activity, disabled)
    }
  }
}
