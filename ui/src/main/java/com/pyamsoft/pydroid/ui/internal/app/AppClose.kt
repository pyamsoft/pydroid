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
import androidx.core.view.isVisible
import com.pyamsoft.pydroid.arch.BaseUiView
import com.pyamsoft.pydroid.arch.UiViewEvent
import com.pyamsoft.pydroid.ui.databinding.ChangelogCloseBinding
import com.pyamsoft.pydroid.ui.util.setOnDebouncedClickListener

internal abstract class AppClose<S : AppViewState, V : UiViewEvent>
protected constructor(parent: ViewGroup, showRatingButton: Boolean) :
    BaseUiView<S, V, ChangelogCloseBinding>(parent) {

  final override val viewBinding = ChangelogCloseBinding::inflate

  final override val layoutRoot by boundView { changelogCloseRoot }

  init {

    doOnTeardown { binding.changelogRate.setOnDebouncedClickListener(null) }

    doOnInflate {
      if (showRatingButton) {
        binding.changelogRate.isVisible = true
      }
    }

    doOnInflate { binding.changelogRate.setOnDebouncedClickListener { publishRate() } }

    doOnTeardown { binding.changelogClose.setOnDebouncedClickListener(null) }

    doOnInflate { binding.changelogClose.setOnDebouncedClickListener { publishClose() } }
  }

  protected abstract fun publishRate()

  protected abstract fun publishClose()
}
