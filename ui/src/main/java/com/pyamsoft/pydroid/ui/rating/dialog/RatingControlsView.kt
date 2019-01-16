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
import android.view.ViewGroup
import android.widget.Button
import com.pyamsoft.pydroid.core.bus.Publisher
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.arch.UiView
import com.pyamsoft.pydroid.ui.rating.dialog.RatingDialogViewEvent.Cancel
import com.pyamsoft.pydroid.ui.rating.dialog.RatingDialogViewEvent.VisitMarket
import com.pyamsoft.pydroid.ui.util.setOnDebouncedClickListener

internal class RatingControlsView internal constructor(
  private val parent: ViewGroup,
  private val rateLink: String,
  uiBus: Publisher<RatingDialogViewEvent>
) : UiView<RatingDialogViewEvent>(uiBus) {

  private lateinit var layoutRoot: ViewGroup
  private lateinit var rateApplication: Button
  private lateinit var noThanks: Button

  override fun id(): Int {
    return layoutRoot.id
  }

  override fun inflate(savedInstanceState: Bundle?) {
    parent.inflateAndAdd(R.layout.rating_controls) {
      layoutRoot = findViewById(R.id.rating_control_root)
      rateApplication = findViewById(R.id.rate_application)
      noThanks = findViewById(R.id.no_thanks)
    }

    setupButtons()
  }

  override fun teardown() {
    rateApplication.setOnClickListener(null)
    noThanks.setOnClickListener(null)
  }

  private fun setupButtons() {
    rateApplication.setOnDebouncedClickListener { publish(VisitMarket(rateLink)) }
    noThanks.setOnDebouncedClickListener { publish(Cancel) }
  }

  override fun saveState(outState: Bundle) {
  }

}