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

package com.pyamsoft.pydroid.ui.internal.rating

import android.app.Activity
import android.view.ViewGroup
import androidx.annotation.CheckResult
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import com.pyamsoft.pydroid.arch.onlyFactory
import com.pyamsoft.pydroid.bootstrap.rating.RatingModule
import com.pyamsoft.pydroid.bootstrap.rating.RatingPreferences
import com.pyamsoft.pydroid.ui.rating.RatingActivity

internal interface RatingComponent {

    fun inject(activity: RatingActivity)

    interface Factory {

        @CheckResult
        fun create(
            activity: Activity,
            owner: LifecycleOwner,
            snackbarRootProvider: () -> ViewGroup
        ): RatingComponent

        data class Parameters internal constructor(
            internal val isFake: Boolean,
            internal val preferences: RatingPreferences
        )
    }

    class Impl private constructor(
        params: Factory.Parameters,
        activity: Activity,
        private val owner: LifecycleOwner,
        private val snackbarRootProvider: () -> ViewGroup
    ) : RatingComponent {

        private val factory: ViewModelProvider.Factory

        init {
            val module = RatingModule(
                RatingModule.Parameters(
                    activity = activity,
                    isFake = params.isFake,
                    preferences = params.preferences
                )
            )
            factory = onlyFactory { RatingViewModel(module.provideInteractor()) }
        }

        override fun inject(activity: RatingActivity) {
            activity.ratingView = RatingView(owner, snackbarRootProvider)
            activity.ratingFactory = factory
        }

        internal class FactoryImpl internal constructor(
            private val params: Factory.Parameters
        ) : Factory {

            override fun create(
                activity: Activity,
                owner: LifecycleOwner,
                snackbarRootProvider: () -> ViewGroup
            ): RatingComponent {
                return Impl(params, activity, owner, snackbarRootProvider)
            }
        }
    }
}
