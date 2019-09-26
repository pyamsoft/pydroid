/*
 * Copyright 2019 Peter Kenji Yamanaka
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
import com.pyamsoft.pydroid.ui.PYDroidViewModelFactory

internal interface RatingComponent {

    fun inject(activity: RatingActivity)

    interface Factory {

        @CheckResult
        fun create(): RatingComponent
    }

    class Impl private constructor(
        private val factory: PYDroidViewModelFactory
    ) : RatingComponent {

        override fun inject(activity: RatingActivity) {
            activity.ratingFactory = factory
        }

        internal class FactoryImpl internal constructor(
            private val factory: PYDroidViewModelFactory
        ) : Factory {

            override fun create(): RatingComponent {
                return Impl(factory)
            }
        }
    }
}
