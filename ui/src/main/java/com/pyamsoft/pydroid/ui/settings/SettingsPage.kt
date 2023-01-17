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

package com.pyamsoft.pydroid.ui.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.lifecycleScope
import com.pyamsoft.pydroid.bootstrap.version.update.AppUpdateLauncher
import com.pyamsoft.pydroid.core.Logger
import com.pyamsoft.pydroid.core.requireNotNull
import com.pyamsoft.pydroid.theme.ZeroSize
import com.pyamsoft.pydroid.ui.inject.rememberComposableInjector
import com.pyamsoft.pydroid.ui.internal.about.AboutDialog
import com.pyamsoft.pydroid.ui.internal.billing.dialog.BillingDialog
import com.pyamsoft.pydroid.ui.internal.changelog.dialog.ChangeLogDialog
import com.pyamsoft.pydroid.ui.internal.datapolicy.dialog.DataPolicyDisclosureDialog
import com.pyamsoft.pydroid.ui.internal.settings.SettingsInjector
import com.pyamsoft.pydroid.ui.internal.settings.SettingsScreen
import com.pyamsoft.pydroid.ui.internal.settings.SettingsViewModeler
import com.pyamsoft.pydroid.ui.internal.settings.reset.ResetDialog
import com.pyamsoft.pydroid.ui.internal.version.upgrade.VersionUpgradeDialog
import com.pyamsoft.pydroid.ui.preference.Preferences
import com.pyamsoft.pydroid.ui.theme.Theming
import com.pyamsoft.pydroid.ui.theme.ZeroElevation
import com.pyamsoft.pydroid.ui.util.rememberActivity
import com.pyamsoft.pydroid.ui.util.rememberNotNull
import com.pyamsoft.pydroid.util.MarketLinker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// Only bottom 16 bits.
private const val RC_APP_UPDATE = 146

@Composable
private fun MountHooks(
    viewModel: SettingsViewModeler,
) {
  LaunchedEffect(viewModel) {
    viewModel.bind(scope = this)
    viewModel.handleLoadPreferences(scope = this)
  }
}

/** Composable for displaying a settings page */
@Composable
public fun SettingsPage(
    modifier: Modifier = Modifier,
    shape: Shape = RectangleShape,
    hideUpgradeInformation: Boolean = false,
    hideClearAll: Boolean = false,
    customPrePreferences: List<Preferences> = remember { emptyList() },
    customPostPreferences: List<Preferences> = remember { emptyList() },
    customTopItemMargin: Dp = ZeroSize,
    customBottomItemMargin: Dp = ZeroSize,
    customElevation: Dp = ZeroElevation,
) {
  val activity = rememberActivity()
  val scope = rememberCoroutineScope()
  val uriHandler = LocalUriHandler.current

  val component = rememberComposableInjector { SettingsInjector() }
  val options = rememberNotNull(component.options)
  val viewModel = rememberNotNull(component.viewModel)
  val versionViewModel = rememberNotNull(component.versionViewModel)

  val versionState = versionViewModel.state()

  val (showChangeLogDialog, setShowChangeLogDialog) = remember { mutableStateOf(false) }
  val handleDismissChangeLogDialog by rememberUpdatedState { setShowChangeLogDialog(false) }
  val handleShowChangeLogDialog by rememberUpdatedState { setShowChangeLogDialog(true) }

  val (showBillingDialog, setShowBillingDialog) = remember { mutableStateOf(false) }
  val handleDismissBillingDialog by rememberUpdatedState { setShowBillingDialog(false) }
  val handleShowBillingDialog by rememberUpdatedState { setShowBillingDialog(true) }

  val (showResetDialog, setShowResetDialog) = remember { mutableStateOf(false) }
  val handleDismissResetDialog by rememberUpdatedState { setShowResetDialog(false) }
  val handleShowResetDialog by rememberUpdatedState { setShowResetDialog(true) }

  val (showVersionDialog, setShowVersionDialog) = remember { mutableStateOf(false) }
  val handleDismissVersionDialog by rememberUpdatedState { setShowVersionDialog(false) }
  val handleShowVersionDialog by rememberUpdatedState { setShowVersionDialog(true) }

  val (showAboutDialog, setShowAboutDialog) = remember { mutableStateOf(false) }
  val handleDismissAboutDialog by rememberUpdatedState { setShowAboutDialog(false) }
  val handleShowAboutDialog by rememberUpdatedState { setShowAboutDialog(true) }

  val (showDataDisclosureDialog, setShowDataDisclosureDialog) = remember { mutableStateOf(false) }
  val handleDismissDataDisclosureDialog by rememberUpdatedState {
    setShowDataDisclosureDialog(false)
  }
  val handleShowDataDisclosureDialog by rememberUpdatedState { setShowDataDisclosureDialog(true) }

  val handleShowVersionUpgrade by rememberUpdatedState { launcher: AppUpdateLauncher ->
    if (options.requireNotNull().disableVersionCheck) {
      Logger.w("Application has disabled the VersionCheck component")
      return@rememberUpdatedState
    }

    // Don't use scope since if this leaves Composition it would die
    // Enforce that we do this on the Main thread
    activity.lifecycleScope.launch(context = Dispatchers.Main) {
      launcher
          .update(activity, RC_APP_UPDATE)
          .onSuccess { Logger.d("Launched an in-app update flow") }
          .onFailure { err -> Logger.e(err, "Unable to launch in-app update flow") }
    }
  }

  val handleChangeDarkMode by rememberUpdatedState { mode: Theming.Mode ->
    viewModel.handleChangeDarkMode(
        // Don't use scope since if this leaves Composition it would die
        scope = activity.lifecycleScope,
        mode = mode,
    )
  }

  val handleCheckForUpdates by rememberUpdatedState {
    if (options.disableVersionCheck) {
      Logger.w("Application has disabled the VersionCheck component")
      return@rememberUpdatedState
    }

    versionViewModel.handleCheckForUpdates(
        scope = scope,
        force = true,
        onLaunchUpdate = {
          handleShowVersionDialog()
          handleShowVersionUpgrade(it)
        },
    )
  }

  val handleOpenPage by rememberUpdatedState { url: String ->
    try {
      uriHandler.openUri(url)
    } catch (e: Throwable) {
      Logger.e(e, "Unable to open Activity for URL: $url")
    }
  }

  val handleViewBlog by rememberUpdatedState { viewModel.handleViewBlog(handleOpenPage) }
  val handleViewSocials by rememberUpdatedState { viewModel.handleViewSocialMedia(handleOpenPage) }
  val handleViewTos by rememberUpdatedState { viewModel.handleViewTermsOfService(handleOpenPage) }
  val handleViewPrivacy by rememberUpdatedState {
    viewModel.handleViewPrivacyPolicy(handleOpenPage)
  }
  val handleViewSource by rememberUpdatedState { viewModel.handleViewSourceCode(handleOpenPage) }
  val handleReportBug by rememberUpdatedState { viewModel.handleReportBug(handleOpenPage) }
  val handleOpenMarket by rememberUpdatedState {
    handleOpenPage(MarketLinker.getStorePageLink(activity))
  }

  MountHooks(
      viewModel = viewModel,
  )

  SettingsScreen(
      modifier = modifier,
      shape = shape,
      elevation = customElevation,
      state = viewModel.state(),
      options = options,
      hideClearAll = hideClearAll,
      hideUpgradeInformation = hideUpgradeInformation,
      topItemMargin = customTopItemMargin,
      bottomItemMargin = customBottomItemMargin,
      customPreContent = customPrePreferences,
      customPostContent = customPostPreferences,
      onDarkModeChanged = handleChangeDarkMode,
      onLicensesClicked = handleShowAboutDialog,
      onCheckUpdateClicked = handleCheckForUpdates,
      onShowChangeLogClicked = handleShowChangeLogDialog,
      onResetClicked = handleShowResetDialog,
      onDonateClicked = handleShowBillingDialog,
      onBugReportClicked = handleReportBug,
      onViewSourceClicked = handleViewSource,
      onViewDataPolicyClicked = handleShowDataDisclosureDialog,
      onViewPrivacyPolicyClicked = handleViewPrivacy,
      onViewTermsOfServiceClicked = handleViewTos,
      onViewSocialMediaClicked = handleViewSocials,
      onViewBlogClicked = handleViewBlog,
      onOpenMarketPage = handleOpenMarket,
  )

  if (showChangeLogDialog) {
    ChangeLogDialog(
        onDismiss = handleDismissChangeLogDialog,
    )
  }

  if (showBillingDialog) {
    BillingDialog(
        onDismiss = handleDismissBillingDialog,
    )
  }

  if (showDataDisclosureDialog) {
    DataPolicyDisclosureDialog(
        onDismiss = handleDismissDataDisclosureDialog,
    )
  }

  if (showResetDialog) {
    ResetDialog(
        onDismiss = handleDismissResetDialog,
    )
  }

  if (showAboutDialog) {
    AboutDialog(
        onDismiss = handleDismissAboutDialog,
    )
  }

  if (showVersionDialog && versionState.isUpdateReadyToInstall) {
    VersionUpgradeDialog(
        newVersionCode = versionState.availableUpdateVersionCode,
        onDismiss = handleDismissVersionDialog,
    )
  }
}
