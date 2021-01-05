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

package com.pyamsoft.pydroid.billing

import android.app.Activity
import androidx.annotation.CheckResult

/**
 * Billing module
 */
public class BillingModule(params: Parameters) {

    private val interactor: BillingInteractor
    private val purchase: BillingPurchase
    private val connector: BillingConnector

    init {
        val impl = PlayStoreBillingInteractor(params.activity)
        interactor = impl
        purchase = impl
        connector = impl
    }

    /**
     * Provide a billing instance
     */
    @CheckResult
    public fun provideInteractor(): BillingInteractor {
        return interactor
    }

    /**
     * Provide a purchase instance
     */
    @CheckResult
    public fun providePurchase(): BillingPurchase {
        return purchase
    }

    /**
     * Provide a connector instance
     */
    @CheckResult
    public fun provideConnector(): BillingConnector {
        return connector
    }

    /**
     * Module parameters
     */
    public data class Parameters(
        internal val activity: Activity,
    )
}
