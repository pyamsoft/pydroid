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

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CheckResult
import androidx.lifecycle.LifecycleOwner
import com.pyamsoft.pydroid.bootstrap.libraries.OssLibrary
import com.pyamsoft.pydroid.ui.PYDroid
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.navigation.FailedNavigationPresenter

internal class AboutViewHolder private constructor(
  owner: LifecycleOwner,
  view: View,
  private val callback: AboutItemPresenter.Callback
) : BaseViewHolder(view), AboutItemPresenter.Callback {

  internal lateinit var titleView: AboutItemTitleView
  internal lateinit var actionsView: AboutItemActionsView
  internal lateinit var descriptionView: AboutItemDescriptionView
  internal lateinit var presenter: AboutItemPresenter
  internal lateinit var failedNavigationPresenter: FailedNavigationPresenter

  init {
    val root: ViewGroup = view.findViewById(R.id.about_listitem_root)
    PYDroid.obtain(itemView.context.applicationContext)
        .plusAboutItemComponent(owner, root)
        .inject(this)

    titleView.inflate(null)
    actionsView.inflate(null)
    descriptionView.inflate(null)

    presenter.bind(this)
  }

  override fun bind(model: OssLibrary) {
    titleView.bind(model)
    actionsView.bind(model)
    descriptionView.bind(model)
  }

  override fun unbind() {
    titleView.unbind()
    actionsView.unbind()
    descriptionView.unbind()
  }

  override fun onViewLicense(
    name: String,
    licenseUrl: String
  ) {
    callback.onViewLicense(name, licenseUrl)
  }

  override fun onVisitHomepage(
    name: String,
    homepageUrl: String
  ) {
    callback.onVisitHomepage(name, homepageUrl)
  }

  companion object {

    @CheckResult
    @JvmStatic
    fun create(
      callback: AboutItemPresenter.Callback,
      owner: LifecycleOwner,
      inflater: LayoutInflater,
      container: ViewGroup
    ): AboutViewHolder {
      val view = inflater.inflate(R.layout.adapter_item_about_license, container, false)
      return AboutViewHolder(owner, view, callback)
    }
  }

}
