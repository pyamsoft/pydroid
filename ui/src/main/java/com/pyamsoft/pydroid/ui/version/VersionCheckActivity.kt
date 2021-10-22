/*
 * Copyright 2020 Peter Kenji Yamanaka
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

import android.os.Bundle
import androidx.activity.viewModels
import androidx.annotation.CallSuper
import androidx.compose.material.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.pyamsoft.pydroid.bootstrap.version.AppUpdateLauncher
import com.pyamsoft.pydroid.core.Logger
import com.pyamsoft.pydroid.core.requireNotNull
import com.pyamsoft.pydroid.inject.Injector
import com.pyamsoft.pydroid.ui.PYDroidComponent
import com.pyamsoft.pydroid.ui.app.ActivityBase
import com.pyamsoft.pydroid.ui.internal.version.VersionCheckComponent
import com.pyamsoft.pydroid.ui.internal.version.VersionCheckControllerEvent.LaunchUpdate
import com.pyamsoft.pydroid.ui.internal.version.VersionCheckControllerEvent.UpgradeReady
import com.pyamsoft.pydroid.ui.internal.version.VersionCheckScreen
import com.pyamsoft.pydroid.ui.internal.version.VersionCheckViewModel
import com.pyamsoft.pydroid.ui.internal.version.upgrade.VersionUpgradeDialog
import com.pyamsoft.pydroid.util.MarketLinker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/** Activity that handles checking for a new version update */
public abstract class VersionCheckActivity : ActivityBase() {

  /** Check for updates automatically */
  protected open val checkForUpdates: Boolean = true

  internal var versionFactory: ViewModelProvider.Factory? = null
  private val viewModel by viewModels<VersionCheckViewModel> { versionFactory.requireNotNull() }

  private var injector: VersionCheckComponent? = null

  /** On create */
  @CallSuper
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    // Need to do this in onPostCreate because the snackbarRoot will not be available until
    // after subclass onCreate
    injector =
        Injector.obtainFromApplication<PYDroidComponent>(this).plusVersionCheck().create().also {
            component ->
          component.inject(this)
        }

    viewModel.bindController(this) { event ->
      return@bindController when (event) {
        is LaunchUpdate -> showVersionUpgrade(event.isFallbackEnabled, event.launcher)
        is UpgradeReady -> VersionUpgradeDialog.show(this)
      }
    }
  }

  override fun onPostCreate(savedInstanceState: Bundle?) {
    super.onPostCreate(savedInstanceState)
    if (checkForUpdates) {
      checkUpdates()
    }
  }

  /**
   * Version Check screen
   *
   * All UI and function related to checking for new updates to Applications
   */
  @Composable
  protected fun VersionScreen(
      snackbarHostState: SnackbarHostState,
  ) {
    val state by viewModel.compose()

    VersionCheckScreen(
        state = state,
        snackbarHostState = snackbarHostState,
        onNavigationErrorDismissed = { viewModel.handleHideNavigation() },
        onVersionCheckErrorDismissed = { viewModel.handleClearError() },
    )
  }

  /** Get system service */
  // Provide this graph as a service injector
  @CallSuper
  override fun getSystemService(name: String): Any? =
      when (name) {
        VersionCheckComponent::class.java.name -> injector.requireNotNull()
        else -> super.getSystemService(name)
      }

  /** On destroy */
  @CallSuper
  override fun onDestroy() {
    super.onDestroy()
    versionFactory = null
  }

  private fun checkUpdates() {
    require(checkForUpdates) {
      "checkUpdates() will be called automatically, do not call this manually."
    }
    viewModel.handleCheckForUpdates(false)
  }

  private fun showVersionUpgrade(isFallbackEnabled: Boolean, launcher: AppUpdateLauncher) {
    val activity = this

    // Enforce that we do this on the Main thread
    lifecycleScope.launch(context = Dispatchers.Main) {
      launcher.update(activity, RC_APP_UPDATE).onFailure { err ->
        Logger.e(err, "Unable to launch in-app update flow")
        if (isFallbackEnabled) {
          MarketLinker.linkToMarketPage(activity)
              .onSuccess { viewModel.handleNavigationSuccess() }
              .onFailure { viewModel.handleNavigationFailed(it) }
        }
      }
    }
  }

  /** Call for an update manually */
  public fun checkForUpdates() {
    require(!checkForUpdates) {
      "checkForUpdates() must be called manually and cannot be called when checkForUpdates is automatic."
    }
    viewModel.handleCheckForUpdates(false)
  }

  public companion object {

    // Only bottom 16 bits.
    private const val RC_APP_UPDATE = 146
  }
}
