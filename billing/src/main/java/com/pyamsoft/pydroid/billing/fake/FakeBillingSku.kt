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

package com.pyamsoft.pydroid.billing.fake

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import com.pyamsoft.pydroid.billing.BillingSku
import java.util.UUID

@Stable
@Immutable
@ConsistentCopyVisibility
internal data class FakeBillingSku
internal constructor(
  override val title: String,
  override val description: String,
  private val priceInCents: Long,
) : BillingSku {

  override val id: String = UUID.randomUUID().toString()

  override val displayPrice: String = "$%.2f".format(priceInCents / 100.0)

  override val price: Long = priceInCents * 10_000
}
