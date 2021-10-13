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

package com.pyamsoft.pydroid.ui.internal.billing.listitem

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pyamsoft.pydroid.billing.BillingSku

@Composable
internal fun BillingListItem(state: BillingItemViewState, onPurchase: () -> Unit) {
  val sku = state.sku

  Row(
      modifier = Modifier.padding(8.dp).clickable { onPurchase() },
      verticalAlignment = Alignment.CenterVertically,
  ) {
    Column(modifier = Modifier.weight(1F)) {
      Name(
          sku = sku,
      )
      Description(
          sku = sku,
      )
    }

    Box(
        modifier = Modifier.padding(start = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
      Price(
          sku = sku,
      )
    }
  }
}

@Composable
private fun Name(sku: BillingSku) {
  Text(
      text = sku.title,
      style = MaterialTheme.typography.body1,
  )
}

@Composable
private fun Description(sku: BillingSku) {
  Text(
      text = sku.description,
      style = MaterialTheme.typography.caption,
  )
}

@Composable
private fun Price(sku: BillingSku) {
  Text(
      text = sku.displayPrice,
      style = MaterialTheme.typography.body1,
  )
}

@Preview
@Composable
private fun PreviewBillingListItem() {
  BillingListItem(
      state =
          BillingItemViewState(
              object : BillingSku {
                override val id: String = ""
                override val displayPrice: String = "$1.00"
                override val price: Long = 100
                override val title: String = "TEST"
                override val description: String = "Just a Test"
                override val iconUrl: String = ""
              },
          ),
      onPurchase = {},
  )
}
