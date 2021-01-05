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

import com.pyamsoft.pydroid.arch.UiControllerEvent
import com.pyamsoft.pydroid.arch.UiViewEvent
import com.pyamsoft.pydroid.billing.BillingSku
import com.pyamsoft.pydroid.ui.internal.app.AppState

internal data class BillingDialogViewState internal constructor(
    override val icon: Int,
    override val name: CharSequence,
    val skuList: List<BillingSku>,
    val error: Throwable?
) : AppState

internal sealed class BillingDialogViewEvent : UiViewEvent {

    object Close : BillingDialogViewEvent()

    data class Purchase internal constructor(
        val index: Int
    ) : BillingDialogViewEvent()
}

internal sealed class BillingDialogControllerEvent : UiControllerEvent {

    object Close : BillingDialogControllerEvent()

    data class LaunchPurchase internal constructor(
        val sku: BillingSku
    ) : BillingDialogControllerEvent()

}
