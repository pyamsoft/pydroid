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

package com.pyamsoft.pydroid.ui.about.dialog

import com.pyamsoft.pydroid.arch.UiBinder

internal class UrlToolbarBinder internal constructor(
) : UiBinder<UrlToolbarBinder.Callback>(),
    UrlToolbarView.Callback {

  override fun onBind() {
  }

  override fun onUnbind() {
  }

  override fun onViewLicenseExternal(url: String) {
    callback.handleViewLicenseExternal(url)
  }

  override fun onToolbarNavClicked() {
    callback.handleNavigationEvent()
  }

  interface Callback : UiBinder.Callback {

    fun handleNavigationEvent()

    fun handleViewLicenseExternal(url: String)

  }
}