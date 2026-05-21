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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.res.stringResource
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.app.PYDroidActivityOptions
import com.pyamsoft.pydroid.ui.preference.Preferences
import com.pyamsoft.pydroid.ui.preference.checkBoxPreference
import com.pyamsoft.pydroid.ui.preference.inAppPreference
import com.pyamsoft.pydroid.ui.preference.preference
import com.pyamsoft.pydroid.ui.preference.preferenceGroup

@Composable
@CheckResult
internal fun rememberSupportPreferencesGroup(
    options: PYDroidActivityOptions,
    applicationName: CharSequence,
    isBillingUpsellDisabled: Boolean,
    onDonateClicked: () -> Unit,
    onOpenMarketPage: () -> Unit,
    onBillingUpsellDisabledChanged: (Boolean) -> Unit,
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

  val billingUpsellDisabledPreference =
      rememberBillingUpsellDisabledPreference(
          isBillingUpsellDisabled = isBillingUpsellDisabled,
          onCheckedChanged = onBillingUpsellDisabledChanged,
      )

  val preferences =
      remember(
          options.disableBilling,
          options.disableRating,
          ratePreference,
          donatePreference,
          isBillingUpsellDisabled,
          billingUpsellDisabledPreference,
      ) {
        mutableListOf<Preferences.Item>().apply {
          if (!options.disableRating) {
            add(ratePreference)
          }

          if (!options.disableBilling) {
            add(donatePreference)
            add(billingUpsellDisabledPreference)
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
    onClick: () -> Unit,
): Preferences.Item {
  val title = stringResource(R.string.rating_title, applicationName)
  val summary = stringResource(R.string.rating_summary)

  val handleClick by rememberUpdatedState(onClick)

  return remember(
      applicationName,
      summary,
  ) {
    preference(
        id = "rate_app",
        name = title,
        summary = summary,
        icon = R.drawable.star_24px,
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
        icon = R.drawable.redeem_24px,
        onClick = { handleClick() },
    )
  }
}

@Composable
@CheckResult
private fun rememberBillingUpsellDisabledPreference(
    isBillingUpsellDisabled: Boolean,
    onCheckedChanged: (Boolean) -> Unit,
): Preferences.Item {
  val name = stringResource(R.string.billing_upsell_disabled_title)
  val summary = stringResource(R.string.billing_upsell_disabled_summary)

  val handleCheckedChanged by rememberUpdatedState(onCheckedChanged)

  return remember(
      name,
      summary,
      isBillingUpsellDisabled,
  ) {
    checkBoxPreference(
        id = "billing_upsell_disabled",
        name = name,
        summary = summary,
        icon = null,
        checked = isBillingUpsellDisabled,
        onCheckedChanged = { checked -> handleCheckedChanged(checked) },
    )
  }
}
