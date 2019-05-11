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

package com.pyamsoft.pydroid.ui.version

import android.os.Bundle
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import com.pyamsoft.pydroid.arch.BaseUiView
import com.pyamsoft.pydroid.arch.UnitViewEvent
import com.pyamsoft.pydroid.arch.onChange
import com.pyamsoft.pydroid.ui.arch.InvalidIdException
import com.pyamsoft.pydroid.ui.util.Snackbreak

internal class VersionView internal constructor(
  private val owner: LifecycleOwner,
  parent: ViewGroup
) : BaseUiView<VersionViewState, UnitViewEvent>() {

  private var parent: ViewGroup? = parent

  override fun id(): Int {
    throw InvalidIdException
  }

  override fun inflate(savedInstanceState: Bundle?) {
  }

  override fun render(
    state: VersionViewState,
    oldState: VersionViewState?
  ) {
    state.onChange(oldState, field = { it.isLoading }) { loading ->
      if (loading != null) {
        if (loading.isLoading) {
          showUpdating()
        } else {
          dismissUpdating()
        }
      }
    }

    state.onChange(oldState, field = { it.throwable }) { throwable ->
      if (throwable == null) {
        clearError()
      } else {
        showError(throwable)
      }
    }
  }

  override fun teardown() {
    parent = null
    clearError()
  }

  override fun saveState(outState: Bundle) {
  }

  private fun showUpdating() {
    Snackbreak.bindTo(owner)
        .short(requireNotNull(parent), "Checking for updates")
        .show()
  }

  private fun dismissUpdating() {
    Snackbreak.bindTo(owner)
        .dismiss()
  }

  private fun showError(error: Throwable) {
    Snackbreak.bindTo(owner)
        .short(
            requireNotNull(parent),
            error.message ?: "No activity found that can handle this URL"
        )
        .show()
  }

  private fun clearError() {
    Snackbreak.bindTo(owner)
        .dismiss()
  }
}
