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

package com.pyamsoft.pydroid.ui.about.listitem

import com.pyamsoft.pydroid.arch.UiState
import com.pyamsoft.pydroid.arch.UiViewModel
import com.pyamsoft.pydroid.ui.about.listitem.AboutItemViewModel.AboutItemState

internal class AboutItemViewModel internal constructor(
  private val handler: AboutItemHandler
) : UiViewModel<AboutItemState>(
    initialState = AboutItemState(url = "")
), AboutItemActionsView.Callback {

  override fun onBind() {
    handler.handle(this)
        .destroy()
  }

  override fun onUnbind() {
  }

  override fun onViewLicenseClicked(
    name: String,
    licenseUrl: String
  ) {
    handleUrl(licenseUrl)
  }

  override fun onVisitHomepageClicked(
    name: String,
    homepageUrl: String
  ) {
    handleUrl(homepageUrl)
  }

  private fun handleUrl(url: String) {
    setUniqueState(url, old = { it.url }) { state, value -> state.copy(url = value) }
  }

  data class AboutItemState(val url: String) : UiState
}

