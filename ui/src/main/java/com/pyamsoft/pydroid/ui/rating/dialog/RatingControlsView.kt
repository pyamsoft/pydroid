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

package com.pyamsoft.pydroid.ui.rating.dialog

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.lifecycle.LifecycleOwner
import com.pyamsoft.pydroid.arch.BaseUiView
import com.pyamsoft.pydroid.arch.UiSavedState
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.rating.dialog.RatingDialogViewEvent.Cancel
import com.pyamsoft.pydroid.ui.rating.dialog.RatingDialogViewEvent.Rate
import com.pyamsoft.pydroid.ui.util.Snackbreak
import com.pyamsoft.pydroid.ui.util.setOnDebouncedClickListener

internal class RatingControlsView internal constructor(
  private val rateLink: String,
  private val owner: LifecycleOwner,
  parent: ViewGroup
) : BaseUiView<RatingDialogViewState, RatingDialogViewEvent>(parent) {

  private val rateApplication by boundView<Button>(R.id.rate_application)
  private val noThanks by boundView<Button>(R.id.no_thanks)

  override val layout: Int = R.layout.rating_controls

  override val layoutRoot by boundView<View>(R.id.rating_control_root)

  override fun onInflated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    rateApplication.setOnDebouncedClickListener { publish(Rate(rateLink)) }
    noThanks.setOnDebouncedClickListener { publish(Cancel) }
  }

  override fun onRender(
    state: RatingDialogViewState,
    savedState: UiSavedState
  ) {

    state.throwable.let { throwable ->
      if (throwable == null) {
        clearError()
      } else {
        showError(throwable)
      }
    }
  }

  private fun showError(error: Throwable) {
    Snackbreak.bindTo(owner)
        .short(layoutRoot, error.message ?: "An unexpected error occurred.")
  }

  private fun clearError() {
    Snackbreak.bindTo(owner)
        .dismiss()
  }

  override fun onTeardown() {
    rateApplication.setOnClickListener(null)
    noThanks.setOnClickListener(null)
    clearError()
  }

}
