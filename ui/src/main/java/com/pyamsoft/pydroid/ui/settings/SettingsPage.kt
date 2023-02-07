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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.lifecycleScope
import com.pyamsoft.pydroid.arch.SaveStateDisposableEffect
import com.pyamsoft.pydroid.core.Logger
import com.pyamsoft.pydroid.theme.ZeroSize
import com.pyamsoft.pydroid.ui.app.PYDroidActivityOptions
import com.pyamsoft.pydroid.ui.billing.BillingViewState
import com.pyamsoft.pydroid.ui.changelog.ChangeLogViewState
import com.pyamsoft.pydroid.ui.inject.rememberComposableInjector
import com.pyamsoft.pydroid.ui.internal.about.AboutDialog
import com.pyamsoft.pydroid.ui.internal.billing.dialog.BillingDialog
import com.pyamsoft.pydroid.ui.internal.changelog.dialog.ChangeLogDialog
import com.pyamsoft.pydroid.ui.internal.datapolicy.dialog.DataPolicyDisclosureDialog
import com.pyamsoft.pydroid.ui.internal.debug.InAppDebugDialog
import com.pyamsoft.pydroid.ui.internal.settings.SettingsInjector
import com.pyamsoft.pydroid.ui.internal.settings.SettingsScreen
import com.pyamsoft.pydroid.ui.internal.settings.SettingsViewModeler
import com.pyamsoft.pydroid.ui.internal.settings.SettingsViewState
import com.pyamsoft.pydroid.ui.internal.settings.reset.ResetDialog
import com.pyamsoft.pydroid.ui.internal.version.VersionCheckViewModeler
import com.pyamsoft.pydroid.ui.preference.Preferences
import com.pyamsoft.pydroid.ui.theme.Theming
import com.pyamsoft.pydroid.ui.theme.ZeroElevation
import com.pyamsoft.pydroid.ui.util.rememberActivity
import com.pyamsoft.pydroid.ui.util.rememberNotNull
import com.pyamsoft.pydroid.util.MarketLinker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
private fun MountHooks(
    viewModel: SettingsViewModeler,
) {
  LaunchedEffect(viewModel) { viewModel.bind(scope = this) }
}

/** Composable for displaying a settings page */
@Composable
public fun SettingsPage(
    modifier: Modifier = Modifier,
    shape: Shape = RectangleShape,
    hideUpgradeInformation: Boolean = false,
    hideClearAll: Boolean = false,
    customPrePreferences: SnapshotStateList<Preferences> = remember { mutableStateListOf() },
    customPostPreferences: SnapshotStateList<Preferences> = remember { mutableStateListOf() },
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
  val changeLogViewModel = rememberNotNull(component.changeLogViewModel)
  val billingViewModel = rememberNotNull(component.billingViewModel)

  val handleOpenPage = { url: String ->
    try {
      uriHandler.openUri(url)
    } catch (e: Throwable) {
      Logger.e(e, "Unable to open Activity for URL: $url")
    }
  }

  MountHooks(
      viewModel = viewModel,
  )

  SaveStateDisposableEffect(billingViewModel)
  SaveStateDisposableEffect(changeLogViewModel)
  SaveStateDisposableEffect(viewModel)

  SettingsContent(
      modifier = modifier,
      shape = shape,
      state = viewModel.state,
      billingState = billingViewModel.state,
      changeLogState = changeLogViewModel.state,
      options = options,
      hideClearAll = hideClearAll,
      hideUpgradeInformation = hideUpgradeInformation,
      customElevation = customElevation,
      customTopItemMargin = customTopItemMargin,
      customBottomItemMargin = customBottomItemMargin,
      customPrePreferences = customPrePreferences,
      customPostPreferences = customPostPreferences,
      onLicensesClicked = { viewModel.handleOpenAboutDialog() },
      onCheckUpdateClicked = {
        if (options.disableVersionCheck) {
          Logger.w("Application has disabled the VersionCheck component")
        } else {
          versionViewModel.handleCheckForUpdates(
              scope = scope,
              force = true,
              onLaunchUpdate = { launcher ->
                // Mark the dialog as open, so that once the update data is fully downloaded it
                // will pop up on screen
                versionViewModel.handleOpenDialog()

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
      },
      onShowChangeLogClicked = { changeLogViewModel.handleShowDialog() },
      onResetClicked = { viewModel.handleOpenResetDialog() },
      onDonateClicked = { billingViewModel.handleOpenDialog() },
      onViewDataPolicyClicked = { viewModel.handleOpenDataPolicyDialog() },
      onBugReportClicked = { viewModel.handleReportBug(handleOpenPage) },
      onViewSourceClicked = { viewModel.handleViewSourceCode(handleOpenPage) },
      onViewPrivacyPolicyClicked = { viewModel.handleViewPrivacyPolicy(handleOpenPage) },
      onViewTermsOfServiceClicked = { viewModel.handleViewTermsOfService(handleOpenPage) },
      onViewSocialMediaClicked = { viewModel.handleViewSocialMedia(handleOpenPage) },
      onViewBlogClicked = { viewModel.handleViewBlog(handleOpenPage) },
      onOpenMarketPage = { handleOpenPage(MarketLinker.getStorePageLink(activity)) },
      onDismissDataPolicyDialog = { viewModel.handleCloseDataPolicyDialog() },
      onDismissResetDialog = { viewModel.handleCloseResetDialog() },
      onDismissAboutDialog = { viewModel.handleCloseAboutDialog() },
      onDismissBillingDialog = { billingViewModel.handleCloseDialog() },
      onDismissChangeLogDialog = { changeLogViewModel.handleCloseDialog() },
      onDarkModeChanged = {
        viewModel.handleChangeDarkMode(
            scope = scope,
            mode = it,
        )
      },
      onInAppDebuggingChanged = { viewModel.handleChangeInAppDebugEnabled(scope = scope) },
      onDismissInAppDebuggingDialog = { viewModel.handleCloseInAppDebuggingDialog() },
      onInAppDebuggingClicked = { viewModel.handleOpenInAppDebuggingDialog() },
  )
}

/** Composable for displaying a settings page */
@Composable
private fun SettingsContent(
    modifier: Modifier = Modifier,
    state: SettingsViewState,
    billingState: BillingViewState,
    changeLogState: ChangeLogViewState,
    options: PYDroidActivityOptions,
    shape: Shape,
    hideUpgradeInformation: Boolean,
    hideClearAll: Boolean,
    customPrePreferences: SnapshotStateList<Preferences>,
    customPostPreferences: SnapshotStateList<Preferences>,
    customTopItemMargin: Dp,
    customBottomItemMargin: Dp,
    customElevation: Dp,
    onDarkModeChanged: (Theming.Mode) -> Unit,
    onLicensesClicked: () -> Unit,
    onCheckUpdateClicked: () -> Unit,
    onShowChangeLogClicked: () -> Unit,
    onResetClicked: () -> Unit,
    onDonateClicked: () -> Unit,
    onBugReportClicked: () -> Unit,
    onViewSourceClicked: () -> Unit,
    onViewDataPolicyClicked: () -> Unit,
    onViewPrivacyPolicyClicked: () -> Unit,
    onViewTermsOfServiceClicked: () -> Unit,
    onViewSocialMediaClicked: () -> Unit,
    onViewBlogClicked: () -> Unit,
    onOpenMarketPage: () -> Unit,
    onInAppDebuggingChanged: () -> Unit,
    onInAppDebuggingClicked: () -> Unit,
    onDismissAboutDialog: () -> Unit,
    onDismissBillingDialog: () -> Unit,
    onDismissChangeLogDialog: () -> Unit,
    onDismissResetDialog: () -> Unit,
    onDismissDataPolicyDialog: () -> Unit,
    onDismissInAppDebuggingDialog: () -> Unit,
) {
  val showResetDialog by state.isShowingResetDialog.collectAsState()
  val showDataPolicyDialog by state.isShowingDataPolicyDialog.collectAsState()
  val showAboutDialog by state.isShowingAboutDialog.collectAsState()
  val showBillingDialog by billingState.isShowingDialog.collectAsState()
  val showChangeLogDialog by changeLogState.isShowingDialog.collectAsState()
  val showInAppDebuggingDialog by state.isShowingInAppDebugDialog.collectAsState()

  SettingsScreen(
      modifier = modifier,
      shape = shape,
      elevation = customElevation,
      state = state,
      options = options,
      hideClearAll = hideClearAll,
      hideUpgradeInformation = hideUpgradeInformation,
      topItemMargin = customTopItemMargin,
      bottomItemMargin = customBottomItemMargin,
      customPreContent = customPrePreferences,
      customPostContent = customPostPreferences,
      onDarkModeChanged = onDarkModeChanged,
      onLicensesClicked = onLicensesClicked,
      onCheckUpdateClicked = onCheckUpdateClicked,
      onShowChangeLogClicked = onShowChangeLogClicked,
      onResetClicked = onResetClicked,
      onDonateClicked = onDonateClicked,
      onBugReportClicked = onBugReportClicked,
      onViewSourceClicked = onViewSourceClicked,
      onViewDataPolicyClicked = onViewDataPolicyClicked,
      onViewPrivacyPolicyClicked = onViewPrivacyPolicyClicked,
      onViewTermsOfServiceClicked = onViewTermsOfServiceClicked,
      onViewSocialMediaClicked = onViewSocialMediaClicked,
      onViewBlogClicked = onViewBlogClicked,
      onOpenMarketPage = onOpenMarketPage,
      onInAppDebuggingChanged = onInAppDebuggingChanged,
      onInAppDebuggingClicked = onInAppDebuggingClicked,
  )

  if (showDataPolicyDialog) {
    DataPolicyDisclosureDialog(
        onDismiss = onDismissDataPolicyDialog,
    )
  }

  if (showResetDialog) {
    ResetDialog(
        onDismiss = onDismissResetDialog,
    )
  }

  if (showAboutDialog) {
    AboutDialog(
        onDismiss = onDismissAboutDialog,
    )
  }

  if (showBillingDialog) {
    BillingDialog(
        onDismiss = onDismissBillingDialog,
    )
  }

  if (showChangeLogDialog) {
    ChangeLogDialog(
        onDismiss = onDismissChangeLogDialog,
    )
  }

  if (showInAppDebuggingDialog) {
    InAppDebugDialog(
        onDismiss = onDismissInAppDebuggingDialog,
    )
  }
}
