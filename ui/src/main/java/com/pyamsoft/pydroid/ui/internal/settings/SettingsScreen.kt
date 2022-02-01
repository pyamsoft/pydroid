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

package com.pyamsoft.pydroid.ui.internal.settings

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.google.accompanist.insets.systemBarsPadding
import com.pyamsoft.pydroid.theme.ZeroSize
import com.pyamsoft.pydroid.theme.keylines
import com.pyamsoft.pydroid.ui.preference.PreferenceScreen
import com.pyamsoft.pydroid.ui.preference.Preferences
import com.pyamsoft.pydroid.ui.theme.Theming
import com.pyamsoft.pydroid.ui.theme.ZeroElevation

@Composable
@OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
internal fun SettingsScreen(
    modifier: Modifier = Modifier,
    elevation: Dp = ZeroElevation,
    hideClearAll: Boolean,
    hideUpgradeInformation: Boolean,
    hideDataPolicy: Boolean,
    state: SettingsViewState,
    topItemMargin: Dp = ZeroSize,
    bottomItemMargin: Dp = ZeroSize,
    customPreContent: List<Preferences> = emptyList(),
    customPostContent: List<Preferences> = emptyList(),
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
    onOpenMarketPage: (UriHandler) -> Unit,
    onViewMoreAppsClicked: (UriHandler) -> Unit,
) {
  val isLoading = state.isLoading
  val applicationName = state.applicationName
  val darkMode = state.darkMode

  Surface(
      modifier = modifier,
      elevation = elevation,
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
            onDonateClicked = onDonateClicked,
            onBugReportClicked = onBugReportClicked,
            onViewSourceClicked = onViewSourceClicked,
            onViewDataPolicyClicked = onViewDataPolicyClicked,
            onViewPrivacyPolicyClicked = onViewPrivacyPolicyClicked,
            onViewTermsOfServiceClicked = onViewTermsOfServiceClicked,
            onViewSocialMediaClicked = onViewSocialMediaClicked,
            onViewBlogClicked = onViewBlogClicked,
            onOpenMarketPage = onOpenMarketPage,
            onViewMoreAppsClicked = onViewMoreAppsClicked,
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
    onDonateClicked: () -> Unit,
    onBugReportClicked: () -> Unit,
    onViewSourceClicked: () -> Unit,
    onViewDataPolicyClicked: () -> Unit,
    onViewPrivacyPolicyClicked: () -> Unit,
    onViewTermsOfServiceClicked: () -> Unit,
    onViewSocialMediaClicked: () -> Unit,
    onViewBlogClicked: () -> Unit,
    onOpenMarketPage: (UriHandler) -> Unit,
    onViewMoreAppsClicked: (UriHandler) -> Unit,
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
                onDonateClicked = onDonateClicked,
                onBugReportClicked = onBugReportClicked,
                onViewSourceClicked = onViewSourceClicked,
                onViewDataPolicyClicked = onViewDataPolicyClicked,
                onViewPrivacyPolicyClicked = onViewPrivacyPolicyClicked,
                onViewTermsOfServiceClicked = onViewTermsOfServiceClicked,
                onOpenMarketPage = onOpenMarketPage,
            ),
        )
        add(
            createMoreAppsPreferencesGroup(
                onViewMoreApps = onViewMoreAppsClicked,
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
private fun PreviewSettingsScreen(isLoading: Boolean) {
  SettingsScreen(
      state =
          MutableSettingsViewState().apply {
            this.isLoading = isLoading
            applicationName = "TEST"
            darkMode = Theming.Mode.LIGHT
            otherApps = emptyList()
          },
      hideClearAll = false,
      hideUpgradeInformation = false,
      hideDataPolicy = false,
      topItemMargin = ZeroSize,
      bottomItemMargin = ZeroSize,
      customPreContent = emptyList(),
      customPostContent = emptyList(),
      onDarkModeChanged = {},
      onLicensesClicked = {},
      onCheckUpdateClicked = {},
      onShowChangeLogClicked = {},
      onResetClicked = {},
      onViewMoreAppsClicked = {},
      onOpenMarketPage = {},
      onDonateClicked = {},
      onBugReportClicked = {},
      onViewSourceClicked = {},
      onViewDataPolicyClicked = {},
      onViewPrivacyPolicyClicked = {},
      onViewTermsOfServiceClicked = {},
      onViewSocialMediaClicked = {},
      onViewBlogClicked = {},
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
