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

package com.pyamsoft.pydroid.ui.widget.shadow

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.arch.UiView
import com.pyamsoft.pydroid.ui.arch.ViewEvent.EMPTY
import com.pyamsoft.pydroid.ui.arch.ViewEvent.EmptyPublisher

class DropshadowView(private val parent: ViewGroup) : UiView<EMPTY>(EmptyPublisher) {

  private lateinit var shadow: View

  override fun id(): Int {
    return shadow.id
  }

  override fun inflate(savedInstanceState: Bundle?) {
    parent.inflateAndAdd(R.layout.dropshadow) {
      shadow = findViewById(R.id.dropshadow_view)
    }
  }

  override fun teardown() {
  }

  override fun saveState(outState: Bundle) {
  }

}