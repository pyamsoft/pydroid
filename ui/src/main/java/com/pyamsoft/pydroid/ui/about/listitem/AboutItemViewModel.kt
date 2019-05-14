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

import com.pyamsoft.pydroid.arch.impl.BaseUiViewModel
import com.pyamsoft.pydroid.bootstrap.libraries.OssLibrary
import com.pyamsoft.pydroid.ui.about.listitem.AboutItemViewEvent.OpenUrl

internal class AboutItemViewModel internal constructor(
  library: OssLibrary
) : BaseUiViewModel<AboutItemState, AboutItemViewEvent, AboutItemControllerEvent>(
    initialState = AboutItemState(library = library)
) {

  override fun handleViewEvent(event: AboutItemViewEvent) {
    return when (event) {
      is OpenUrl -> publish(AboutItemControllerEvent.ExternalUrl(event.url))
    }
  }

}

