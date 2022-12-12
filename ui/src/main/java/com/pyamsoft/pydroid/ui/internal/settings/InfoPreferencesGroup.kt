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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.app.PYDroidActivityOptions
import com.pyamsoft.pydroid.ui.icons.BugReport
import com.pyamsoft.pydroid.ui.icons.Business
import com.pyamsoft.pydroid.ui.icons.Code
import com.pyamsoft.pydroid.ui.icons.Policy
import com.pyamsoft.pydroid.ui.preference.Preferences
import com.pyamsoft.pydroid.ui.preference.preference
import com.pyamsoft.pydroid.ui.preference.preferenceGroup

@Composable
@CheckResult
internal fun rememberInfoPreferencesGroup(
    options: PYDroidActivityOptions,
    applicationName: CharSequence,
    onBugReportClicked: () -> Unit,
    onViewSourceClicked: () -> Unit,
    onViewDataPolicyClicked: () -> Unit,
    onViewPrivacyPolicyClicked: () -> Unit,
    onViewTermsOfServiceClicked: () -> Unit,
): Preferences.Group {
  val bugReportPreference =
      rememberBugReportPreference(
          onClick = onBugReportClicked,
      )
  val viewSourcePreference =
      rememberViewSourceCodePreference(
          onClick = onViewSourceClicked,
      )
  val dataPolicyPreference =
      rememberDataPolicyPreference(
          onClick = onViewDataPolicyClicked,
      )

  val privacyPreference =
      rememberPrivacyPolicyPreference(
          onClick = onViewPrivacyPolicyClicked,
      )

  val tosPreference =
      rememberTermsOfServicePreference(
          onClick = onViewTermsOfServiceClicked,
      )

  val preferences =
      remember(
          options.disableDataPolicy,
          bugReportPreference,
          viewSourcePreference,
          dataPolicyPreference,
          privacyPreference,
          tosPreference,
      ) {
        mutableListOf<Preferences.Item>().apply {
          add(bugReportPreference)
          add(viewSourcePreference)

          if (!options.disableDataPolicy) {
            add(dataPolicyPreference)
          }

          add(privacyPreference)
          add(tosPreference)
        }
      }

  val title = remember(applicationName) { "$applicationName Information" }
  return remember(
      title,
      preferences,
  ) {
    preferenceGroup(
        name = title,
        preferences = preferences,
    )
  }
}

@Composable
@CheckResult
private fun rememberBugReportPreference(
    onClick: () -> Unit,
): Preferences.Item {
  val name = stringResource(R.string.bugreport_title)
  val summary = stringResource(R.string.bugreport_summary)

  return remember(
      name,
      summary,
      onClick,
  ) {
    preference(
        name = name,
        summary = summary,
        icon = Icons.Outlined.BugReport,
        onClick = onClick,
    )
  }
}

@Composable
@CheckResult
private fun rememberViewSourceCodePreference(
    onClick: () -> Unit,
): Preferences.Item {
  val name = stringResource(R.string.view_source_title)
  val summary = stringResource(R.string.view_source_summary)

  return remember(
      name,
      summary,
      onClick,
  ) {
    preference(
        name = name,
        summary = summary,
        icon = Icons.Outlined.Code,
        onClick = onClick,
    )
  }
}

@Composable
@CheckResult
private fun rememberDataPolicyPreference(
    onClick: () -> Unit,
): Preferences.Item {
  val name = stringResource(R.string.view_data_policy_title)
  val summary = stringResource(R.string.view_data_policy_summary)

  return remember(
      name,
      summary,
      onClick,
  ) {
    preference(
        name = name,
        summary = summary,
        icon = Icons.Outlined.Policy,
        onClick = onClick,
    )
  }
}

@Composable
@CheckResult
private fun rememberPrivacyPolicyPreference(
    onClick: () -> Unit,
): Preferences.Item {
  val name = stringResource(R.string.view_privacy_title)
  val summary = stringResource(R.string.view_privacy_summary)

  return remember(
      name,
      summary,
      onClick,
  ) {
    preference(
        name = name,
        summary = summary,
        icon = Icons.Outlined.Policy,
        onClick = onClick,
    )
  }
}

@Composable
@CheckResult
private fun rememberTermsOfServicePreference(
    onClick: () -> Unit,
): Preferences.Item {
  val name = stringResource(R.string.view_terms_title)
  val summary = stringResource(R.string.view_terms_summary)

  return remember(
      name,
      summary,
      onClick,
  ) {
    preference(
        name = name,
        summary = summary,
        icon = Icons.Outlined.Business,
        onClick = onClick,
    )
  }
}
