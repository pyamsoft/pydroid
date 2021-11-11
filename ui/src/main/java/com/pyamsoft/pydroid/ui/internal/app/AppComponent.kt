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

package com.pyamsoft.pydroid.ui.internal.app

import androidx.annotation.CheckResult
import com.pyamsoft.pydroid.billing.BillingModule
import com.pyamsoft.pydroid.bootstrap.libraries.OssLibraries
import com.pyamsoft.pydroid.ui.app.PYDroidActivity
import com.pyamsoft.pydroid.ui.internal.billing.BillingComponent

internal interface AppComponent {

  fun inject(activity: PYDroidActivity)

  @CheckResult fun plusDialog(): BillingComponent.DialogComponent.Factory

  interface Factory {

    @CheckResult fun create(): AppComponent
  }

  class Impl private constructor(private val params: BillingComponent.Factory.Parameters) :
      AppComponent {

    // Make this module each time since if it falls out of scope, the in-app billing system
    // will crash
    private val module =
        BillingModule(
            BillingModule.Parameters(
                context = params.context.applicationContext,
                errorBus = params.errorBus,
            ))

    override fun inject(activity: PYDroidActivity) {
      activity.billing = BillingDelegate(this, module.provideConnector())
    }

    override fun plusDialog(): BillingComponent.DialogComponent.Factory {
      return BillingComponent.DialogComponent.Impl.FactoryImpl(module, params)
    }

    class FactoryImpl
    internal constructor(private val params: BillingComponent.Factory.Parameters) : Factory {

      override fun create(): AppComponent {
        OssLibraries.usingUi = true
        return Impl(params)
      }
    }
  }
}
