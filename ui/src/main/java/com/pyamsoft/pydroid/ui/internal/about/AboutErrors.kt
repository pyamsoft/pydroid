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

package com.pyamsoft.pydroid.ui.internal.about

import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import com.pyamsoft.pydroid.arch.UiRender
import com.pyamsoft.pydroid.arch.UiView
import com.pyamsoft.pydroid.ui.util.Snackbreak

internal class AboutErrors
internal constructor(
    private val owner: LifecycleOwner,
    private val parent: ViewGroup,
) : UiView<AboutViewState, AboutViewEvent.ErrorEvent>() {

  override fun render(state: UiRender<AboutViewState>) {
    state.mapChanged { it.loadError }.render(viewScope) { handleLoadError(it) }
    state.mapChanged { it.navigationError }.render(viewScope) { handleNavigateError(it) }
  }

  private fun handleLoadError(throwable: Throwable?) {
    if (throwable != null) {
      showLoadError(throwable)
    }
  }

  private fun handleNavigateError(throwable: Throwable?) {
    if (throwable != null) {
      showNavigationError(throwable)
    }
  }

  private fun showNavigationError(error: Throwable) {
    Snackbreak.bindTo(owner) {
      long(
          parent,
          error.message ?: "An unexpected error occurred.",
          onHidden = { _, _ -> publish(AboutViewEvent.ErrorEvent.HideNavigationError) })
    }
  }

  private fun showLoadError(error: Throwable) {
    Snackbreak.bindTo(owner) {
      long(
          parent,
          error.message ?: "An unexpected error occurred.",
          onHidden = { _, _ -> publish(AboutViewEvent.ErrorEvent.HideLoadError) })
    }
  }
}
