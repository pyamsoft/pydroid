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

import androidx.annotation.CheckResult
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.res.stringResource
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.app.PYDroidActivityOptions
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
    options: PYDroidActivityOptions,
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

  val ratingSummary = stringResource(R.string.rating_summary)
  val donateName = stringResource(R.string.donate_title)
  val donateSummary = stringResource(R.string.donate_summary)
  val bugReportName = stringResource(R.string.bugreport_title)
  val bugReportSummary = stringResource(R.string.bugreport_summary)
  val viewSourceName = stringResource(R.string.view_source_title)
  val viewSourceSummary = stringResource(R.string.view_source_summary)
  val dataPolicyName = stringResource(R.string.view_data_policy_title)
  val dataPolicySummary = stringResource(R.string.view_data_policy_summary)
  val privacyName = stringResource(R.string.view_privacy_title)
  val privacySummary = stringResource(R.string.view_privacy_summary)
  val tosName = stringResource(R.string.view_terms_title)
  val tosSummary = stringResource(R.string.view_terms_summary)

  val preferences =
      remember(
          options.disableDataPolicy,
          options.disableBilling,
          options.disableRating,
          ratingSummary,
          donateName,
          donateSummary,
          bugReportName,
          bugReportSummary,
          viewSourceName,
          viewSourceSummary,
          dataPolicyName,
          dataPolicySummary,
          privacyName,
          privacySummary,
          tosName,
          tosSummary,
          uriHandler,
          applicationName,
          onOpenMarketPage,
          onDonateClicked,
          onBugReportClicked,
          onViewDataPolicyClicked,
          onViewSourceClicked,
          onViewPrivacyPolicyClicked,
          onViewTermsOfServiceClicked,
      ) {
        mutableListOf<Preferences.Item>().apply {
          if (!options.disableRating) {
            add(
                ratePreference(
                    applicationName = applicationName,
                    summary = ratingSummary,
                    onRateClicked = { onOpenMarketPage(uriHandler) },
                ),
            )
          }

          if (!options.disableBilling) {
            add(
                donatePreference(
                    name = donateName,
                    summary = donateSummary,
                    onDonateClicked = onDonateClicked,
                ),
            )
          }

          add(
              bugReportPreference(
                  name = bugReportName,
                  summary = bugReportSummary,
                  onBugReportClicked = onBugReportClicked,
              ),
          )
          add(
              sourceCodePreference(
                  name = viewSourceName,
                  summary = viewSourceSummary,
                  onViewSourceClicked = onViewSourceClicked,
              ),
          )

          if (!options.disableDataPolicy) {
            add(
                dataPolicyPreference(
                    name = dataPolicyName,
                    summary = dataPolicySummary,
                    onViewDataPolicyClicked = onViewDataPolicyClicked,
                ),
            )
          }

          add(
              privacyPolicyPreference(
                  name = privacyName,
                  summary = privacySummary,
                  onViewPrivacyPolicyClicked = onViewPrivacyPolicyClicked,
              ),
          )
          add(
              termsOfServicePreference(
                  name = tosName,
                  summary = tosSummary,
                  onViewTermsOfServiceClicked = onViewTermsOfServiceClicked,
              ),
          )
        }
      }

  return preferenceGroup(
      name = "Support pyamsoft",
      preferences = preferences,
  )
}

@CheckResult
private fun ratePreference(
    applicationName: CharSequence,
    summary: String,
    onRateClicked: () -> Unit
): Preferences.Item {
  return preference(
      name = "Rate $applicationName",
      summary = summary,
      icon = Icons.Outlined.Star,
      onClick = onRateClicked,
  )
}

@CheckResult
private fun donatePreference(
    name: String,
    summary: String,
    onDonateClicked: () -> Unit,
): Preferences.Item {
  return inAppPreference(
      name = name,
      summary = summary,
      icon = Icons.Outlined.Redeem,
      onClick = onDonateClicked,
  )
}

@CheckResult
private fun bugReportPreference(
    name: String,
    summary: String,
    onBugReportClicked: () -> Unit,
): Preferences.Item {
  return preference(
      name = name,
      summary = summary,
      icon = Icons.Outlined.BugReport,
      onClick = onBugReportClicked,
  )
}

@CheckResult
private fun sourceCodePreference(
    name: String,
    summary: String,
    onViewSourceClicked: () -> Unit,
): Preferences.Item {
  return preference(
      name = name,
      summary = summary,
      icon = Icons.Outlined.Code,
      onClick = onViewSourceClicked,
  )
}

@CheckResult
private fun dataPolicyPreference(
    name: String,
    summary: String,
    onViewDataPolicyClicked: () -> Unit,
): Preferences.Item {
  return preference(
      name = name,
      summary = summary,
      icon = Icons.Outlined.Policy,
      onClick = onViewDataPolicyClicked,
  )
}

@CheckResult
private fun privacyPolicyPreference(
    name: String,
    summary: String,
    onViewPrivacyPolicyClicked: () -> Unit,
): Preferences.Item {
  return preference(
      name = name,
      summary = summary,
      icon = Icons.Outlined.Policy,
      onClick = onViewPrivacyPolicyClicked,
  )
}

@CheckResult
private fun termsOfServicePreference(
    name: String,
    summary: String,
    onViewTermsOfServiceClicked: () -> Unit,
): Preferences.Item {
  return preference(
      name = name,
      summary = summary,
      icon = Icons.Outlined.Business,
      onClick = onViewTermsOfServiceClicked,
  )
}
