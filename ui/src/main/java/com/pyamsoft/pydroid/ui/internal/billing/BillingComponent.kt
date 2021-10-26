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

import android.content.Context
import androidx.annotation.CheckResult
import com.pyamsoft.pydroid.arch.createViewModelFactory
import com.pyamsoft.pydroid.billing.BillingModule
import com.pyamsoft.pydroid.bootstrap.changelog.ChangeLogInteractor
import com.pyamsoft.pydroid.bus.EventBus
import com.pyamsoft.pydroid.ui.app.ActivityBase
import com.pyamsoft.pydroid.ui.app.ComposeThemeFactory
import com.pyamsoft.pydroid.ui.internal.app.AppProvider
import com.pyamsoft.pydroid.ui.theme.Theming

internal interface BillingComponent {

  fun inject(activity: ActivityBase)

  @CheckResult fun plusDialog(): DialogComponent.Factory

  interface DialogComponent {

    fun inject(dialog: BillingDialog)

    interface Factory {

      @CheckResult fun create(provider: AppProvider): DialogComponent
    }

    class Impl
    private constructor(
        private val module: BillingModule,
        private val params: BillingComponent.Factory.Parameters,
        provider: AppProvider,
    ) : DialogComponent {

      private val factory = createViewModelFactory {
        BillingViewModel(params.interactor, module.provideInteractor(), provider)
      }

      override fun inject(dialog: BillingDialog) {
        dialog.composeTheme = params.composeTheme
        dialog.purchaseClient = module.provideLauncher()
        dialog.factory = factory
      }

      internal class FactoryImpl
      internal constructor(
          private val module: BillingModule,
          private val params: BillingComponent.Factory.Parameters,
      ) : Factory {

        override fun create(provider: AppProvider): DialogComponent {
          return Impl(module, params, provider)
        }
      }
    }
  }

  interface Factory {

    @CheckResult fun create(): BillingComponent

    data class Parameters
    internal constructor(
        internal val context: Context,
        internal val theming: Theming,
        internal val errorBus: EventBus<Throwable>,
        internal val interactor: ChangeLogInteractor,
        internal val composeTheme: ComposeThemeFactory,
    )
  }

  class Impl private constructor(private val params: Factory.Parameters) : BillingComponent {

    // Make this module each time since if it falls out of scope, the in-app billing system
    // will crash
    private val module =
        BillingModule(
            BillingModule.Parameters(
                context = params.context.applicationContext,
                errorBus = params.errorBus,
            ))

    override fun inject(activity: ActivityBase) {
      activity.billingConnector = module.provideConnector()
      activity.theming = params.theming
    }

    override fun plusDialog(): DialogComponent.Factory {
      return DialogComponent.Impl.FactoryImpl(module, params)
    }

    internal class FactoryImpl internal constructor(private val params: Factory.Parameters) :
        Factory {

      override fun create(): BillingComponent {
        return Impl(params)
      }
    }
  }
}
