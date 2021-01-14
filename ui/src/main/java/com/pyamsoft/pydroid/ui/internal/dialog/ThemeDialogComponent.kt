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

package com.pyamsoft.pydroid.ui.internal.dialog

import android.graphics.drawable.Drawable
import android.view.ViewGroup
import androidx.annotation.CheckResult
import com.pyamsoft.pydroid.arch.createViewModelFactory

internal interface ThemeDialogComponent {

    fun inject(dialog: FullscreenThemeDialog)

    interface Factory {

        @CheckResult
        fun create(
            name: String,
            background: Drawable,
            parent: ViewGroup
        ): ThemeDialogComponent
    }

    class Impl private constructor(
        private val parent: ViewGroup,
        private val background: Drawable,
        name: String
    ) : ThemeDialogComponent {

        private val factory = createViewModelFactory { ThemeDialogViewModel(name) }

        override fun inject(dialog: FullscreenThemeDialog) {
            dialog.toolbar =
                ThemeDialogToolbar(
                    parent,
                    background
                )
            dialog.frame =
                ThemeDialogFrame(parent)
            dialog.factory = factory
        }

        internal class FactoryImpl internal constructor() : Factory {

            override fun create(
                name: String,
                background: Drawable,
                parent: ViewGroup
            ): ThemeDialogComponent {
                return Impl(
                    parent,
                    background,
                    name
                )
            }
        }
    }
}
