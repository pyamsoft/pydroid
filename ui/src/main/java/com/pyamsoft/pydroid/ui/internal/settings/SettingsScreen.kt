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

package com.pyamsoft.pydroid.ui.internal.settings

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pyamsoft.pydroid.theme.keylines
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.app.PYDroidActivityOptions
import com.pyamsoft.pydroid.ui.internal.settings.version.VersionCheckingSettingsState
import com.pyamsoft.pydroid.ui.preference.PreferenceScreen
import com.pyamsoft.pydroid.ui.preference.Preferences
import com.pyamsoft.pydroid.ui.theme.Theming
import com.pyamsoft.pydroid.ui.theme.ZeroSize
import com.pyamsoft.pydroid.ui.version.VersionCheckViewState.CheckingState
import com.pyamsoft.pydroid.util.Logger

@Composable
internal fun SettingsScreen(
    modifier: Modifier = Modifier,
    topItemMargin: Dp,
    bottomItemMargin: Dp,
    state: SettingsViewState,
    appViewState: SettingsAppViewState,
    versionCheckingState: VersionCheckingSettingsState,
    options: PYDroidActivityOptions,
    hideClearAll: Boolean,
    hideUpgradeInformation: Boolean,
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
    onInAppDebuggingChanged: () -> Unit,
    onHapticsChanged: (Boolean) -> Unit,
    onBillingUpsellDisabledChanged: (Boolean) -> Unit,
    onUpdateCheckComplete: () -> Unit,
) {
  val snackbarHostState = remember { SnackbarHostState() }

  val loadingState by state.loadingState.collectAsStateWithLifecycle()
  val applicationName by state.applicationName.collectAsStateWithLifecycle()

  val themeMode by appViewState.themeMode.collectAsStateWithLifecycle()
  val isMaterialYou by appViewState.isMaterialYou.collectAsStateWithLifecycle()
  val isHapticsEnabled by appViewState.isHapticsEnabled.collectAsStateWithLifecycle()

  val isInAppDebugEnabled by state.isInAppDebuggingEnabled.collectAsStateWithLifecycle()
  val isBillingUpsellDisabled by state.isBillingUpsellDisabled.collectAsStateWithLifecycle()

  Crossfade(
      modifier = modifier,
      label = "Settings",
      targetState = loadingState,
  ) { loading ->
    when (loading) {
      SettingsViewState.LoadingState.NONE,
      SettingsViewState.LoadingState.LOADING -> {
        Loading()
      }

      SettingsViewState.LoadingState.DONE -> {
        SettingsList(
            snackbarHost = snackbarHostState,
            appViewState = appViewState,
            versionCheckingState = versionCheckingState,
            applicationName = applicationName,
            themeMode = themeMode,
            isMaterialYou = isMaterialYou,
            options = options,
            topItemMargin = topItemMargin,
            bottomItemMargin = bottomItemMargin,
            isInAppDebugChecked = isInAppDebugEnabled,
            isHapticsEnabled = isHapticsEnabled,
            isBillingUpsellDisabled = isBillingUpsellDisabled,
            hideClearAll = hideClearAll,
            hideUpgradeInformation = hideUpgradeInformation,
            onThemeModeChanged = onThemeModeChanged,
            onMaterialYouChange = onMaterialYouChange,
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
            onHapticsChanged = onHapticsChanged,
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
    snackbarHost: SnackbarHostState,
    appViewState: SettingsAppViewState,
    versionCheckingState: VersionCheckingSettingsState,
    options: PYDroidActivityOptions,
    topItemMargin: Dp,
    bottomItemMargin: Dp,
    hideClearAll: Boolean,
    hideUpgradeInformation: Boolean,
    isInAppDebugChecked: Boolean,
    isHapticsEnabled: Boolean,
    isBillingUpsellDisabled: Boolean,
    applicationName: CharSequence,
    themeMode: Theming.Mode,
    isMaterialYou: Boolean,
    onMaterialYouChange: (Boolean) -> Unit,
    onThemeModeChanged: (Theming.Mode) -> Unit,
    onHapticsChanged: (Boolean) -> Unit,
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
    onInAppDebuggingChanged: () -> Unit,
    onBillingUpsellDisabledChanged: (Boolean) -> Unit,
    onUpdateCheckComplete: () -> Unit,
) {
  val applicationPrefs =
      rememberApplicationPreferencesGroup(
          options = options,
          isHapticsEnabled = isHapticsEnabled,
          hideUpgradeInformation = hideUpgradeInformation,
          applicationName = applicationName,
          themeMode = themeMode,
          isMaterialYou = isMaterialYou,
          onHapticsChanged = onHapticsChanged,
          onMaterialYouChange = onMaterialYouChange,
          onThemeModeChanged = onThemeModeChanged,
          onLicensesClicked = onLicensesClicked,
          onCheckUpdateClicked = onCheckUpdateClicked,
          onShowChangeLogClicked = onShowChangeLogClicked,
      )

  val supportPrefs =
      rememberSupportPreferencesGroup(
          options = options,
          applicationName = applicationName,
          isBillingUpsellDisabled = isBillingUpsellDisabled,
          onDonateClicked = onDonateClicked,
          onOpenMarketPage = onOpenMarketPage,
          onBillingUpsellDisabledChanged = onBillingUpsellDisabledChanged,
      )

  val infoPreferences =
      rememberInfoPreferencesGroup(
          options = options,
          applicationName = applicationName,
          onBugReportClicked = onBugReportClicked,
          onViewSourceClicked = onViewSourceClicked,
          onViewDataPolicyClicked = onViewDataPolicyClicked,
          onViewPrivacyPolicyClicked = onViewPrivacyPolicyClicked,
          onViewTermsOfServiceClicked = onViewTermsOfServiceClicked,
      )

  val socialMediaPreferences =
      rememberSocialMediaPreferencesGroup(
          onViewSocialMediaClicked = onViewSocialMediaClicked,
          onViewBlogClicked = onViewBlogClicked,
      )

  val dangerZonePreferences =
      rememberDangerZonePreferencesGroup(
          isInAppDebugChecked = isInAppDebugChecked,
          hideClearAll = hideClearAll,
          onResetClicked = onResetClicked,
          onInAppDebuggingChanged = onInAppDebuggingChanged,
          onInAppDebuggingClicked = onInAppDebuggingClicked,
      )

  val preferences =
      remember(
          applicationPrefs,
          supportPrefs,
          infoPreferences,
          socialMediaPreferences,
          dangerZonePreferences,
      ) {
        mutableStateListOf<Preferences>().apply {
          add(applicationPrefs)
          add(supportPrefs)
          add(infoPreferences)
          add(socialMediaPreferences)
          add(dangerZonePreferences)
        }
      }

  Box(
      modifier = modifier,
  ) {
    PreferenceScreen(
        appViewState = appViewState,
        topItemMargin = topItemMargin,
        bottomItemMargin = bottomItemMargin,
        preferences = preferences,
    )

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
      appViewState = state,
      versionCheckingState = VersionCheckingSettingsState.empty(),
      hideClearAll = false,
      hideUpgradeInformation = false,
      topItemMargin = ZeroSize,
      bottomItemMargin = ZeroSize,
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
