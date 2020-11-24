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
import com.pyamsoft.pydroid.arch.BaseUiView
import com.pyamsoft.pydroid.loader.ImageLoader
import com.pyamsoft.pydroid.loader.Loaded
import com.pyamsoft.pydroid.ui.databinding.ChangelogIconBinding

internal class ChangeLogIcon internal constructor(
    parent: ViewGroup,
    private val imageLoader: ImageLoader
) : BaseUiView<ChangeLogDialogViewState, ChangeLogDialogViewEvent, ChangelogIconBinding>(parent) {

    override val viewBinding = ChangelogIconBinding::inflate

    override val layoutRoot by boundView { changelogIcon }

    private var loaded: Loaded? = null

    init {
        doOnTeardown {
            clear()
        }
    }

    override fun onRender(state: ChangeLogDialogViewState) {
        handleIcon(state)
    }

    private fun handleIcon(state: ChangeLogDialogViewState) {
        state.icon.let { icon ->
            clear()
            if (icon != 0) {
                loaded = imageLoader.load(icon).into(binding.changelogIcon)
            }
        }
    }

    private fun clear() {
        loaded?.dispose()
        loaded = null
    }

}
