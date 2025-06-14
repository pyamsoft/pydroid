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
    versionCheckingState: VersionCheckingSettingsState,
    options: PYDroidActivityOptions,
    hideClearAll: Boolean,
    hideUpgradeInformation: Boolean,
    customPreContent: List<Preferences>,
    customPostContent: List<Preferences>,
    onMaterialYouChange: (Boolean) -> Unit,
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
    onInAppDebuggingClicked: () -> Unit,
    onInAppDebuggingChanged: () -> Unit,
    onHapticsChanged: (Boolean) -> Unit,
    onUpdateCheckComplete: () -> Unit,
) {
  val snackbarHostState = remember { SnackbarHostState() }

  val loadingState by state.loadingState.collectAsStateWithLifecycle()
  val applicationName by state.applicationName.collectAsStateWithLifecycle()

  val darkMode by state.darkMode.collectAsStateWithLifecycle()
  val isMaterialYou by state.isMaterialYou.collectAsStateWithLifecycle()

  val isHapticsEnabled by state.isHapticsEnabled.collectAsStateWithLifecycle()
  val isInAppDebugEnabled by state.isInAppDebuggingEnabled.collectAsStateWithLifecycle()

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
            versionCheckingState = versionCheckingState,
            applicationName = applicationName,
            darkMode = darkMode,
            isMaterialYou = isMaterialYou,
            options = options,
            topItemMargin = topItemMargin,
            bottomItemMargin = bottomItemMargin,
            customPreContent = customPreContent,
            customPostContent = customPostContent,
            isInAppDebugChecked = isInAppDebugEnabled,
            isHapticsEnabled = isHapticsEnabled,
            hideClearAll = hideClearAll,
            hideUpgradeInformation = hideUpgradeInformation,
            onDarkModeChanged = onDarkModeChanged,
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
    versionCheckingState: VersionCheckingSettingsState,
    options: PYDroidActivityOptions,
    topItemMargin: Dp,
    bottomItemMargin: Dp,
    customPreContent: List<Preferences>,
    customPostContent: List<Preferences>,
    hideClearAll: Boolean,
    hideUpgradeInformation: Boolean,
    isInAppDebugChecked: Boolean,
    isHapticsEnabled: Boolean,
    applicationName: CharSequence,
    darkMode: Theming.Mode,
    isMaterialYou: Boolean,
    onMaterialYouChange: (Boolean) -> Unit,
    onDarkModeChanged: (Theming.Mode) -> Unit,
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
    onUpdateCheckComplete: () -> Unit,
) {
  val applicationPrefs =
      rememberApplicationPreferencesGroup(
          options = options,
          isHapticsEnabled = isHapticsEnabled,
          hideUpgradeInformation = hideUpgradeInformation,
          applicationName = applicationName,
          darkMode = darkMode,
          isMaterialYou = isMaterialYou,
          onHapticsChanged = onHapticsChanged,
          onMaterialYouChange = onMaterialYouChange,
          onDarkModeChanged = onDarkModeChanged,
          onLicensesClicked = onLicensesClicked,
          onCheckUpdateClicked = onCheckUpdateClicked,
          onShowChangeLogClicked = onShowChangeLogClicked,
      )

  val supportPrefs =
      rememberSupportPreferencesGroup(
          options = options,
          applicationName = applicationName,
          onDonateClicked = onDonateClicked,
          onOpenMarketPage = onOpenMarketPage,
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
          customPreContent,
          customPostContent,
          applicationPrefs,
          supportPrefs,
          infoPreferences,
          socialMediaPreferences,
          dangerZonePreferences,
      ) {
        mutableStateListOf<Preferences>().apply {
          addAll(customPreContent)
          add(applicationPrefs)
          add(supportPrefs)
          add(infoPreferences)
          add(socialMediaPreferences)
          add(dangerZonePreferences)
          addAll(customPostContent)
        }
      }

  Box(
      modifier = modifier,
  ) {
    PreferenceScreen(
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
  SettingsScreen(
      options = PYDroidActivityOptions(),
      state =
          MutableSettingsViewState().apply {
            this.loadingState.value = loadingState
            applicationName.value = "TEST"
            darkMode.value = Theming.Mode.LIGHT
          },
      versionCheckingState = VersionCheckingSettingsState.empty(),
      hideClearAll = false,
      hideUpgradeInformation = false,
      topItemMargin = ZeroSize,
      bottomItemMargin = ZeroSize,
      customPreContent = remember { mutableStateListOf() },
      customPostContent = remember { mutableStateListOf() },
      onDarkModeChanged = {},
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
