/*
 * Copyright 2021 Peter Kenji Yamanaka
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

package com.pyamsoft.pydroid.ui.internal.settings

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.systemBarsPadding
import com.pyamsoft.pydroid.ui.preference.PreferenceScreen
import com.pyamsoft.pydroid.ui.preference.Preferences
import com.pyamsoft.pydroid.ui.theme.Theming

@Composable
@OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
internal fun SettingsScreen(
    state: SettingsViewState,
    topItemMargin: Dp,
    bottomItemMargin: Dp,
    customContent: List<Preferences>,
    onDarkModeChanged: (Theming.Mode) -> Unit,
    onLicensesClicked: () -> Unit,
    onCheckUpdateClicked: () -> Unit,
    onShowChangeLogClicked: () -> Unit,
    onResetClicked: () -> Unit,
    onRateClicked: () -> Unit,
    onDonateClicked: () -> Unit,
    onBugReportClicked: () -> Unit,
    onViewSourceClicked: () -> Unit,
    onViewPrivacyPolicy: () -> Unit,
    onViewTermsOfServiceClicked: () -> Unit,
    onViewMoreAppsClicked: () -> Unit,
    onViewSocialMediaClicked: () -> Unit,
    onViewBlogClicked: () -> Unit,
    onNavigationErrorDismissed: () -> Unit,
) {
  val isLoading = state.isLoading
  val applicationName = state.applicationName
  val darkMode = state.darkMode
  val hideClearAll = state.hideClearAll
  val hideUpgradeInformation = state.hideUpgradeInformation
  val navigationError = state.navigationError

  val scaffoldState = rememberScaffoldState()
  Scaffold(
      scaffoldState = scaffoldState,
  ) {
    Crossfade(
        targetState = isLoading,
    ) { loading ->
      if (loading) {
        Loading()
      } else {
        SettingsList(
            topItemMargin = topItemMargin,
            bottomItemMargin = bottomItemMargin,
            customContent = customContent,
            hideClearAll = hideClearAll,
            hideUpgradeInformation = hideUpgradeInformation,
            applicationName = applicationName,
            darkMode = darkMode,
            onDarkModeChanged = onDarkModeChanged,
            onLicensesClicked = onLicensesClicked,
            onCheckUpdateClicked = onCheckUpdateClicked,
            onShowChangeLogClicked = onShowChangeLogClicked,
            onResetClicked = onResetClicked,
            onRateClicked = onRateClicked,
            onDonateClicked = onDonateClicked,
            onBugReportClicked = onBugReportClicked,
            onViewSourceClicked = onViewSourceClicked,
            onViewPrivacyPolicy = onViewPrivacyPolicy,
            onViewTermsOfServiceClicked = onViewTermsOfServiceClicked,
            onViewMoreAppsClicked = onViewMoreAppsClicked,
            onViewSocialMediaClicked = onViewSocialMediaClicked,
            onViewBlogClicked = onViewBlogClicked,
        )
      }
    }

    NavigationError(
        snackbarHost = scaffoldState.snackbarHostState,
        error = navigationError,
        onSnackbarDismissed = onNavigationErrorDismissed,
    )
  }
}

@Composable
private fun Loading() {
  Box(
      modifier = Modifier.systemBarsPadding().fillMaxHeight().fillMaxWidth().padding(16.dp),
      contentAlignment = Alignment.Center,
  ) { CircularProgressIndicator() }
}

@Composable
private fun SettingsList(
    topItemMargin: Dp,
    bottomItemMargin: Dp,
    customContent: List<Preferences>,
    hideClearAll: Boolean,
    hideUpgradeInformation: Boolean,
    applicationName: CharSequence,
    darkMode: Theming.Mode,
    onDarkModeChanged: (Theming.Mode) -> Unit,
    onLicensesClicked: () -> Unit,
    onCheckUpdateClicked: () -> Unit,
    onShowChangeLogClicked: () -> Unit,
    onResetClicked: () -> Unit,
    onRateClicked: () -> Unit,
    onDonateClicked: () -> Unit,
    onBugReportClicked: () -> Unit,
    onViewSourceClicked: () -> Unit,
    onViewPrivacyPolicy: () -> Unit,
    onViewTermsOfServiceClicked: () -> Unit,
    onViewMoreAppsClicked: () -> Unit,
    onViewSocialMediaClicked: () -> Unit,
    onViewBlogClicked: () -> Unit,
) {
  PreferenceScreen(
      topItemMargin = topItemMargin,
      bottomItemMargin = bottomItemMargin,
      preferences =
          customContent +
              listOf(
                  createApplicationPreferencesGroup(
                      hideClearAll = hideClearAll,
                      hideUpgradeInformation = hideUpgradeInformation,
                      applicationName = applicationName,
                      darkMode = darkMode,
                      onDarkModeChanged = onDarkModeChanged,
                      onLicensesClicked = onLicensesClicked,
                      onCheckUpdateClicked = onCheckUpdateClicked,
                      onShowChangeLogClicked = onShowChangeLogClicked,
                      onResetClicked = onResetClicked,
                  ),
                  createSupportPreferencesGroup(
                      applicationName = applicationName,
                      onRateClicked = onRateClicked,
                      onDonateClicked = onDonateClicked,
                      onBugReportClicked = onBugReportClicked,
                      onViewSourceClicked = onViewSourceClicked,
                      onViewPrivacyPolicy = onViewPrivacyPolicy,
                      onViewTermsOfServiceClicked = onViewTermsOfServiceClicked,
                  ),
                  createMoreAppsPreferencesGroup(
                      onViewMoreAppsClicked = onViewMoreAppsClicked,
                  ),
                  createSocialMediaPreferencesGroup(
                      onViewSocialMediaClicked = onViewSocialMediaClicked,
                      onViewBlogClicked = onViewBlogClicked,
                  ),
              ),
  )
}

@Composable
private fun NavigationError(
    snackbarHost: SnackbarHostState,
    error: Throwable?,
    onSnackbarDismissed: () -> Unit,
) {
  if (error != null) {
    LaunchedEffect(error) {
      snackbarHost.showSnackbar(
          message = error.message ?: "An unexpected error occurred",
          duration = SnackbarDuration.Long,
      )
      onSnackbarDismissed()
    }
  }
}

@Composable
private fun PreviewSettingsScreen(isLoading: Boolean) {
  SettingsScreen(
      state =
          SettingsViewState(
              isLoading = isLoading,
              hideClearAll = false,
              hideUpgradeInformation = false,
              applicationName = "TEST",
              darkMode = Theming.Mode.LIGHT,
              otherApps = emptyList(),
              navigationError = null,
          ),
      topItemMargin = 0.dp,
      bottomItemMargin = 0.dp,
      customContent = emptyList(),
      onDarkModeChanged = {},
      onLicensesClicked = {},
      onCheckUpdateClicked = {},
      onShowChangeLogClicked = {},
      onResetClicked = {},
      onRateClicked = {},
      onDonateClicked = {},
      onBugReportClicked = {},
      onViewSourceClicked = {},
      onViewPrivacyPolicy = {},
      onViewTermsOfServiceClicked = {},
      onViewMoreAppsClicked = {},
      onViewSocialMediaClicked = {},
      onViewBlogClicked = {},
      onNavigationErrorDismissed = {},
  )
}

@Preview
@Composable
private fun PreviewSettingsScreenLoading() {
  PreviewSettingsScreen(isLoading = true)
}

@Preview
@Composable
private fun PreviewSettingsScreenLoaded() {
  PreviewSettingsScreen(isLoading = false)
}
