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
import android.widget.Button
import android.widget.LinearLayout
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.Unbinder
import com.pyamsoft.pydroid.bootstrap.libraries.OssLibrary
import com.pyamsoft.pydroid.core.bus.Publisher
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.R2
import com.pyamsoft.pydroid.ui.about.AboutViewEvent
import com.pyamsoft.pydroid.ui.about.AboutViewEvent.ViewLicense
import com.pyamsoft.pydroid.ui.about.AboutViewEvent.VisitHomepage
import com.pyamsoft.pydroid.ui.arch.UiView
import com.pyamsoft.pydroid.ui.util.setOnDebouncedClickListener

internal class AboutItemActionsView internal constructor(
  private val parent: ViewGroup,
  bus: Publisher<AboutViewEvent>
) : UiView<AboutViewEvent>(bus), BaseAboutItem {

  private lateinit var unbinder: Unbinder
  @field:BindView(R2.id.layout_root) internal lateinit var layoutRoot: LinearLayout
  @field:BindView(R2.id.about_library_view_license) internal lateinit var viewLicense: Button
  @field:BindView(R2.id.about_library_visit_homepage) internal lateinit var visitHomepage: Button

  override fun id(): Int {
    return layoutRoot.id
  }

  override fun inflate(savedInstanceState: Bundle?) {
    val root = parent.inflateAndAdd(R.layout.about_item_actions)
    unbinder = ButterKnife.bind(this, root)
  }

  override fun teardown() {
    unbinder.unbind()
  }

  override fun saveState(outState: Bundle) {
  }

  override fun bind(model: OssLibrary) {
    viewLicense.setOnDebouncedClickListener {
      publish(ViewLicense(model.name, model.licenseUrl))
    }

    visitHomepage.setOnDebouncedClickListener {
      publish(VisitHomepage(model.name, model.libraryUrl))
    }
  }

  override fun unbind() {
    viewLicense.setOnDebouncedClickListener(null)
    visitHomepage.setOnDebouncedClickListener(null)
  }

}