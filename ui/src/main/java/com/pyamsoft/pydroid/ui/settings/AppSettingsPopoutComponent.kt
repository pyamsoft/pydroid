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

package com.pyamsoft.pydroid.ui.settings

import android.graphics.drawable.Drawable
import android.view.ViewGroup
import androidx.annotation.CheckResult
import com.pyamsoft.pydroid.arch.onlyFactory

internal interface AppSettingsPopoutComponent {

    fun inject(dialog: AppSettingsPopoutDialog)

    interface Factory {

        @CheckResult
        fun create(
            name: String,
            background: Drawable, parent: ViewGroup
        ): AppSettingsPopoutComponent

        data class Parameters internal constructor(
            internal val debug: Boolean
        )
    }

    class Impl private constructor(
        private val parent: ViewGroup,
        private val background: Drawable,
        name: String,
        params: Factory.Parameters
    ) : AppSettingsPopoutComponent {

        private val factory = onlyFactory(params.debug) { AppSettingsPopoutViewModel(name, it) }

        override fun inject(dialog: AppSettingsPopoutDialog) {
            dialog.toolbar = AppSettingsPopoutToolbar(parent, background)
            dialog.frame = AppSettingsPopoutFrame(parent)
            dialog.factory = factory
        }

        internal class FactoryImpl internal constructor(
            private val params: Factory.Parameters
        ) : Factory {

            override fun create(
                name: String,
                background: Drawable,
                parent: ViewGroup
            ): AppSettingsPopoutComponent {
                return Impl(parent, background, name, params)
            }
        }
    }
}
