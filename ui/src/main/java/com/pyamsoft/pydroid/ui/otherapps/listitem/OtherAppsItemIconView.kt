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
import com.pyamsoft.pydroid.arch.BaseUiView
import com.pyamsoft.pydroid.loader.ImageLoader
import com.pyamsoft.pydroid.loader.Loaded
import com.pyamsoft.pydroid.ui.databinding.OtherAppsItemIconBinding

internal class OtherAppsItemIconView internal constructor(
    private val imageLoader: ImageLoader,
    parent: ViewGroup
) : BaseUiView<OtherAppsItemViewState, OtherAppsItemViewEvent, OtherAppsItemIconBinding>(parent) {

    override val viewBinding = OtherAppsItemIconBinding::inflate

    override val layoutRoot by boundView { otherAppsIconRoot }

    private var loaded: Loaded? = null

    init {
        doOnTeardown {
            clear()
        }
    }

    private fun clear() {
        loaded?.dispose()
        loaded = null
    }

    override fun onRender(state: OtherAppsItemViewState) {
        handleApp(state)
    }

    private fun handleApp(state: OtherAppsItemViewState) {
        state.app.let { app ->
            clear()
            loaded = imageLoader.load(app.icon).into(binding.otherAppsIcon)
        }
    }
}
