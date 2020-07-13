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

package com.pyamsoft.pydroid.ui.otherapps

import android.view.ViewGroup
import androidx.annotation.CheckResult
import androidx.lifecycle.LifecycleOwner
import com.pyamsoft.pydroid.ui.arch.PYDroidViewModelFactory

internal interface OtherAppsComponent {

    fun inject(fragment: OtherAppsFragment)

    interface Factory {

        @CheckResult
        fun create(
            parent: ViewGroup,
            owner: LifecycleOwner
        ): OtherAppsComponent

        data class Parameters internal constructor(
            internal val factory: PYDroidViewModelFactory
        )
    }

    class Impl private constructor(
        private val parent: ViewGroup,
        private val owner: LifecycleOwner,
        private val params: Factory.Parameters
    ) : OtherAppsComponent {

        override fun inject(fragment: OtherAppsFragment) {
            val listView = OtherAppsList(owner, parent)
            fragment.factory = params.factory
            fragment.listView = listView
        }

        class FactoryImpl internal constructor(
            private val params: Factory.Parameters
        ) : Factory {

            override fun create(
                parent: ViewGroup,
                owner: LifecycleOwner
            ): OtherAppsComponent {
                return Impl(parent, owner, params)
            }
        }
    }
}
