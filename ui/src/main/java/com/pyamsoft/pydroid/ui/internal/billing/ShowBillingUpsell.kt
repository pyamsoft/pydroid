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

package com.pyamsoft.pydroid.ui.internal.billing

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.pyamsoft.pydroid.theme.keylines
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.internal.widget.InterruptCard

@Composable
internal fun ShowBillingUpsell(
    modifier: Modifier = Modifier,
    state: BillingViewState,
    onShowBilling: () -> Unit,
    onDismiss: () -> Unit,
) {
  InterruptCard(
      modifier = modifier,
      visible = state.showUpsell,
  ) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(MaterialTheme.keylines.content),
    ) {
      Text(
          text = stringResource(R.string.donate_summary),
          style =
              MaterialTheme.typography.body2.copy(
                  color = MaterialTheme.colors.primary,
              ),
      )

      OutlinedButton(
          modifier = Modifier.padding(top = MaterialTheme.keylines.content),
          onClick = onShowBilling,
      ) {
        Text(
            text = stringResource(R.string.donate_title),
        )
      }
    }
  }
}

@Preview
@Composable
private fun PreviewShowBillingUpsell() {
  ShowBillingUpsell(
      state = MutableBillingViewState().apply { showUpsell = true },
      onDismiss = {},
      onShowBilling = {},
  )
}
