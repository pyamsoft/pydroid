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
import androidx.lifecycle.LifecycleOwner
import com.pyamsoft.pydroid.arch.BaseUiComponent
import com.pyamsoft.pydroid.arch.doOnDestroy
import com.pyamsoft.pydroid.bootstrap.libraries.OssLibrary
import com.pyamsoft.pydroid.ui.about.listitem.AboutViewHolderUiComponent.Callback

internal class AboutViewHolderUiComponentImpl internal constructor(
  private val titleView: AboutItemTitleView,
  private val actionsView: AboutItemActionsView,
  private val descriptionView: AboutItemDescriptionView,
  private val presenter: AboutItemPresenter
) : BaseUiComponent<AboutViewHolderUiComponent.Callback>(),
    AboutViewHolderUiComponent,
    AboutItemPresenter.Callback {

  override fun onBind(
    owner: LifecycleOwner,
    savedInstanceState: Bundle?,
    callback: Callback
  ) {
    titleView.inflate(savedInstanceState)
    actionsView.inflate(savedInstanceState)
    descriptionView.inflate(savedInstanceState)

    // Do not call teardown, this lifecycle is controlled by bind() unbind()
  }

  override fun bind(
    owner: LifecycleOwner,
    model: OssLibrary
  ) {
    owner.doOnDestroy {
      titleView.unbind()
      actionsView.unbind()
      descriptionView.unbind()
      presenter.unbind()
    }

    titleView.bind(model)
    actionsView.bind(model)
    descriptionView.bind(model)
    presenter.bind(this)
  }

  override fun onViewLicense(
    name: String,
    licenseUrl: String
  ) {
    callback.showLicense(name, licenseUrl)
  }

  override fun onVisitHomepage(
    name: String,
    homepageUrl: String
  ) {
    callback.showHomepage(name, homepageUrl)
  }

  override fun saveState(outState: Bundle) {
    titleView.saveState(outState)
    actionsView.saveState(outState)
    descriptionView.saveState(outState)
  }

}