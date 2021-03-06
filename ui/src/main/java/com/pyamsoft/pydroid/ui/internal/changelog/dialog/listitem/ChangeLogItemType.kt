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

package com.pyamsoft.pydroid.ui.internal.changelog.dialog.listitem

import android.view.ViewGroup
import com.pyamsoft.pydroid.arch.BaseUiView
import com.pyamsoft.pydroid.arch.UiRender
import com.pyamsoft.pydroid.arch.UnitViewEvent
import com.pyamsoft.pydroid.ui.databinding.ChangelogItemTypeBinding
import com.pyamsoft.pydroid.ui.internal.changelog.ChangeLogLine

internal class ChangeLogItemType internal constructor(parent: ViewGroup) :
    BaseUiView<ChangeLogItemViewState, UnitViewEvent, ChangelogItemTypeBinding>(parent) {

  override val viewBinding = ChangelogItemTypeBinding::inflate

  override val layoutRoot by boundView { changelogItemTypeRoot }

  init {
    doOnTeardown { clear() }
  }

  private fun clear() {
    binding.changelogItemType.text = ""
  }

  override fun onRender(state: UiRender<ChangeLogItemViewState>) {
    state.mapChanged { it.line }.render(viewScope) { handleLine(it) }
  }

  private fun handleLine(line: ChangeLogLine) {
    binding.changelogItemType.text = line.type.name
  }
}
