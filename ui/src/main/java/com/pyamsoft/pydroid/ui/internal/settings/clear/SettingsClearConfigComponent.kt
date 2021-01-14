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

package com.pyamsoft.pydroid.ui.internal.settings.clear

import androidx.annotation.CheckResult
import com.pyamsoft.pydroid.arch.createFactory
import com.pyamsoft.pydroid.bootstrap.settings.SettingsInteractor

internal interface SettingsClearConfigComponent {

    fun inject(dialog: SettingsClearConfigDialog)

    interface Factory {

        @CheckResult
        fun create(): SettingsClearConfigComponent

        data class Parameters internal constructor(
            internal val settingsInteractor: SettingsInteractor
        )
    }

    class Impl internal constructor(
        private val params: Factory.Parameters
    ) : SettingsClearConfigComponent {

        private val factory = createFactory {
            SettingsClearConfigViewModel(params.settingsInteractor)
        }

        override fun inject(dialog: SettingsClearConfigDialog) {
            dialog.factory = factory
        }

        class FactoryImpl internal constructor(private val params: Factory.Parameters) : Factory {

            override fun create(): SettingsClearConfigComponent {
                return Impl(params)
            }
        }
    }

}
