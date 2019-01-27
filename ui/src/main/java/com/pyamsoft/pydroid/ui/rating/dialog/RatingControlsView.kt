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
import com.pyamsoft.pydroid.core.bus.EventBus
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.arch.BaseUiView
import com.pyamsoft.pydroid.ui.rating.dialog.RatingDialogViewEvent.Cancel
import com.pyamsoft.pydroid.ui.rating.dialog.RatingDialogViewEvent.VisitMarket
import com.pyamsoft.pydroid.ui.util.setOnDebouncedClickListener

internal class RatingControlsView internal constructor(
  private val rateLink: String,
  parent: ViewGroup,
  uiBus: EventBus<RatingDialogViewEvent>
) : BaseUiView<RatingDialogViewEvent>(parent, uiBus) {

  private val layoutRoot by lazyView<View>(R.id.rating_control_root)
  private val rateApplication by lazyView<Button>(R.id.rate_application)
  private val noThanks by lazyView<Button>(R.id.no_thanks)

  override val layout: Int = R.layout.rating_controls

  override fun id(): Int {
    return layoutRoot.id
  }

  override fun onInflated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    setupButtons()
  }

  private fun setupButtons() {
    rateApplication.setOnDebouncedClickListener { publish(VisitMarket(rateLink)) }
    noThanks.setOnDebouncedClickListener { publish(Cancel) }
  }

  override fun teardown() {
    rateApplication.setOnClickListener(null)
    noThanks.setOnClickListener(null)
  }

}
