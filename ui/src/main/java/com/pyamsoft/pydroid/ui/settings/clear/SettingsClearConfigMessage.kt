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

package com.pyamsoft.pydroid.ui.settings.clear

import android.view.ViewGroup
import com.pyamsoft.pydroid.arch.BaseUiView
import com.pyamsoft.pydroid.ui.databinding.ClearConfigMessageBinding

internal class SettingsClearConfigMessage internal constructor(
    parent: ViewGroup
) : BaseUiView<SettingsClearConfigViewState, SettingsClearConfigViewEvent, ClearConfigMessageBinding>(
    parent
) {

    override val viewBinding = ClearConfigMessageBinding::inflate

    override val layoutRoot by boundView { clearConfigMessage }

    init {
        doOnInflate {
            binding.clearConfigMessage.text = """
        Really reset all application settings?
        
        All saved data will be cleared and all settings reset to default. The app act as if you are launching it for the first time. This cannot be undone.
            """.trimIndent()
        }

        doOnTeardown {
            binding.clearConfigMessage.text = ""
        }
    }

    override fun onRender(state: SettingsClearConfigViewState) {
    }
}
