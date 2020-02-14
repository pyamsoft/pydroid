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

package com.pyamsoft.pydroid.ui.rating.dialog

import android.view.ViewGroup
import androidx.annotation.CheckResult
import androidx.lifecycle.LifecycleOwner
import com.pyamsoft.pydroid.loader.LoaderModule
import com.pyamsoft.pydroid.ui.PYDroidViewModelFactory

internal interface RatingDialogComponent {

    fun inject(dialog: RatingDialog)

    interface Factory {

        @CheckResult
        fun create(
            parent: ViewGroup,
            owner: LifecycleOwner,
            rateLink: String
        ): RatingDialogComponent

        data class Parameters internal constructor(
            internal val factory: PYDroidViewModelFactory,
            internal val module: LoaderModule
        )
    }

    class Impl private constructor(
        private val parent: ViewGroup,
        private val rateLink: String,
        private val owner: LifecycleOwner,
        private val params: Factory.Parameters
    ) : RatingDialogComponent {

        override fun inject(dialog: RatingDialog) {
            val icon = RatingIconView(params.module.provideLoader(), parent)
            val changelog = RatingChangelogView(parent)
            val controls = RatingControlsView(rateLink, owner, parent)

            dialog.factory = params.factory
            dialog.iconView = icon
            dialog.changelogView = changelog
            dialog.controlsView = controls
        }

        internal class FactoryImpl internal constructor(
            private val params: Factory.Parameters
        ) : Factory {

            override fun create(
                parent: ViewGroup,
                owner: LifecycleOwner,
                rateLink: String
            ): RatingDialogComponent {
                return Impl(parent, rateLink, owner, params)
            }
        }
    }
}
