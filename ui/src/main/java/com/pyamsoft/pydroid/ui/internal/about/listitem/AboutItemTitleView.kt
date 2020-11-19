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

package com.pyamsoft.pydroid.ui.internal.about.listitem

import android.view.ViewGroup
import androidx.annotation.CheckResult
import androidx.annotation.StringRes
import com.pyamsoft.pydroid.arch.BaseUiView
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.databinding.AboutItemTitleBinding

internal class AboutItemTitleView internal constructor(
    parent: ViewGroup
) : BaseUiView<AboutItemViewState, AboutItemViewEvent, AboutItemTitleBinding>(parent) {

    override val viewBinding = AboutItemTitleBinding::inflate

    override val layoutRoot by boundView { aboutTitle }

    init {
        doOnTeardown {
            clear()
        }
    }

    private fun clear() {
        binding.title.text = ""
        binding.license.text = ""
    }

    override fun onRender(state: AboutItemViewState) {
        handleLibrary(state)
    }

    private fun handleLibrary(state: AboutItemViewState) {
        state.library.let { library ->
            binding.title.text = library.name
            binding.license.text = getString(R.string.license_name, library.licenseName)
        }
    }

    @CheckResult
    private fun getString(@StringRes id: Int, vararg formatArgs: Any): String {
        return layoutRoot.context.getString(id, *formatArgs)
    }
}
