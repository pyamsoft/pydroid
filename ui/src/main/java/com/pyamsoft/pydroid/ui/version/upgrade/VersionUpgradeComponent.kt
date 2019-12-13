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

package com.pyamsoft.pydroid.ui.version.upgrade

import android.view.ViewGroup
import androidx.annotation.CheckResult
import androidx.lifecycle.LifecycleOwner
import com.pyamsoft.pydroid.ui.PYDroidViewModelFactory

internal interface VersionUpgradeComponent {

    fun inject(dialog: VersionUpgradeDialog)

    interface Factory {

        @CheckResult
        fun create(
            parent: ViewGroup,
            owner: LifecycleOwner,
            newVersion: Int
        ): VersionUpgradeComponent
    }

    class Impl private constructor(
        private val parent: ViewGroup,
        private val owner: LifecycleOwner,
        private val applicationName: String,
        private val currentVersion: Int,
        private val newVersion: Int,
        private val factory: PYDroidViewModelFactory
    ) : VersionUpgradeComponent {

        override fun inject(dialog: VersionUpgradeDialog) {
            val contentView =
                VersionUpgradeContentView(parent)
            val controlsView = VersionUpgradeControlView(owner, parent)

            dialog.factory = VersionUpgradeViewModelFactory(
                factory, applicationName, currentVersion, newVersion
            )
            dialog.control = controlsView
            dialog.content = contentView
        }

        internal class FactoryImpl internal constructor(
            private val applicationName: String,
            private val currentVersion: Int,
            private val factory: PYDroidViewModelFactory
        ) : Factory {

            override fun create(
                parent: ViewGroup,
                owner: LifecycleOwner,
                newVersion: Int
            ): VersionUpgradeComponent {
                return Impl(
                    parent, owner, applicationName, currentVersion, newVersion, factory
                )
            }
        }
    }
}
