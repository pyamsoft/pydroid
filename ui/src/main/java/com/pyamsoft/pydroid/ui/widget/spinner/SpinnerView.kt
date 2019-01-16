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
import android.widget.ProgressBar
import androidx.core.view.isVisible
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.arch.UiToggleView
import com.pyamsoft.pydroid.ui.arch.UiView
import com.pyamsoft.pydroid.ui.arch.ViewEvent.EMPTY
import com.pyamsoft.pydroid.ui.arch.ViewEvent.EmptyPublisher

class SpinnerView(
  private val parent: ViewGroup
) : UiView<EMPTY>(EmptyPublisher), UiToggleView<EMPTY> {

  private lateinit var layoutRoot: ViewGroup
  private lateinit var spinner: ProgressBar

  override fun id(): Int {
    return layoutRoot.id
  }

  override fun inflate(savedInstanceState: Bundle?) {
    parent.inflateAndAdd(R.layout.loading_spinner) {
      layoutRoot = findViewById(R.id.spinner_root)
      spinner = findViewById(R.id.spinner)
    }
  }

  override fun teardown() {
    spinner.isVisible = false
  }

  override fun saveState(outState: Bundle) {
  }

  override fun show() {
    spinner.isVisible = true
  }

  override fun hide() {
    spinner.isVisible = false
  }

}