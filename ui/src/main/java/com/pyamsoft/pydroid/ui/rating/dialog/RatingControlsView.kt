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
import com.pyamsoft.pydroid.arch.BaseUiView
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.rating.dialog.RatingControlsView.Callback
import com.pyamsoft.pydroid.ui.util.setOnDebouncedClickListener

internal class RatingControlsView internal constructor(
  private val rateLink: String,
  parent: ViewGroup,
  callback: Callback
) : BaseUiView<Callback>(parent, callback) {

  private val rateApplication by lazyView<Button>(R.id.rate_application)
  private val noThanks by lazyView<Button>(R.id.no_thanks)

  override val layout: Int = R.layout.rating_controls

  override val layoutRoot by lazyView<View>(R.id.rating_control_root)

  override fun onInflated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    rateApplication.setOnDebouncedClickListener { callback.onRateApplicationClicked(rateLink) }
    noThanks.setOnDebouncedClickListener { callback.onNotRatingApplication() }
  }

  override fun onTeardown() {
    rateApplication.setOnClickListener(null)
    noThanks.setOnClickListener(null)
  }

  interface Callback {

    fun onRateApplicationClicked(link: String)

    fun onNotRatingApplication()

  }

}
