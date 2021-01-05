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

import android.app.Activity
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.CheckResult
import com.pyamsoft.pydroid.arch.onlyFactory
import com.pyamsoft.pydroid.billing.BillingModule
import com.pyamsoft.pydroid.bootstrap.changelog.ChangeLogInteractor
import com.pyamsoft.pydroid.loader.ImageLoader
import com.pyamsoft.pydroid.ui.app.ActivityBase
import com.pyamsoft.pydroid.ui.internal.app.AppProvider

internal interface BillingComponent {

    fun inject(activity: ActivityBase)

    @CheckResult
    fun plusDialog(): DialogComponent.Factory

    interface DialogComponent {

        fun inject(dialog: BillingDialog)

        interface Factory {

            @CheckResult
            fun create(
                parent: ViewGroup,
                imageView: ImageView,
                provider: AppProvider,
            ): DialogComponent
        }

        class Impl private constructor(
            private val module: BillingModule,
            private val params: BillingComponent.Factory.Parameters,
            private val imageView: ImageView,
            private val parent: ViewGroup,
            provider: AppProvider,
        ) : DialogComponent {

            private val factory = onlyFactory {
                BillingViewModel(params.interactor, module.provideInteractor(), provider)
            }

            override fun inject(dialog: BillingDialog) {
                dialog.purchaseClient = module.providePurchase()
                dialog.factory = factory
                dialog.iconView = BillingIcon(params.imageLoader, imageView)
                dialog.nameView = BillingName(parent)
            }

            internal class FactoryImpl internal constructor(
                private val module: BillingModule,
                private val params: BillingComponent.Factory.Parameters,
            ) : Factory {

                override fun create(
                    parent: ViewGroup,
                    imageView: ImageView,
                    provider: AppProvider,
                ): DialogComponent {
                    return Impl(module, params, imageView, parent, provider)
                }
            }

        }
    }

    interface Factory {

        @CheckResult
        fun create(activity: Activity): BillingComponent

        data class Parameters internal constructor(
            internal val imageLoader: ImageLoader,
            internal val interactor: ChangeLogInteractor,
        )
    }

    class Impl private constructor(
        private val params: Factory.Parameters,
        activity: Activity
    ) : BillingComponent {

        private val module = BillingModule(
            BillingModule.Parameters(
                activity = activity
            )
        )

        override fun inject(activity: ActivityBase) {
            activity.billingConnector = module.provideConnector()
        }

        override fun plusDialog(): DialogComponent.Factory {
            return DialogComponent.Impl.FactoryImpl(module, params)
        }

        internal class FactoryImpl internal constructor(
            private val params: Factory.Parameters
        ) : Factory {

            override fun create(activity: Activity): BillingComponent {
                return Impl(params, activity)
            }
        }
    }
}
