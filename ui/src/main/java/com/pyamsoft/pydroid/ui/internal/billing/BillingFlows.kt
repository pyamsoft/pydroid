/*
 * Copyright 2020 Peter Kenji Yamanaka
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

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.pyamsoft.pydroid.billing.BillingSku
import com.pyamsoft.pydroid.billing.BillingState
import com.pyamsoft.pydroid.ui.internal.app.AppViewState

internal interface BillingViewState : AppViewState {
  override val icon: Int
  override val name: String
  val connected: BillingState
  val skuList: List<BillingSku>
  val error: Throwable?
}

internal class MutableBillingViewState : BillingViewState {
  override var skuList by mutableStateOf(emptyList<BillingSku>())
  override var connected by mutableStateOf(BillingState.LOADING)
  override var error by mutableStateOf<Throwable?>(null)
  override var icon by mutableStateOf(0)
  override var name by mutableStateOf("")
}
