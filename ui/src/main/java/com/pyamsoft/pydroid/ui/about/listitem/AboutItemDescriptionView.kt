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

import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.pyamsoft.pydroid.bootstrap.libraries.OssLibrary
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.arch.BaseUiView

internal class AboutItemDescriptionView internal constructor(
  parent: ViewGroup
) : BaseUiView<Unit>(parent, Unit), BaseAboutItem {

  private val description by lazyView<TextView>(R.id.about_description)

  override val layout: Int = R.layout.about_item_description

  override fun id(): Int {
    return description.id
  }

  override fun teardown() {
    unbind()
  }

  override fun bind(model: OssLibrary) {
    description.apply {
      text = model.description
      isVisible = model.description.isNotBlank()
    }
  }

  override fun unbind() {
    description.apply {
      text = ""
      isGone = true
    }
  }

}
