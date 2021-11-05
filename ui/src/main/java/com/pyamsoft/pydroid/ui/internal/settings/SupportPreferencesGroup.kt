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
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.preference.Preferences
import com.pyamsoft.pydroid.ui.preference.inAppPreference
import com.pyamsoft.pydroid.ui.preference.preference
import com.pyamsoft.pydroid.ui.preference.preferenceGroup

@Composable
@CheckResult
internal fun createSupportPreferencesGroup(
    applicationName: CharSequence,
    onRateClicked: () -> Unit,
    onDonateClicked: () -> Unit,
    onBugReportClicked: () -> Unit,
    onViewSourceClicked: () -> Unit,
    onViewPrivacyPolicy: () -> Unit,
    onViewTermsOfServiceClicked: () -> Unit,
): Preferences.Group {
  return preferenceGroup(
      name = "Support pyamsoft",
      preferences =
          listOf(
              ratePreference(
                  applicationName = applicationName,
                  onRateClicked = onRateClicked,
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
              privacyPolicyPreference(
                  onViewPrivacyPolicy = onViewPrivacyPolicy,
              ),
              termsOfServicePreference(
                  onViewTermsOfServiceClicked = onViewTermsOfServiceClicked,
              ),
          ),
  )
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
      icon = R.drawable.ic_star_24dp,
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
      icon = R.drawable.ic_gift_24dp,
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
      icon = R.drawable.ic_bug_report_24dp,
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
      icon = R.drawable.ic_code_24dp,
      onClick = onViewSourceClicked,
  )
}

@Composable
@CheckResult
private fun privacyPolicyPreference(
    onViewPrivacyPolicy: () -> Unit,
): Preferences.Item {
  return preference(
      name = stringResource(R.string.view_privacy_title),
      summary = stringResource(R.string.view_privacy_summary),
      icon = R.drawable.ic_policy_24dp,
      onClick = onViewPrivacyPolicy,
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
      icon = R.drawable.ic_terms_24dp,
      onClick = onViewTermsOfServiceClicked,
  )
}
