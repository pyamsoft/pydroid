/*
 * Copyright 2024 pyamsoft
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

package com.pyamsoft.pydroid.ui.settings

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pyamsoft.pydroid.arch.SaveStateDisposableEffect
import com.pyamsoft.pydroid.core.Logger
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
import com.pyamsoft.pydroid.ui.preference.Preferences
import com.pyamsoft.pydroid.ui.theme.Theming
import com.pyamsoft.pydroid.ui.theme.ZeroElevation
import com.pyamsoft.pydroid.ui.theme.ZeroSize
import com.pyamsoft.pydroid.ui.uri.rememberUriHandler
import com.pyamsoft.pydroid.ui.util.rememberNotNull
import com.pyamsoft.pydroid.util.MarketLinker

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
    dialogModifier: Modifier = Modifier,
    shape: Shape = RectangleShape,
    hideUpgradeInformation: Boolean = false,
    hideClearAll: Boolean = false,
    customPrePreferences: SnapshotStateList<Preferences> = remember { mutableStateListOf() },
    customPostPreferences: SnapshotStateList<Preferences> = remember { mutableStateListOf() },
    customTopItemMargin: Dp = ZeroSize,
    customBottomItemMargin: Dp = ZeroSize,
    customElevation: Dp = ZeroElevation,
    extraDebugContent: LazyListScope.() -> Unit = {},
) {
  val scope = rememberCoroutineScope()
  val context = LocalContext.current

  val component = rememberComposableInjector { SettingsInjector() }
  val options = rememberNotNull(component.options)

  val viewModel = rememberNotNull(component.viewModel)
  val versionViewModel = rememberNotNull(component.versionViewModel)
  val changeLogViewModel = rememberNotNull(component.changeLogViewModel)
  val billingViewModel = rememberNotNull(component.billingViewModel)

  val uriHandler = rememberUriHandler()
  val handleOpenPage by rememberUpdatedState { url: String -> uriHandler.openUri(url) }

  MountHooks(
      viewModel = viewModel,
  )

  SaveStateDisposableEffect(billingViewModel)
  SaveStateDisposableEffect(changeLogViewModel)
  SaveStateDisposableEffect(viewModel)

  SettingsContent(
      modifier = modifier,
      dialogModifier = dialogModifier,
      shape = shape,
      state = viewModel,
      billingState = billingViewModel,
      changeLogState = changeLogViewModel,
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
          Logger.w { "Application has disabled the VersionCheck component" }
        } else {
          versionViewModel.handleCheckForUpdates(
              scope = scope,
              force = true,
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
      onOpenMarketPage = { handleOpenPage(MarketLinker.getStorePageLink(context)) },
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
      onInAppDebuggingChanged = { viewModel.handleChangeInAppDebugEnabled() },
      onDismissInAppDebuggingDialog = { viewModel.handleCloseInAppDebuggingDialog() },
      onInAppDebuggingClicked = { viewModel.handleOpenInAppDebuggingDialog() },
      onHapticsChanged = { viewModel.handleHapticsChanged(it) },
      extraDebugContent = extraDebugContent,
  )
}

/** Composable for displaying a settings page */
@Composable
private fun SettingsContent(
    modifier: Modifier = Modifier,
    dialogModifier: Modifier = Modifier,
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
    onHapticsChanged: (Boolean) -> Unit,
    extraDebugContent: LazyListScope.() -> Unit,
) {
  val showResetDialog by state.isShowingResetDialog.collectAsStateWithLifecycle()
  val showDataPolicyDialog by state.isShowingDataPolicyDialog.collectAsStateWithLifecycle()
  val showAboutDialog by state.isShowingAboutDialog.collectAsStateWithLifecycle()
  val showBillingDialog by billingState.isShowingDialog.collectAsStateWithLifecycle()
  val showChangeLogDialog by changeLogState.isShowingDialog.collectAsStateWithLifecycle()
  val showInAppDebuggingDialog by state.isShowingInAppDebugDialog.collectAsStateWithLifecycle()

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
      onHapticsChanged = onHapticsChanged,
  )

  if (showDataPolicyDialog) {
    DataPolicyDisclosureDialog(
        modifier = dialogModifier,
        onDismiss = onDismissDataPolicyDialog,
    )
  }

  if (showResetDialog) {
    ResetDialog(
        modifier = dialogModifier,
        onDismiss = onDismissResetDialog,
    )
  }

  if (showAboutDialog) {
    AboutDialog(
        modifier = dialogModifier,
        onDismiss = onDismissAboutDialog,
    )
  }

  if (showBillingDialog) {
    BillingDialog(
        modifier = dialogModifier,
        onDismiss = onDismissBillingDialog,
    )
  }

  if (showChangeLogDialog) {
    ChangeLogDialog(
        modifier = dialogModifier,
        onDismiss = onDismissChangeLogDialog,
    )
  }

  if (showInAppDebuggingDialog) {
    InAppDebugDialog(
        modifier = dialogModifier,
        onDismiss = onDismissInAppDebuggingDialog,
        extraContent = extraDebugContent,
    )
  }
}
