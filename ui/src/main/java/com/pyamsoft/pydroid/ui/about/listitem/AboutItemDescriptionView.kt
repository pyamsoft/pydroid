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
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isGone
import androidx.core.view.isVisible
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.Unbinder
import com.pyamsoft.pydroid.bootstrap.libraries.OssLibrary
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.R2
import com.pyamsoft.pydroid.ui.arch.UiView
import com.pyamsoft.pydroid.ui.arch.ViewEvent.EMPTY
import com.pyamsoft.pydroid.ui.arch.ViewEvent.EmptyPublisher

internal class AboutItemDescriptionView internal constructor(
  private val parent: ViewGroup
) : UiView<EMPTY>(EmptyPublisher), BaseAboutItem {

  private lateinit var unbinder: Unbinder
  @field:BindView(R2.id.about_library_description)
  internal lateinit var aboutLibraryDescription: TextView

  override fun id(): Int {
    return aboutLibraryDescription.id
  }

  override fun inflate(savedInstanceState: Bundle?) {
    val root = parent.inflateAndAdd(R.layout.about_item_description)
    unbinder = ButterKnife.bind(this, root)
  }

  override fun teardown() {
    unbinder.unbind()
  }

  override fun saveState(outState: Bundle) {
  }

  override fun bind(model: OssLibrary) {
    aboutLibraryDescription.text = model.description
    aboutLibraryDescription.isVisible = model.description.isNotBlank()
  }

  override fun unbind() {
    aboutLibraryDescription.text = ""
    aboutLibraryDescription.isGone = true
  }

}