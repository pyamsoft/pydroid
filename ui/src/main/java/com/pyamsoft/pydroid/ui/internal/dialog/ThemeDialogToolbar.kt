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
import com.pyamsoft.pydroid.arch.BaseUiView
import com.pyamsoft.pydroid.ui.internal.dialog.ThemeDialogViewEvent.Close
import com.pyamsoft.pydroid.ui.databinding.ThemeDialogToolbarBinding
import com.pyamsoft.pydroid.ui.util.DebouncedOnClickListener
import com.pyamsoft.pydroid.ui.util.setUpEnabled

internal class ThemeDialogToolbar internal constructor(
    parent: ViewGroup,
    backgroundDrawable: Drawable
) : BaseUiView<ThemeDialogViewState, ThemeDialogViewEvent, ThemeDialogToolbarBinding>(
    parent
) {

    override val viewBinding = ThemeDialogToolbarBinding::inflate

    override val layoutRoot by boundView { themeDialogToolbar }

    init {
        doOnInflate {
            binding.themeDialogToolbar.apply {
                background = backgroundDrawable
                setUpEnabled(true)
                setNavigationOnClickListener(DebouncedOnClickListener.create {
                    publish(Close)
                })
            }
        }

        doOnTeardown {
            binding.themeDialogToolbar.apply {
                setUpEnabled(false)
                setNavigationOnClickListener(null)
            }
        }
    }

    override fun onRender(state: ThemeDialogViewState) {
        handleTitle(state)
    }

    private fun handleTitle(state: ThemeDialogViewState) {
        binding.themeDialogToolbar.title = state.name
    }
}
