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
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.pyamsoft.pydroid.theme.ZeroSize
import com.pyamsoft.pydroid.theme.keylines
import com.pyamsoft.pydroid.ui.app.PYDroidActivityOptions
import com.pyamsoft.pydroid.ui.preference.PreferenceScreen
import com.pyamsoft.pydroid.ui.preference.Preferences
import com.pyamsoft.pydroid.ui.theme.Theming
import com.pyamsoft.pydroid.ui.theme.ZeroElevation
import com.pyamsoft.pydroid.ui.util.SafeList
import com.pyamsoft.pydroid.ui.util.safe

@Composable
internal fun SettingsScreen(
    modifier: Modifier = Modifier,
    shape: Shape,
    elevation: Dp,
    topItemMargin: Dp,
    bottomItemMargin: Dp,
    state: SettingsViewState,
    options: PYDroidActivityOptions,
    hideClearAll: Boolean,
    hideUpgradeInformation: Boolean,
    customPreContent: SafeList<Preferences>,
    customPostContent: SafeList<Preferences>,
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
) {
  val loadingState by state.loadingState.collectAsState()
  val applicationName by state.applicationName.collectAsState()
  val darkMode by state.darkMode.collectAsState()

  Surface(
      modifier = modifier,
      elevation = elevation,
      shape = shape,
  ) {
    Crossfade(
        targetState = loadingState,
    ) { loading ->
      when (loading) {
        SettingsViewState.LoadingState.NONE,
        SettingsViewState.LoadingState.LOADING -> {
          Loading()
        }
        SettingsViewState.LoadingState.DONE -> {
          SettingsList(
              applicationName = applicationName,
              darkMode = darkMode,
              options = options,
              topItemMargin = topItemMargin,
              bottomItemMargin = bottomItemMargin,
              customPreContent = customPreContent,
              customPostContent = customPostContent,
              hideClearAll = hideClearAll,
              hideUpgradeInformation = hideUpgradeInformation,
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
          )
        }
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
    options: PYDroidActivityOptions,
    topItemMargin: Dp,
    bottomItemMargin: Dp,
    customPreContent: SafeList<Preferences>,
    customPostContent: SafeList<Preferences>,
    hideClearAll: Boolean,
    hideUpgradeInformation: Boolean,
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
    onOpenMarketPage: () -> Unit,
) {
  val applicationPrefs =
      rememberApplicationPreferencesGroup(
          options = options,
          hideUpgradeInformation = hideUpgradeInformation,
          applicationName = applicationName,
          darkMode = darkMode,
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
          hideClearAll = hideClearAll,
          onResetClicked = onResetClicked,
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
        mutableListOf<Preferences>().apply {
          addAll(customPreContent.list)
          add(applicationPrefs)
          add(supportPrefs)
          add(infoPreferences)
          add(socialMediaPreferences)
          add(dangerZonePreferences)
          addAll(customPostContent.list)
        }
      }

  val data = remember(preferences) { preferences.safe() }

  PreferenceScreen(
      topItemMargin = topItemMargin,
      bottomItemMargin = bottomItemMargin,
      preferences = data,
  )
}

@Composable
private fun PreviewSettingsScreen(loadingState: SettingsViewState.LoadingState) {
  SettingsScreen(
      options = PYDroidActivityOptions(),
      shape = MaterialTheme.shapes.medium,
      state =
          MutableSettingsViewState().apply {
            this.loadingState.value = loadingState
            applicationName.value = "TEST"
            darkMode.value = Theming.Mode.LIGHT
          },
      hideClearAll = false,
      hideUpgradeInformation = false,
      elevation = ZeroElevation,
      topItemMargin = ZeroSize,
      bottomItemMargin = ZeroSize,
      customPreContent = emptyList<Preferences>().safe(),
      customPostContent = emptyList<Preferences>().safe(),
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
