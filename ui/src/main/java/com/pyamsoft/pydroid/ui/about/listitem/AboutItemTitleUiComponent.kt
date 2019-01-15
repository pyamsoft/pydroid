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
import com.pyamsoft.pydroid.bootstrap.libraries.OssLibrary
import com.pyamsoft.pydroid.ui.arch.UiComponent
import com.pyamsoft.pydroid.ui.arch.ViewEvent.EMPTY
import io.reactivex.Observable

internal class AboutItemTitleUiComponent internal constructor(
  private val aboutTitleView: AboutItemTitleView
) : UiComponent<EMPTY>, BaseAboutItem {

  override fun id(): Int {
    return aboutTitleView.id()
  }

  override fun create(savedInstanceState: Bundle?) {
    aboutTitleView.inflate(savedInstanceState)
  }

  override fun saveState(outState: Bundle) {
  }

  override fun onUiEvent(): Observable<EMPTY> {
    return Observable.empty()
  }

  override fun bind(model: OssLibrary) {
    aboutTitleView.bind(model)
  }

  override fun unbind() {
    aboutTitleView.unbind()
  }

}