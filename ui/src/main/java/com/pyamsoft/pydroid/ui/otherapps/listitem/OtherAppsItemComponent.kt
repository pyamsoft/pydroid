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

package com.pyamsoft.pydroid.ui.otherapps.listitem

import android.view.ViewGroup
import androidx.annotation.CheckResult
import com.pyamsoft.pydroid.loader.ImageLoader

internal interface OtherAppsItemComponent {

    fun inject(viewHolder: OtherAppsViewHolder)

    interface Factory {

        @CheckResult
        fun create(parent: ViewGroup): OtherAppsItemComponent
    }

    class Impl private constructor(
        private val parent: ViewGroup,
        private val imageLoader: ImageLoader
    ) : OtherAppsItemComponent {

        override fun inject(viewHolder: OtherAppsViewHolder) {
            val title = OtherAppsItemTitleView(parent)
            val icon = OtherAppsItemIconView(imageLoader, parent)
            val action = OtherAppsItemActionView(parent)
            viewHolder.titleView = title
            viewHolder.iconView = icon
            viewHolder.actionView = action
        }

        class FactoryImpl internal constructor(
            private val imageLoader: ImageLoader
        ) : Factory {

            override fun create(parent: ViewGroup): OtherAppsItemComponent {
                return Impl(parent, imageLoader)
            }
        }
    }
}
