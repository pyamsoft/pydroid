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

package com.pyamsoft.pydroid.ui.widget.spinner

import android.os.Bundle
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.pyamsoft.pydroid.ui.arch.UiToggleView
import com.pyamsoft.pydroid.ui.arch.UiView
import com.pyamsoft.pydroid.ui.arch.ViewEvent.EMPTY
import com.pyamsoft.pydroid.ui.arch.ViewEvent.EmptyPublisher
import com.pyamsoft.pydroid.ui.databinding.LoadingSpinnerBinding

class SpinnerView internal constructor(
  private val parent: ViewGroup
) : UiView<EMPTY>(EmptyPublisher), UiToggleView<EMPTY> {

  private lateinit var binding: LoadingSpinnerBinding

  override fun id(): Int {
    return binding.progressSpinner.id
  }

  override fun inflate(savedInstanceState: Bundle?) {
    binding = LoadingSpinnerBinding.inflate(parent.inflater(), parent, false)
    parent.addView(binding.root)
  }

  override fun saveState(outState: Bundle) {
  }

  override fun show() {
    binding.progressSpinner.isVisible = true
  }

  override fun hide() {
    binding.progressSpinner.isVisible = false
  }

}