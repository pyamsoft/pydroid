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
import com.pyamsoft.pydroid.arch.BaseUiView
import com.pyamsoft.pydroid.ui.databinding.AppSettingsPopoutToolbarBinding
import com.pyamsoft.pydroid.ui.util.DebouncedOnClickListener
import com.pyamsoft.pydroid.ui.util.setUpEnabled

internal class AppSettingsPopoutToolbar internal constructor(
    parent: ViewGroup,
    background: Drawable
) : BaseUiView<AppSettingsPopoutViewState, AppSettingsPopoutViewEvent, AppSettingsPopoutToolbarBinding>(
    parent
) {

    override val viewBinding = AppSettingsPopoutToolbarBinding::inflate

    override val layoutRoot by boundView { settingsPopoutToolbar }

    init {
        doOnInflate {
            binding.settingsPopoutToolbar.background = background
            binding.settingsPopoutToolbar.setUpEnabled(true)
            binding.settingsPopoutToolbar.setNavigationOnClickListener(DebouncedOnClickListener.create {
                publish(AppSettingsPopoutViewEvent.ClosePopout)
            })
        }

        doOnTeardown {
            binding.settingsPopoutToolbar.setUpEnabled(false)
            binding.settingsPopoutToolbar.setNavigationOnClickListener(null)
        }
    }

    override fun onRender(state: AppSettingsPopoutViewState) {
        layoutRoot.post { handleTitle(state) }
    }

    private fun handleTitle(state: AppSettingsPopoutViewState) {
        binding.settingsPopoutToolbar.title = state.name
    }
}
