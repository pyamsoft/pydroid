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

import android.os.Bundle
import android.view.View
import com.pyamsoft.pydroid.bootstrap.libraries.OssLibrary
import com.pyamsoft.pydroid.ui.arch.UiComponent
import io.reactivex.Observable

internal class AboutItemUiComponent internal constructor(
  private val aboutTitleView: AboutItemTitleView,
  private val aboutActionsView: AboutItemActionsView,
  private val aboutDescriptionView: AboutItemDescriptionView
) : UiComponent<Unit>, BaseAboutItem {

  override fun id(): Int {
    return View.NO_ID
  }

  override fun create(savedInstanceState: Bundle?) {
    aboutTitleView.inflate(savedInstanceState)
    aboutActionsView.inflate(savedInstanceState)
    aboutDescriptionView.inflate(savedInstanceState)
  }

  override fun saveState(outState: Bundle) {
  }

  override fun onUiEvent(): Observable<Unit> {
    return Observable.empty()
  }

  override fun bind(model: OssLibrary) {
    aboutTitleView.bind(model)
    aboutActionsView.bind(model)
    aboutDescriptionView.bind(model)
  }

  override fun unbind() {
    aboutTitleView.unbind()
    aboutActionsView.unbind()
    aboutDescriptionView.unbind()
  }

}