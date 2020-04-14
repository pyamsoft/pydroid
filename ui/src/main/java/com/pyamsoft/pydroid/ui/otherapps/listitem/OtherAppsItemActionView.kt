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
import com.pyamsoft.pydroid.ui.databinding.OtherAppsItemActionBinding
import com.pyamsoft.pydroid.ui.util.setOnDebouncedClickListener

internal class OtherAppsItemActionView internal constructor(
    parent: ViewGroup
) : BaseUiView<OtherAppsItemViewState, OtherAppsItemViewEvent, OtherAppsItemActionBinding>(parent) {

    override val viewBinding = OtherAppsItemActionBinding::inflate

    override val layoutRoot by boundView { otherAppsActions }

    init {
        doOnInflate {
            binding.actionOpenStore.setOnDebouncedClickListener {
                publish(OtherAppsItemViewEvent.OpenStore)
            }
            binding.actionViewSource.setOnDebouncedClickListener {
                publish(OtherAppsItemViewEvent.ViewSource)
            }
        }

        doOnTeardown {
            binding.actionViewSource.setOnDebouncedClickListener(null)
            binding.actionOpenStore.setOnDebouncedClickListener(null)
        }
    }

    override fun onRender(state: OtherAppsItemViewState) {
    }
}
