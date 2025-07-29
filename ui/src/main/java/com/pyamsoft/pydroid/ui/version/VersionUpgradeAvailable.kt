/*
 * Copyright 2025 pyamsoft
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
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

import androidx.activity.ComponentActivity
import androidx.annotation.CheckResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.pyamsoft.pydroid.bootstrap.version.AppUpdateResultStatus
import com.pyamsoft.pydroid.bootstrap.version.update.AppUpdateLauncher
import com.pyamsoft.pydroid.core.requireNotNull
import com.pyamsoft.pydroid.ui.internal.pydroid.ObjectGraph
import com.pyamsoft.pydroid.ui.internal.util.rememberResolvedActivity
import com.pyamsoft.pydroid.ui.internal.version.VersionCheckViewModeler
import com.pyamsoft.pydroid.ui.internal.version.VersionUpdateProgressScreen
import com.pyamsoft.pydroid.ui.internal.version.VersionUpgradeAvailableScreen
import com.pyamsoft.pydroid.ui.internal.version.VersionUpgradeCompleteScreen
import com.pyamsoft.pydroid.util.Logger
import com.pyamsoft.pydroid.util.doOnCreate
import com.pyamsoft.pydroid.util.doOnDestroy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/** Upon download action started, this callback will run */
public typealias OnUpdateDownloadStartedCallback = (launcher: AppUpdateLauncher) -> Unit

/** Upon upgrade action started, this callback will run */
public typealias OnUpgradeStartedCallback = () -> Unit

/** User has selected to try downloading again after initial download failed */
public typealias OnDownloadRetryCallback = () -> Unit

/** A Composable that can display version upgrade availability */
public typealias VersionUpgradeWidget =
    @Composable
    (
        VersionCheckViewState,
        OnUpdateDownloadStartedCallback,
        OnUpgradeStartedCallback,
        OnDownloadRetryCallback,
    ) -> Unit

private fun handleRestartUpdateFlow(
    viewModel: VersionCheckViewModeler,
    activity: ComponentActivity,
    onLauncherReceived: (AppUpdateLauncher) -> Unit = {},
) {
  // Since an update launcher can only be used once, if the update fails to
  // happen because of the user or a failure, re-fetch a new updater
  //
  // Don't use scope since if this leaves Composition it would die
  viewModel.handleCheckForUpdates(
      scope = activity.lifecycleScope,
      force = true,
      onLauncherReceived = onLauncherReceived,
  )
}

/**
 * A self contained class which is able to check for updates and prompt the user to install them
 * in-app. Adopts the theme from whichever composable it is rendered into
 */
public class VersionUpgradeAvailable
internal constructor(
    activity: ComponentActivity,
    private val disabled: Boolean,
) {
  internal var viewModel: VersionCheckViewModeler? = null

  init {
    if (disabled) {
      Logger.w { "Application has disabled the VersionCheck component" }
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

              override fun onCreate(owner: LifecycleOwner) {
                val vm = viewModel.requireNotNull()
                vm.bind(scope = owner.lifecycleScope)
              }

              override fun onStart(owner: LifecycleOwner) {
                viewModel
                    .requireNotNull()
                    .handleCheckForUpdates(
                        scope = owner.lifecycleScope,
                        force = false,
                    )
              }
            }
        val lifecycle = activity.lifecycle
        lifecycle.addObserver(observer)
        activity.doOnDestroy { lifecycle.removeObserver(observer) }
      }
    }

    activity.doOnDestroy { viewModel = null }
  }

  /**
   * Render into a composable the version check screen upsell
   *
   * Using custom UI
   */
  @Composable
  public fun Render(
      content: VersionUpgradeWidget,
  ) {
    if (disabled) {
      // Log in a LE so that we only log once per lifecycle instead of per-render
      LaunchedEffect(Unit) { Logger.w { "Application has disabled the VersionCheck component" } }
      return
    }

    val vm = viewModel.requireNotNull()

    // Required to launch upgrade flows and force finish
    val activity = rememberResolvedActivity()

    val handleStartInAppUpdateDownload by rememberUpdatedState { launcher: AppUpdateLauncher ->
      // Don't use scope since if this leaves Composition it would die
      // Enforce that we do this on the Main thread
      activity.lifecycleScope.launch(context = Dispatchers.Main) {
        launcher
            .launchUpdate(activity)
            .onSuccess { status ->
              when (status) {
                AppUpdateResultStatus.ACCEPTED -> {
                  Logger.d { "Started downloading new in-app update..." }
                }

                AppUpdateResultStatus.USER_CANCELLED -> {
                  Logger.d { "User canceled in-app flow, unable to start downloading update." }
                  handleRestartUpdateFlow(
                      viewModel = vm,
                      activity = activity,
                  )
                }

                AppUpdateResultStatus.IN_APP_UPDATE_FAILED -> {
                  Logger.d { "In-app flow failed, unable to start downloading update." }
                  vm.handleInAppUpdateFailed()
                }
              }
            }
            .onFailure {
              Logger.e(it) { "Unable to launch in-app update flow to start download." }
              vm.handleInAppUpdateFailed()
            }
      }
    }

    val handleUpgradeStarted by rememberUpdatedState {
      // Fire the upgrade (in prod, this closes the app)
      vm.handleCompleteUpgrade(
          scope = activity.lifecycleScope,
          onUpgradeCompleted = {
            Logger.d { "Upgrade completed, finish Activity" }
            activity.finishAndRemoveTask()
          },
      )
    }

    content(
        vm,
        { handleStartInAppUpdateDownload(it) },
        { handleUpgradeStarted() },
        {
          handleRestartUpdateFlow(
              viewModel = vm,
              activity = activity,
              onLauncherReceived = {
                // Once we receive the launcher immediately restart the download flow
                handleStartInAppUpdateDownload(it)
              },
          )
        },
    )
  }

  /** Render into a composable the default version check screen upsell */
  @Composable
  public fun RenderVersionCheckWidget(
      modifier: Modifier = Modifier,
  ) {
    Render { state, onDownloadStarted, onUpgradeStarted, onRestartDownload ->
      VersionUpgradeAvailableScreen(
          modifier = modifier,
          state = state,
          onBeginInAppUpdate = { launcher, forceRetry ->
            if (forceRetry) {
              onRestartDownload()
            } else {
              onDownloadStarted(launcher)
            }
          },
      )

      VersionUpdateProgressScreen(
          modifier = modifier,
          state = state,
      )

      VersionUpgradeCompleteScreen(
          modifier = modifier,
          state = state,
          onCompleteUpdate = onUpgradeStarted,
      )
    }
  }

  public companion object {

    /** Create a new version upgrade available UI component */
    @JvmStatic
    @CheckResult
    @JvmOverloads
    public fun create(
        activity: ComponentActivity,
        disabled: Boolean = false,
    ): VersionUpgradeAvailable {
      return VersionUpgradeAvailable(activity, disabled)
    }
  }
}
