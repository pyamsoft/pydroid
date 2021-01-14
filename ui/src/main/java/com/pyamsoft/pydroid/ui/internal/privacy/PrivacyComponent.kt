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

package com.pyamsoft.pydroid.ui.internal.privacy

import android.view.ViewGroup
import androidx.annotation.CheckResult
import androidx.lifecycle.LifecycleOwner
import com.pyamsoft.pydroid.arch.createViewModelFactory
import com.pyamsoft.pydroid.ui.privacy.PrivacyActivity

internal interface PrivacyComponent {

    fun inject(activity: PrivacyActivity)

    interface Factory {

        @CheckResult
        fun create(
            owner: LifecycleOwner,
            snackbarRootProvider: () -> ViewGroup
        ): PrivacyComponent
    }

    class Impl private constructor(
        private val snackbarRootProvider: () -> ViewGroup,
        private val owner: LifecycleOwner,
    ) : PrivacyComponent {

        private val factory = createViewModelFactory { PrivacyViewModel() }

        override fun inject(activity: PrivacyActivity) {
            activity.privacyFactory = factory
            activity.privacyView = PrivacyView(
                owner,
                snackbarRootProvider
            )
        }

        internal class FactoryImpl internal constructor() : Factory {

            override fun create(
                owner: LifecycleOwner,
                snackbarRootProvider: () -> ViewGroup
            ): PrivacyComponent {
                return Impl(snackbarRootProvider, owner)
            }
        }
    }
}
