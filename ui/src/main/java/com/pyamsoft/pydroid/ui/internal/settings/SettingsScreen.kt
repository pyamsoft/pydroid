/*
 * Copyright 2026 pyamsoft
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

package com.pyamsoft.pydroid.ui.internal.settings

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pyamsoft.pydroid.theme.keylines
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.app.PYDroidActivityOptions
import com.pyamsoft.pydroid.ui.internal.settings.section.renderDangerZoneSettings
import com.pyamsoft.pydroid.ui.internal.settings.section.renderExternalLinksSettings
import com.pyamsoft.pydroid.ui.internal.settings.section.renderInAppInteractionSettings
import com.pyamsoft.pydroid.ui.internal.settings.section.renderPublisherLinksSettings
import com.pyamsoft.pydroid.ui.internal.settings.section.renderUISettings
import com.pyamsoft.pydroid.ui.internal.settings.version.VersionCheckingSettingsState
import com.pyamsoft.pydroid.ui.theme.Theming
import com.pyamsoft.pydroid.ui.version.VersionCheckViewState.CheckingState
import com.pyamsoft.pydroid.util.Logger

@Composable
internal fun SettingsScreen(
    modifier: Modifier = Modifier,
    listState: LazyListState,
    state: SettingsViewState,
    uiViewState: SettingsUIViewState,
    dangerZoneViewState: SettingsDangerZoneViewState,
    inAppInteractionViewState: SettingsInAppInteractionViewState,
    versionCheckingState: VersionCheckingSettingsState,
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
    onInAppDebuggingClicked: () -> Unit,
    onInAppDebuggingChanged: (Boolean) -> Unit,
    onHapticsChanged: (Boolean) -> Unit,
    onBillingUpsellDisabledChanged: (Boolean) -> Unit,
    onUpdateCheckComplete: () -> Unit,
) {
  val snackbarHostState = remember { SnackbarHostState() }

  val loadingState by state.loadingState.collectAsStateWithLifecycle()
  val applicationName by state.applicationName.collectAsStateWithLifecycle()

  val themeMode by uiViewState.themeMode.collectAsStateWithLifecycle()
  val isMaterialYou by uiViewState.isMaterialYou.collectAsStateWithLifecycle()
  val isHapticsEnabled by uiViewState.isHapticsEnabled.collectAsStateWithLifecycle()

  val isBillingUpsellDisabled by
      inAppInteractionViewState.isBillingUpsellDisabled.collectAsStateWithLifecycle()

  val isInAppDebugEnabled by
      dangerZoneViewState.isInAppDebuggingEnabled.collectAsStateWithLifecycle()

  Crossfade(
      modifier = modifier,
      label = "Settings",
      targetState = loadingState,
  ) { loading ->
    when (loading) {
      SettingsViewState.LoadingState.NONE,
      SettingsViewState.LoadingState.LOADING,
      -> {
        Loading()
      }

      SettingsViewState.LoadingState.DONE -> {
        SettingsList(
            listState = listState,
            snackbarHost = snackbarHostState,
            uiViewState = uiViewState,
            inAppInteractionViewState = inAppInteractionViewState,
            dangerZoneViewState = dangerZoneViewState,
            versionCheckingState = versionCheckingState,
            applicationName = applicationName,
            themeMode = themeMode,
            isMaterialYou = isMaterialYou,
            options = options,
            isInAppDebugChecked = isInAppDebugEnabled,
            isHapticsEnabled = isHapticsEnabled,
            isBillingUpsellDisabled = isBillingUpsellDisabled,
            onThemeModeChanged = onThemeModeChanged,
            onMaterialYouChanged = onMaterialYouChange,
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
            onInAppDebuggingClicked = onInAppDebuggingClicked,
            onInAppDebuggingChanged = onInAppDebuggingChanged,
            onHapticFeedbackChanged = onHapticsChanged,
            onBillingUpsellDisabledChanged = onBillingUpsellDisabledChanged,
            onUpdateCheckComplete = onUpdateCheckComplete,
        )
      }
    }
  }
}

@Composable
private fun Loading() {
  Box(
      modifier =
          Modifier.systemBarsPadding()
              .fillMaxHeight()
              .fillMaxWidth()
              .padding(
                  all = MaterialTheme.keylines.content,
              ),
      contentAlignment = Alignment.Center,
  ) {
    CircularProgressIndicator()
  }
}

@Composable
private fun SettingsList(
    modifier: Modifier = Modifier,
    listState: LazyListState,
    snackbarHost: SnackbarHostState,
    uiViewState: SettingsUIViewState,
    inAppInteractionViewState: SettingsInAppInteractionViewState,
    dangerZoneViewState: SettingsDangerZoneViewState,
    versionCheckingState: VersionCheckingSettingsState,
    options: PYDroidActivityOptions,
    isInAppDebugChecked: Boolean,
    isHapticsEnabled: Boolean,
    isBillingUpsellDisabled: Boolean,
    applicationName: String,
    themeMode: Theming.Mode,
    isMaterialYou: Boolean,
    onMaterialYouChanged: (Boolean) -> Unit,
    onThemeModeChanged: (Theming.Mode) -> Unit,
    onHapticFeedbackChanged: (Boolean) -> Unit,
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
    onInAppDebuggingClicked: () -> Unit,
    onInAppDebuggingChanged: (Boolean) -> Unit,
    onBillingUpsellDisabledChanged: (Boolean) -> Unit,
    onUpdateCheckComplete: () -> Unit,
) {
  Box(
      modifier = modifier,
  ) {
    LazyColumn(
        state = listState,
    ) {
      renderUISettings(
          state = uiViewState,
          onThemeModeChanged = onThemeModeChanged,
          onMaterialYouChanged = onMaterialYouChanged,
          onHapticFeedbackChanged = onHapticFeedbackChanged,
      )

      renderAppSettings(
          options = options,
          onCheckUpdateClicked = onCheckUpdateClicked,
          onShowChangeLogClicked = onShowChangeLogClicked,
      )

      renderInAppInteractionSettings(
          options = options,
          appName = applicationName,
          state = inAppInteractionViewState,
          onBillingUpsellDisabledChanged = onBillingUpsellDisabledChanged,
          onDonateClicked = onDonateClicked,
          onOpenMarketPage = onOpenMarketPage,
      )

      renderExternalLinksSettings(
          options = options,
          onViewTermsOfServiceClicked = onViewTermsOfServiceClicked,
          onViewPrivacyPolicyClicked = onViewPrivacyPolicyClicked,
          onViewDataPolicyClicked = onViewDataPolicyClicked,
          onLicensesClicked = onLicensesClicked,
          onBugReportClicked = onBugReportClicked,
          onViewSourceClicked = onViewSourceClicked,
      )

      renderPublisherLinksSettings(
          onViewBlogClicked = onViewBlogClicked,
          onViewSocialMediaClicked = onViewSocialMediaClicked,
      )

      renderDangerZoneSettings(
          state = dangerZoneViewState,
          onResetClicked = onResetClicked,
          onInAppDebuggingClicked = onInAppDebuggingClicked,
          onInAppDebuggingChanged = onInAppDebuggingChanged,
      )
    }

    CheckingUpdateStatus(
        modifier = Modifier.align(Alignment.BottomCenter),
        snackbarHost = snackbarHost,
        versionCheckingState = versionCheckingState,
        onUpdateCheckComplete = onUpdateCheckComplete,
    )
  }
}

@Composable
private fun CheckingUpdateStatus(
    modifier: Modifier = Modifier,
    snackbarHost: SnackbarHostState,
    versionCheckingState: VersionCheckingSettingsState,
    onUpdateCheckComplete: () -> Unit,
) {
  val isChecking = versionCheckingState.isChecking
  val isEmptyUpdate = versionCheckingState.isEmptyUpdate
  val newVersion = versionCheckingState.newVersion

  val handleUpdateCheckComplete by rememberUpdatedState(onUpdateCheckComplete)

  // In case we dismiss the dialog BEFORE the snackbar hides
  DisposableEffect(Unit) {
    onDispose {
      Logger.d { "Update check is DISMISSED, mark complete" }
      handleUpdateCheckComplete()
    }
  }

  val checkingForUpdatesMessage = stringResource(R.string.checking_for_updates)
  val emptyUpdateMessage = stringResource(R.string.empty_update)
  val updateAvailableMessage =
      stringResource(R.string.an_update_to_version_is_available, newVersion)

  LaunchedEffect(
      isChecking,
      isEmptyUpdate,
      newVersion,
      checkingForUpdatesMessage,
      emptyUpdateMessage,
      updateAvailableMessage,
  ) {
    val message: String
    when (isChecking) {
      is CheckingState.None -> {
        Logger.d { "Do nothing visual for NONE version checking state" }
        message = ""
      }

      is CheckingState.Checking -> {
        if (isChecking.force) {
          Logger.d { "Show snackbar checking for update because user requested it" }
          message = checkingForUpdatesMessage
        } else {
          Logger.d { "Silent checking for update" }
          message = ""
        }
      }

      is CheckingState.Done -> {
        if (isChecking.force) {
          if (isEmptyUpdate || newVersion <= 0) {
            Logger.d { "Done checking for update, show update is Empty" }
            message = emptyUpdateMessage
          } else {
            Logger.d { "Done checking for update, update is Available: $newVersion" }
            message = updateAvailableMessage
          }
        } else {
          Logger.d { "Done silent checking for update, newVersion=$newVersion" }
          message = ""
        }
      }
    }

    if (message.isNotBlank()) {
      snackbarHost.showSnackbar(
          message = message,
          duration = SnackbarDuration.Short,
      )

      // After the snackbar is shown in DONE state
      if (isChecking is CheckingState.Done) {
        Logger.d { "Update check is DONE, mark complete" }
        handleUpdateCheckComplete()
      }
    }
  }

  SnackbarHost(
      modifier = modifier,
      hostState = snackbarHost,
  )
}

@Composable
private fun PreviewSettingsScreen(loadingState: SettingsViewState.LoadingState) {
  val state =
      MutableSettingsViewState().apply {
        this.loadingState.value = loadingState
        applicationName.value = "TEST"
        themeMode.value = Theming.Mode.LIGHT
      }

  SettingsScreen(
      options = PYDroidActivityOptions(),
      state = state,
      listState = rememberLazyListState(),
      uiViewState = state,
      inAppInteractionViewState = state,
      dangerZoneViewState = state,
      versionCheckingState = VersionCheckingSettingsState.empty(),
      onThemeModeChanged = {},
      onLicensesClicked = {},
      onCheckUpdateClicked = {},
      onShowChangeLogClicked = {},
      onResetClicked = {},
      onOpenMarketPage = {},
      onDonateClicked = {},
      onBugReportClicked = {},
      onViewSourceClicked = {},
      onViewDataPolicyClicked = {},
      onViewPrivacyPolicyClicked = {},
      onViewTermsOfServiceClicked = {},
      onViewSocialMediaClicked = {},
      onViewBlogClicked = {},
      onInAppDebuggingClicked = {},
      onInAppDebuggingChanged = {},
      onHapticsChanged = {},
      onMaterialYouChange = {},
      onBillingUpsellDisabledChanged = {},
      onUpdateCheckComplete = {},
  )
}

@Preview
@Composable
private fun PreviewSettingsScreenDefault() {
  PreviewSettingsScreen(loadingState = SettingsViewState.LoadingState.NONE)
}

@Preview
@Composable
private fun PreviewSettingsScreenLoading() {
  PreviewSettingsScreen(loadingState = SettingsViewState.LoadingState.LOADING)
}

@Preview
@Composable
private fun PreviewSettingsScreenLoaded() {
  PreviewSettingsScreen(loadingState = SettingsViewState.LoadingState.DONE)
}
