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

package com.pyamsoft.pydroid.ui.about

import android.os.Bundle
import androidx.lifecycle.LifecycleOwner
import com.pyamsoft.pydroid.arch.BaseUiComponent
import com.pyamsoft.pydroid.arch.doOnDestroy
import com.pyamsoft.pydroid.bootstrap.libraries.OssLibrary
import com.pyamsoft.pydroid.ui.about.AboutUiComponent.Callback
import com.pyamsoft.pydroid.ui.widget.spinner.SpinnerView

internal class AboutUiComponentImpl internal constructor(
  private val listView: AboutListView,
  private val spinner: SpinnerView,
  private val toolbar: AboutToolbarView,
  private val presenter: AboutPresenter
) : BaseUiComponent<AboutUiComponent.Callback>(),
    AboutUiComponent,
    AboutPresenter.Callback {

  override fun onBind(
    owner: LifecycleOwner,
    savedInstanceState: Bundle?,
    callback: Callback
  ) {
    owner.doOnDestroy {
      toolbar.teardown()
      listView.teardown()
      spinner.teardown()
      presenter.unbind()
    }

    toolbar.inflate(savedInstanceState)
    listView.inflate(savedInstanceState)
    spinner.inflate(savedInstanceState)
    presenter.bind(this)
  }

  override fun saveState(outState: Bundle) {
    toolbar.saveState(outState)
    listView.saveState(outState)
    spinner.saveState(outState)
  }

  override fun onLicenseLoadBegin() {
    listView.hide()
    spinner.show()
  }

  override fun onLicensesLoaded(licenses: List<OssLibrary>) {
    listView.loadLicenses(licenses)
  }

  override fun onLicenseLoadError(throwable: Throwable) {
    listView.showError(throwable)
  }

  override fun onLicenseLoadComplete() {
    spinner.hide()
    listView.show()
  }

  override fun onNavigationEvent() {
    callback.close()
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
    callback.navigateToHomepage(name, homepageUrl)
  }

}