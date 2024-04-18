/*
 * Copyright 2024 pyamsoft
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

package com.pyamsoft.pydroid.ui.internal.billing.dialog

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.pyamsoft.pydroid.billing.BillingSku
import com.pyamsoft.pydroid.theme.keylines

@Composable
internal fun BillingListItem(
    modifier: Modifier = Modifier,
    sku: BillingSku,
    onPurchase: (BillingSku) -> Unit,
) {
  Row(
      modifier =
          modifier
              .clickable { onPurchase(sku) }
              .padding(vertical = MaterialTheme.keylines.baseline)
              .padding(horizontal = MaterialTheme.keylines.content),
      verticalAlignment = Alignment.CenterVertically,
  ) {
    Column(
        modifier = Modifier.weight(1F),
    ) {
      Name(
          sku = sku,
      )
      Description(
          sku = sku,
      )
    }

    Box(
        modifier = Modifier.padding(start = MaterialTheme.keylines.baseline),
        contentAlignment = Alignment.Center,
    ) {
      Price(
          sku = sku,
      )
    }
  }
}

@Composable
private fun Name(
    modifier: Modifier = Modifier,
    sku: BillingSku,
) {
  val title =
      remember(sku.title) {
        // In production, the title is something like "Pay me Money (APP_NAME)" even though we want
        // it to just show "Pay me Money"
        // So we modify it visually here
        val title = sku.title
        val indexOfTitle = title.indexOf(" (")

        // Remove the (APP_NAME) section if it exists
        return@remember if (indexOfTitle >= 0) title.substring(0, indexOfTitle) else title
      }

  Text(
      modifier = modifier,
      text = title,
      style = MaterialTheme.typography.bodyLarge,
  )
}

@Composable
private fun Description(
    modifier: Modifier = Modifier,
    sku: BillingSku,
) {
  Text(
      modifier = modifier,
      text = sku.description,
      style = MaterialTheme.typography.bodySmall,
  )
}

@Composable
private fun Price(
    modifier: Modifier = Modifier,
    sku: BillingSku,
) {
  Text(
      modifier = modifier,
      text = sku.displayPrice,
      style = MaterialTheme.typography.bodyLarge,
  )
}

@Preview
@Composable
private fun PreviewBillingListItem() {
  Surface {
    BillingListItem(
        sku =
            object : BillingSku {
              override val id: String = ""
              override val displayPrice: String = "$1.00"
              override val price: Long = 100
              override val title: String = "TEST (THIS SHOULD NOT SHOW)"
              override val description: String = "Just a Test"
            },
        onPurchase = {},
    )
  }
}
