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

import com.pyamsoft.pydroid.ui.arch.Presenter

internal interface UrlPresenter : Presenter<UrlPresenter.Callback> {

  interface Callback {

    fun onWebviewBegin()

    fun onWebviewOtherPageLoaded(url: String)

    fun onWebviewTargetPageLoaded(url: String)

    fun onWebviewExternalNavigationEvent(url: String)

    fun onToolbarNavigateEvent()

    fun onToolbarMenuItemEvent(
      itemId: Int,
      url: String
    )
  }
}