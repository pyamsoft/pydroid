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
 *
 */

package com.pyamsoft.pydroid.ui.rating

import androidx.annotation.CheckResult
import com.pyamsoft.pydroid.ui.arch.PYDroidViewModelFactory

internal interface RatingComponent {

    fun inject(activity: RatingActivity)

    interface Factory {

        @CheckResult
        fun create(): RatingComponent

        data class Parameters internal constructor(
            internal val factory: PYDroidViewModelFactory
        )
    }

    class Impl private constructor(
        private val params: Factory.Parameters
    ) : RatingComponent {

        override fun inject(activity: RatingActivity) {
            activity.ratingFactory = params.factory
        }

        internal class FactoryImpl internal constructor(
            private val params: Factory.Parameters
        ) : Factory {

            override fun create(): RatingComponent {
                return Impl(params)
            }
        }
    }
}
