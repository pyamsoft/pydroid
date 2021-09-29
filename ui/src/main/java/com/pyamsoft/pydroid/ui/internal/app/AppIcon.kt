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

import android.widget.ImageView
import com.pyamsoft.pydroid.arch.UiRender
import com.pyamsoft.pydroid.arch.UiView
import com.pyamsoft.pydroid.core.requireNotNull
import com.pyamsoft.pydroid.loader.ImageLoader
import com.pyamsoft.pydroid.loader.Loaded

internal abstract class AppIcon<S : AppState>
protected constructor(private val imageLoader: ImageLoader, icon: ImageView) :
    UiView<S, Nothing>() {

  private var iconView: ImageView? = icon
  private var loaded: Loaded? = null

  init {
    doOnTeardown { clear() }
  }

  final override fun onFinalTeardown() {
    iconView = null
  }

  final override fun render(state: UiRender<S>) {
    state.mapChanged { it.icon }.render(viewScope) { handleIcon(it) }
  }

  private fun handleIcon(icon: Int) {
    clear()
    if (icon != 0) {
      loaded = imageLoader.asDrawable().load(icon).into(iconView.requireNotNull())
    }
  }

  private fun clear() {
    loaded?.dispose()
    loaded = null
  }
}
