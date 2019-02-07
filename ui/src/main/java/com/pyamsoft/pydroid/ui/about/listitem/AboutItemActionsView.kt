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

import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.pyamsoft.pydroid.bootstrap.libraries.OssLibrary
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.arch.BaseUiView
import com.pyamsoft.pydroid.ui.util.setOnDebouncedClickListener

internal class AboutItemActionsView internal constructor(
  parent: ViewGroup,
  callback: AboutItemActionsView.Callback
) : BaseUiView<AboutItemActionsView.Callback>(parent, callback), BaseAboutItem {

  private val layoutRoot by lazyView<View>(R.id.about_actions)
  private val viewLicense by lazyView<Button>(R.id.action_view_license)
  private val visitHomepage by lazyView<Button>(R.id.action_visit_homepage)

  override val layout: Int = R.layout.about_item_actions

  override fun id(): Int {
    return layoutRoot.id
  }

  override fun teardown() {
    unbind()
  }

  override fun bind(model: OssLibrary) {
    viewLicense.setOnDebouncedClickListener {
      callback.onViewLicenseClicked(model.name, model.licenseUrl)
    }

    visitHomepage.setOnDebouncedClickListener {
      callback.onVisitHomepageClicked(model.name, model.libraryUrl)
    }
  }

  override fun unbind() {
    viewLicense.setOnDebouncedClickListener(null)
    visitHomepage.setOnDebouncedClickListener(null)
  }

  interface Callback {

    fun onViewLicenseClicked(
      name: String,
      licenseUrl: String
    )

    fun onVisitHomepageClicked(
      name: String,
      homepageUrl: String
    )

  }

}
