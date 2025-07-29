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

package com.pyamsoft.pydroid.ui.internal.billing.dialog

import androidx.compose.runtime.Stable
import com.pyamsoft.pydroid.billing.BillingPurchase
import com.pyamsoft.pydroid.billing.BillingSku
import com.pyamsoft.pydroid.billing.BillingState
import com.pyamsoft.pydroid.ui.internal.app.AppViewState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Stable
internal interface BillingDialogViewState : AppViewState {
  val connected: StateFlow<BillingState>
  val skuList: StateFlow<List<BillingSku>>

  val error: StateFlow<Throwable?>
  val thanksPurchase: StateFlow<BillingPurchase?>

  val isRefreshing: StateFlow<Boolean>
}

@Stable
internal class MutableBillingDialogViewState internal constructor() : BillingDialogViewState {
  override val connected = MutableStateFlow(BillingState.LOADING)
  override val skuList = MutableStateFlow(emptyList<BillingSku>())

  override val error = MutableStateFlow<Throwable?>(null)
  override val thanksPurchase = MutableStateFlow<BillingPurchase?>(null)

  override val icon = MutableStateFlow(0)
  override val name = MutableStateFlow("")
  override val isRefreshing = MutableStateFlow(false)
}
