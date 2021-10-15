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

package com.pyamsoft.pydroid.ui.internal.app

import android.view.ViewGroup
import com.pyamsoft.pydroid.arch.BaseUiView
import com.pyamsoft.pydroid.arch.UiRender
import com.pyamsoft.pydroid.ui.databinding.ChangelogNameBinding

internal abstract class AppName<S : AppViewState> protected constructor(parent: ViewGroup) :
    BaseUiView<S, Nothing, ChangelogNameBinding>(parent) {

  final override val viewBinding = ChangelogNameBinding::inflate

  final override val layoutRoot by boundView { changelogName }

  init {
    doOnTeardown { clear() }
  }

  final override fun onRender(state: UiRender<S>) {
    state.mapChanged { it.name }.render(viewScope) { handleName(it) }
  }

  private fun handleName(name: CharSequence) {
    if (name.isBlank()) {
      clear()
    } else {
      binding.changelogName.text = name
    }
  }

  private fun clear() {
    binding.changelogName.text = ""
  }
}
