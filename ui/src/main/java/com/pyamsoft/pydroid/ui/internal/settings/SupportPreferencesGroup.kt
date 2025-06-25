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

import androidx.annotation.CheckResult
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.res.stringResource
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.app.PYDroidActivityOptions
import com.pyamsoft.pydroid.ui.icons.Redeem
import com.pyamsoft.pydroid.ui.preference.Preferences
import com.pyamsoft.pydroid.ui.preference.inAppPreference
import com.pyamsoft.pydroid.ui.preference.preference
import com.pyamsoft.pydroid.ui.preference.preferenceGroup

@Composable
@CheckResult
internal fun rememberSupportPreferencesGroup(
    options: PYDroidActivityOptions,
    applicationName: CharSequence,
    onDonateClicked: () -> Unit,
    onOpenMarketPage: () -> Unit,
): Preferences.Group {
  val ratePreference =
      rememberRatePreference(
          applicationName = applicationName,
          onClick = onOpenMarketPage,
      )

  val donatePreference =
      rememberDonatePreference(
          onClick = onDonateClicked,
      )

  val preferences =
      remember(
          options.disableBilling,
          options.disableRating,
          ratePreference,
          donatePreference,
      ) {
        mutableListOf<Preferences.Item>().apply {
          if (!options.disableRating) {
            add(ratePreference)
          }

          if (!options.disableBilling) {
            add(donatePreference)
          }
        }
      }

  val title = remember { "Support pyamsoft" }
  return remember(
      title,
      preferences,
  ) {
    preferenceGroup(
        id = "support_pyamsoft",
        name = title,
        preferences = preferences,
    )
  }
}

@Composable
@CheckResult
private fun rememberRatePreference(
    applicationName: CharSequence,
    onClick: () -> Unit
): Preferences.Item {
  val summary = stringResource(R.string.rating_summary)

  val handleClick by rememberUpdatedState(onClick)

  return remember(
      applicationName,
      summary,
  ) {
    preference(
        id = "rate_app",
        name = "Rate $applicationName",
        summary = summary,
        icon = Icons.Outlined.Star,
        onClick = { handleClick() },
    )
  }
}

@Composable
@CheckResult
private fun rememberDonatePreference(
    onClick: () -> Unit,
): Preferences.Item {
  val name = stringResource(R.string.donate_title)
  val summary = stringResource(R.string.donate_summary)

  val handleClick by rememberUpdatedState(onClick)

  return remember(
      name,
      summary,
  ) {
    inAppPreference(
        id = "tip_me",
        name = name,
        summary = summary,
        icon = Icons.Outlined.Redeem,
        onClick = { handleClick() },
    )
  }
}
