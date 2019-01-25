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
import com.pyamsoft.pydroid.ui.arch.BaseUiView
import com.pyamsoft.pydroid.ui.arch.ViewEvent.EMPTY
import com.pyamsoft.pydroid.ui.arch.ViewEvent.EmptyPublisher

class DropshadowView(parent: ViewGroup) : BaseUiView<EMPTY>(parent, EmptyPublisher) {

  private val shadow by lazyView<View>(R.id.dropshadow_view)

  override val layout: Int = R.layout.dropshadow

  override fun id(): Int {
    return shadow.id
  }

  override fun onInflated(
    view: View,
    savedInstanceState: Bundle?
  ) {
  }

  override fun teardown() {
  }

  override fun saveState(outState: Bundle) {
  }

}