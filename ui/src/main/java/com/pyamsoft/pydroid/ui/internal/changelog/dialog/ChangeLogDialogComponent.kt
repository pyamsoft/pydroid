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

package com.pyamsoft.pydroid.ui.internal.changelog.dialog

import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.CheckResult
import com.pyamsoft.pydroid.arch.createViewModelFactory
import com.pyamsoft.pydroid.bootstrap.changelog.ChangeLogInteractor
import com.pyamsoft.pydroid.loader.ImageLoader
import com.pyamsoft.pydroid.ui.internal.changelog.ChangeLogProvider

internal interface ChangeLogDialogComponent {

    fun inject(dialog: ChangeLogDialog)

    interface Factory {

        @CheckResult
        fun create(
            parent: ViewGroup,
            imageView: ImageView,
            provider: ChangeLogProvider
        ): ChangeLogDialogComponent

        data class Parameters internal constructor(
            internal val imageLoader: ImageLoader,
            internal val interactor: ChangeLogInteractor,
        )
    }

    class Impl private constructor(
        private val parent: ViewGroup,
        private val imageView: ImageView,
        private val provider: ChangeLogProvider,
        private val params: Factory.Parameters,
    ) : ChangeLogDialogComponent {

        override fun inject(dialog: ChangeLogDialog) {
            dialog.factory = createViewModelFactory { ChangeLogDialogViewModel(params.interactor, provider) }
            dialog.listView = ChangeLogList(parent)
            dialog.nameView = ChangeLogName(parent)
            dialog.closeView = ChangeLogClose(parent)
            dialog.iconView = ChangeLogIcon(params.imageLoader, imageView)
        }

        internal class FactoryImpl internal constructor(
            private val params: Factory.Parameters
        ) : Factory {

            override fun create(
                parent: ViewGroup,
                imageView: ImageView,
                provider: ChangeLogProvider
            ): ChangeLogDialogComponent {
                return Impl(parent, imageView, provider, params)
            }
        }
    }
}
