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

package com.pyamsoft.pydroid.ui.internal.otherapps.listitem

import android.view.ViewGroup
import com.pyamsoft.pydroid.arch.BaseUiView
import com.pyamsoft.pydroid.arch.UiRender
import com.pyamsoft.pydroid.bootstrap.otherapps.api.OtherApp
import com.pyamsoft.pydroid.ui.databinding.OtherAppsItemTitleBinding

internal class OtherAppsItemTitleView internal constructor(parent: ViewGroup) :
    BaseUiView<OtherAppsItemViewState, OtherAppsItemViewEvent, OtherAppsItemTitleBinding>(parent) {

  override val viewBinding = OtherAppsItemTitleBinding::inflate

  override val layoutRoot by boundView { otherAppsTitle }

  init {
    doOnTeardown { clear() }
  }

  private fun clear() {
    binding.title.text = ""
    binding.description.text = ""
  }

  override fun onRender(state: UiRender<OtherAppsItemViewState>) {
    state.mapChanged { it.app }.render(viewScope) { handleApp(it) }
  }

  private fun handleApp(app: OtherApp) {
    binding.title.text = app.name
    binding.description.text = app.description
  }
}
