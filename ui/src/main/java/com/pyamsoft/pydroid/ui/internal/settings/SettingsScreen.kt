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
    modifier: Modifier = Modifier,
    hideClearAll: Boolean,
    hideUpgradeInformation: Boolean,
    hideDataPolicy: Boolean,
    state: SettingsViewState,
    topItemMargin: Dp = 0.dp,
    bottomItemMargin: Dp = 0.dp,
    customPreContent: List<Preferences> = emptyList(),
    customPostContent: List<Preferences> = emptyList(),
    onDarkModeChanged: (Theming.Mode) -> Unit,
    onLicensesClicked: () -> Unit,
    onCheckUpdateClicked: () -> Unit,
    onShowChangeLogClicked: () -> Unit,
    onResetClicked: () -> Unit,
    onRateClicked: () -> Unit,
    onDonateClicked: () -> Unit,
    onBugReportClicked: () -> Unit,
    onViewSourceClicked: () -> Unit,
    onViewDataPolicyClicked: () -> Unit,
    onViewPrivacyPolicyClicked: () -> Unit,
    onViewTermsOfServiceClicked: () -> Unit,
    onViewMoreAppsClicked: () -> Unit,
    onViewSocialMediaClicked: () -> Unit,
    onViewBlogClicked: () -> Unit,
    onNavigationErrorDismissed: () -> Unit,
) {
  val isLoading = state.isLoading
  val applicationName = state.applicationName
  val darkMode = state.darkMode
  val navigationError = state.navigationError

  val scaffoldState = rememberScaffoldState()
  Scaffold(
      modifier = modifier,
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
            customPreContent = customPreContent,
            customPostContent = customPostContent,
            hideClearAll = hideClearAll,
            hideUpgradeInformation = hideUpgradeInformation,
            hideDataPolicy = hideDataPolicy,
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
            onViewDataPolicyClicked = onViewDataPolicyClicked,
            onViewPrivacyPolicyClicked = onViewPrivacyPolicyClicked,
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
    customPreContent: List<Preferences>,
    customPostContent: List<Preferences>,
    hideClearAll: Boolean,
    hideUpgradeInformation: Boolean,
    hideDataPolicy: Boolean,
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
    onViewDataPolicyClicked: () -> Unit,
    onViewPrivacyPolicyClicked: () -> Unit,
    onViewTermsOfServiceClicked: () -> Unit,
    onViewMoreAppsClicked: () -> Unit,
    onViewSocialMediaClicked: () -> Unit,
    onViewBlogClicked: () -> Unit,
) {
  val preferences =
      mutableListOf<Preferences>().apply {
        if (customPreContent.isNotEmpty()) {
          addAll(customPreContent)
        }

        add(
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
        )
        add(
            createSupportPreferencesGroup(
                hideDataPolicy = hideDataPolicy,
                applicationName = applicationName,
                onRateClicked = onRateClicked,
                onDonateClicked = onDonateClicked,
                onBugReportClicked = onBugReportClicked,
                onViewSourceClicked = onViewSourceClicked,
                onViewDataPolicyClicked = onViewDataPolicyClicked,
                onViewPrivacyPolicyClicked = onViewPrivacyPolicyClicked,
                onViewTermsOfServiceClicked = onViewTermsOfServiceClicked,
            ),
        )
        add(
            createMoreAppsPreferencesGroup(
                onViewMoreAppsClicked = onViewMoreAppsClicked,
            ),
        )
        add(
            createSocialMediaPreferencesGroup(
                onViewSocialMediaClicked = onViewSocialMediaClicked,
                onViewBlogClicked = onViewBlogClicked,
            ),
        )

        if (customPostContent.isNotEmpty()) {
          addAll(customPostContent)
        }
      }

  PreferenceScreen(
      topItemMargin = topItemMargin,
      bottomItemMargin = bottomItemMargin,
      preferences = preferences,
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
          MutableSettingsViewState().apply {
            this.isLoading = isLoading
            applicationName = "TEST"
            darkMode = Theming.Mode.LIGHT
            otherApps = emptyList()
            navigationError = null
          },
      hideClearAll = false,
      hideUpgradeInformation = false,
      hideDataPolicy = false,
      topItemMargin = 0.dp,
      bottomItemMargin = 0.dp,
      customPreContent = emptyList(),
      customPostContent = emptyList(),
      onDarkModeChanged = {},
      onLicensesClicked = {},
      onCheckUpdateClicked = {},
      onShowChangeLogClicked = {},
      onResetClicked = {},
      onRateClicked = {},
      onDonateClicked = {},
      onBugReportClicked = {},
      onViewSourceClicked = {},
      onViewDataPolicyClicked = {},
      onViewPrivacyPolicyClicked = {},
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
