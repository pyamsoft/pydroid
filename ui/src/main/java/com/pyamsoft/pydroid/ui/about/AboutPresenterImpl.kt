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

import com.pyamsoft.pydroid.arch.BasePresenter
import com.pyamsoft.pydroid.bootstrap.SchedulerProvider
import com.pyamsoft.pydroid.bootstrap.about.AboutInteractor
import com.pyamsoft.pydroid.core.bus.EventBus
import com.pyamsoft.pydroid.core.singleDisposable
import com.pyamsoft.pydroid.core.tryDispose
import com.pyamsoft.pydroid.ui.about.AboutPresenter.Callback
import com.pyamsoft.pydroid.ui.about.LicenseLoadState.Begin
import com.pyamsoft.pydroid.ui.about.LicenseLoadState.Complete
import com.pyamsoft.pydroid.ui.about.LicenseLoadState.Error
import com.pyamsoft.pydroid.ui.about.LicenseLoadState.Loaded
import timber.log.Timber

internal class AboutPresenterImpl internal constructor(
  private val interactor: AboutInteractor,
  private val schedulerProvider: SchedulerProvider,
  bus: EventBus<LicenseLoadState>
) : BasePresenter<LicenseLoadState, Callback>(bus),
    AboutToolbarView.Callback,
    AboutListView.Callback,
    AboutPresenter {

  private var licenseDisposable by singleDisposable()

  override fun onBind() {
    listen()
        .subscribeOn(schedulerProvider.backgroundScheduler)
        .observeOn(schedulerProvider.foregroundScheduler)
        .subscribe {
          return@subscribe when (it) {
            is Begin -> callback.onLicenseLoadBegin()
            is Loaded -> callback.onLicensesLoaded(it.licenses)
            is Error -> callback.onLicenseLoadError(it.error)
            is Complete -> callback.onLicenseLoadComplete()
          }
        }
        .destroy()

    loadLicenses(false)
  }

  override fun onUnbind() {
    licenseDisposable.tryDispose()
  }

  override fun onViewLicenseClicked(
    name: String,
    licenseUrl: String
  ) {
    callback.onViewLicense(name, licenseUrl)
  }

  override fun onVisitHomepageClicked(
    name: String,
    homepageUrl: String
  ) {
    callback.onVisitHomepage(name, homepageUrl)
  }

  override fun onToolbarNavClicked() {
    callback.onNavigationEvent()
  }

  private fun loadLicenses(force: Boolean) {
    licenseDisposable = interactor.loadLicenses(force)
        .subscribeOn(schedulerProvider.backgroundScheduler)
        .observeOn(schedulerProvider.foregroundScheduler)
        .doOnSubscribe { publish(Begin) }
        .doAfterTerminate { publish(Complete) }
        .subscribe({ publish(Loaded(it)) }, {
          Timber.e(it, "Error loading licenses")
          publish(Error(it))
        })
  }
}
