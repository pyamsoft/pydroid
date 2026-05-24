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

package com.pyamsoft.pydroid.ui.settings

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.pyamsoft.pydroid.arch.SaveStateDisposableEffect
import com.pyamsoft.pydroid.ui.app.PYDroidActivityOptions
import com.pyamsoft.pydroid.ui.billing.BillingViewState
import com.pyamsoft.pydroid.ui.changelog.ChangeLogViewState
import com.pyamsoft.pydroid.ui.inject.rememberComposableInjector
import com.pyamsoft.pydroid.ui.internal.about.AboutDialog
import com.pyamsoft.pydroid.ui.internal.billing.dialog.BillingDialog
import com.pyamsoft.pydroid.ui.internal.changelog.dialog.ChangeLogDialog
import com.pyamsoft.pydroid.ui.internal.datapolicy.dialog.DataPolicyDisclosureDialog
import com.pyamsoft.pydroid.ui.internal.debug.InAppDebugDialog
import com.pyamsoft.pydroid.ui.internal.settings.SettingsDangerZoneViewState
import com.pyamsoft.pydroid.ui.internal.settings.SettingsInAppInteractionViewState
import com.pyamsoft.pydroid.ui.internal.settings.SettingsInjector
import com.pyamsoft.pydroid.ui.internal.settings.SettingsScreen
import com.pyamsoft.pydroid.ui.internal.settings.SettingsUIViewState
import com.pyamsoft.pydroid.ui.internal.settings.SettingsViewModeler
import com.pyamsoft.pydroid.ui.internal.settings.SettingsViewState
import com.pyamsoft.pydroid.ui.internal.settings.reset.ResetDialog
import com.pyamsoft.pydroid.ui.internal.settings.version.VersionCheckingSettingsState
import com.pyamsoft.pydroid.ui.theme.Theming
import com.pyamsoft.pydroid.ui.uri.rememberUriHandler
import com.pyamsoft.pydroid.ui.util.rememberNotNull
import com.pyamsoft.pydroid.ui.version.VersionCheckViewState
import com.pyamsoft.pydroid.util.Logger
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
    listState: LazyListState = rememberLazyListState(),
    extraDebugContent: LazyListScope.() -> Unit = {},
) {
  // Use the LifecycleOwner.CoroutineScope (Activity usually)
  // so that the scope does not die because of navigation events
  val owner = LocalLifecycleOwner.current
  val lifecycleScope = owner.lifecycleScope

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
      listState = listState,
      state = viewModel,
      uiViewState = viewModel,
      inAppInteractionViewState = viewModel,
      dangerZoneViewState = viewModel,
      billingState = billingViewModel,
      changeLogState = changeLogViewModel,
      versionCheckViewState = versionViewModel,
      options = options,
      onLicensesClicked = { viewModel.handleOpenAboutDialog() },
      onCheckUpdateClicked = {
        if (options.disableVersionCheck) {
          Logger.w { "Application has disabled the VersionCheck component" }
        } else {
          versionViewModel.handleCheckForUpdates(
              scope = lifecycleScope,
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
      onThemeModeChanged = {
        viewModel.handleChangeThemeMode(
            scope = lifecycleScope,
            mode = it,
        )
      },
      onMaterialYouChange = { viewModel.handleMaterialYouChange(it) },
      onInAppDebuggingChanged = { viewModel.handleChangeInAppDebugEnabled(it) },
      onDismissInAppDebuggingDialog = { viewModel.handleCloseInAppDebuggingDialog() },
      onInAppDebuggingClicked = { viewModel.handleOpenInAppDebuggingDialog() },
      onHapticsChanged = { viewModel.handleHapticsChanged(it) },
      onBillingUpsellDisabledChanged = { viewModel.handleBillingUpsellDisabledChanged(it) },
      onUpdateCheckComplete = { versionViewModel.handleManualUpdateCheckComplete() },
      extraDebugContent = extraDebugContent,
  )
}

/** Composable for displaying a settings page */
@Composable
private fun SettingsContent(
    modifier: Modifier = Modifier,
    dialogModifier: Modifier = Modifier,
    listState: LazyListState,
    state: SettingsViewState,
    uiViewState: SettingsUIViewState,
    dangerZoneViewState: SettingsDangerZoneViewState,
    inAppInteractionViewState: SettingsInAppInteractionViewState,
    versionCheckViewState: VersionCheckViewState,
    billingState: BillingViewState,
    changeLogState: ChangeLogViewState,
    options: PYDroidActivityOptions,
    onMaterialYouChange: (Boolean) -> Unit,
    onThemeModeChanged: (Theming.Mode) -> Unit,
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
    onInAppDebuggingChanged: (Boolean) -> Unit,
    onInAppDebuggingClicked: () -> Unit,
    onBillingUpsellDisabledChanged: (Boolean) -> Unit,
    onDismissAboutDialog: () -> Unit,
    onDismissBillingDialog: () -> Unit,
    onDismissChangeLogDialog: () -> Unit,
    onDismissResetDialog: () -> Unit,
    onDismissDataPolicyDialog: () -> Unit,
    onDismissInAppDebuggingDialog: () -> Unit,
    onHapticsChanged: (Boolean) -> Unit,
    onUpdateCheckComplete: () -> Unit,
    extraDebugContent: LazyListScope.() -> Unit,
) {
  val showResetDialog by state.isShowingResetDialog.collectAsStateWithLifecycle()
  val showDataPolicyDialog by state.isShowingDataPolicyDialog.collectAsStateWithLifecycle()
  val showAboutDialog by state.isShowingAboutDialog.collectAsStateWithLifecycle()
  val showBillingDialog by billingState.isShowingDialog.collectAsStateWithLifecycle()
  val showChangeLogDialog by changeLogState.isShowingDialog.collectAsStateWithLifecycle()
  val showInAppDebuggingDialog by state.isShowingInAppDebugDialog.collectAsStateWithLifecycle()

  val versionCheckStatus by versionCheckViewState.isCheckingForUpdate.collectAsStateWithLifecycle()
  val versionCheckLauncher by versionCheckViewState.launcher.collectAsStateWithLifecycle()

  val versionCheckingState =
      remember(versionCheckStatus, versionCheckLauncher) {
        VersionCheckingSettingsState(
            isChecking = versionCheckStatus,
            isEmptyUpdate =
                versionCheckLauncher.let { it != null && it.availableUpdateVersion() <= 0 },
            newVersion = versionCheckLauncher?.availableUpdateVersion() ?: 0,
        )
      }

  SettingsScreen(
      modifier = modifier,
      versionCheckingState = versionCheckingState,
      listState = listState,
      state = state,
      uiViewState = uiViewState,
      inAppInteractionViewState = inAppInteractionViewState,
      dangerZoneViewState = dangerZoneViewState,
      options = options,
      onMaterialYouChange = onMaterialYouChange,
      onThemeModeChanged = onThemeModeChanged,
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
      onBillingUpsellDisabledChanged = onBillingUpsellDisabledChanged,
      onUpdateCheckComplete = onUpdateCheckComplete,
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
