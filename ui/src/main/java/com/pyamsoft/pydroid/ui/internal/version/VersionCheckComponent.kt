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

package com.pyamsoft.pydroid.ui.internal.version

import android.view.ViewGroup
import androidx.annotation.CheckResult
import androidx.lifecycle.LifecycleOwner
import com.pyamsoft.pydroid.ui.internal.arch.PYDroidViewModelFactory
import com.pyamsoft.pydroid.ui.internal.rating.RatingComponent
import com.pyamsoft.pydroid.ui.version.VersionCheckActivity

internal interface VersionCheckComponent {

    @CheckResult
    fun plusRating(): RatingComponent.Factory

    fun inject(activity: VersionCheckActivity)

    interface Factory {

        @CheckResult
        fun create(
            owner: LifecycleOwner,
            snackbarRootProvider: () -> ViewGroup
        ): VersionCheckComponent

        data class Parameters internal constructor(
            internal val factory: PYDroidViewModelFactory
        )
    }

    class Impl private constructor(
        private val snackbarRootProvider: () -> ViewGroup,
        private val owner: LifecycleOwner,
        private val params: Factory.Parameters
    ) : VersionCheckComponent {

        private val ratingParams by lazy {
            RatingComponent.Factory.Parameters(
                factory = params.factory
            )
        }

        override fun plusRating(): RatingComponent.Factory {
            return RatingComponent.Impl.FactoryImpl(ratingParams)
        }

        override fun inject(activity: VersionCheckActivity) {
            val versionView = VersionCheckView(
                owner,
                snackbarRootProvider
            )

            activity.versionFactory = params.factory
            activity.versionCheckView = versionView
        }

        internal class FactoryImpl internal constructor(
            private val params: Factory.Parameters
        ) : Factory {

            override fun create(
                owner: LifecycleOwner,
                snackbarRootProvider: () -> ViewGroup
            ): VersionCheckComponent {
                return Impl(snackbarRootProvider, owner, params)
            }
        }
    }
}
