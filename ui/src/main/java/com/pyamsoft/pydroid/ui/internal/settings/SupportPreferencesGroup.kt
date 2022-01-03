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

import androidx.annotation.CheckResult
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Star
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.res.stringResource
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.icons.BugReport
import com.pyamsoft.pydroid.ui.icons.Business
import com.pyamsoft.pydroid.ui.icons.Code
import com.pyamsoft.pydroid.ui.icons.Policy
import com.pyamsoft.pydroid.ui.icons.Redeem
import com.pyamsoft.pydroid.ui.preference.Preferences
import com.pyamsoft.pydroid.ui.preference.inAppPreference
import com.pyamsoft.pydroid.ui.preference.preference
import com.pyamsoft.pydroid.ui.preference.preferenceGroup

@Composable
@CheckResult
internal fun createSupportPreferencesGroup(
    hideDataPolicy: Boolean,
    applicationName: CharSequence,
    onDonateClicked: () -> Unit,
    onBugReportClicked: () -> Unit,
    onViewSourceClicked: () -> Unit,
    onViewDataPolicyClicked: () -> Unit,
    onViewPrivacyPolicyClicked: () -> Unit,
    onViewTermsOfServiceClicked: () -> Unit,
    onOpenMarketPage: (UriHandler) -> Unit,
): Preferences.Group {
  val uriHandler = LocalUriHandler.current

  return preferenceGroup(
      name = "Support pyamsoft",
      preferences =
          listOf(
              ratePreference(
                  applicationName = applicationName,
                  onRateClicked = { onOpenMarketPage(uriHandler) },
              ),
              donatePreference(
                  onDonateClicked = onDonateClicked,
              ),
              bugReportPreference(
                  onBugReportClicked = onBugReportClicked,
              ),
              sourceCodePreference(
                  onViewSourceClicked = onViewSourceClicked,
              ),
          ) +
              decideDataPolicyPreference(
                  hideDataPolicy = hideDataPolicy,
                  onViewDataPolicyClicked = onViewDataPolicyClicked,
              ) +
              listOf(
                  privacyPolicyPreference(
                      onViewPrivacyPolicyClicked = onViewPrivacyPolicyClicked,
                  ),
                  termsOfServicePreference(
                      onViewTermsOfServiceClicked = onViewTermsOfServiceClicked,
                  ),
              ),
  )
}

@Composable
@CheckResult
private fun decideDataPolicyPreference(
    hideDataPolicy: Boolean,
    onViewDataPolicyClicked: () -> Unit,
): List<Preferences.Item> {
  return if (hideDataPolicy) emptyList()
  else {
    listOf(
        dataPolicyPreference(
            onViewDataPolicyClicked = onViewDataPolicyClicked,
        ),
    )
  }
}

@Composable
@CheckResult
private fun ratePreference(
    applicationName: CharSequence,
    onRateClicked: () -> Unit
): Preferences.Item {
  return preference(
      name = "Rate $applicationName",
      summary = stringResource(R.string.rating_summary),
      icon = Icons.Outlined.Star,
      onClick = onRateClicked,
  )
}

@Composable
@CheckResult
private fun donatePreference(
    onDonateClicked: () -> Unit,
): Preferences.Item {
  return inAppPreference(
      name = stringResource(R.string.donate_title),
      summary = stringResource(R.string.donate_summary),
      icon = Icons.Outlined.Redeem,
      onClick = onDonateClicked,
  )
}

@Composable
@CheckResult
private fun bugReportPreference(
    onBugReportClicked: () -> Unit,
): Preferences.Item {
  return preference(
      name = stringResource(R.string.bugreport_title),
      summary = stringResource(R.string.bugreport_summary),
      icon = Icons.Outlined.BugReport,
      onClick = onBugReportClicked,
  )
}

@Composable
@CheckResult
private fun sourceCodePreference(
    onViewSourceClicked: () -> Unit,
): Preferences.Item {
  return preference(
      name = stringResource(R.string.view_source_title),
      summary = stringResource(R.string.view_source_summary),
      icon = Icons.Outlined.Code,
      onClick = onViewSourceClicked,
  )
}

@Composable
@CheckResult
private fun dataPolicyPreference(
    onViewDataPolicyClicked: () -> Unit,
): Preferences.Item {
  return preference(
      name = stringResource(R.string.view_data_policy_title),
      summary = stringResource(R.string.view_data_policy_summary),
      icon = Icons.Outlined.Policy,
      onClick = onViewDataPolicyClicked,
  )
}

@Composable
@CheckResult
private fun privacyPolicyPreference(
    onViewPrivacyPolicyClicked: () -> Unit,
): Preferences.Item {
  return preference(
      name = stringResource(R.string.view_privacy_title),
      summary = stringResource(R.string.view_privacy_summary),
      icon = Icons.Outlined.Policy,
      onClick = onViewPrivacyPolicyClicked,
  )
}

@Composable
@CheckResult
private fun termsOfServicePreference(
    onViewTermsOfServiceClicked: () -> Unit,
): Preferences.Item {
  return preference(
      name = stringResource(R.string.view_terms_title),
      summary = stringResource(R.string.view_terms_summary),
      icon = Icons.Outlined.Business,
      onClick = onViewTermsOfServiceClicked,
  )
}
