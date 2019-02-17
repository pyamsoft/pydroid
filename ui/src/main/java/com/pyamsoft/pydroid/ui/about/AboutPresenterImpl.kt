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

import androidx.lifecycle.LifecycleOwner
import com.pyamsoft.pydroid.bootstrap.SchedulerProvider
import com.pyamsoft.pydroid.bootstrap.about.AboutInteractor
import com.pyamsoft.pydroid.core.bus.EventBus
import com.pyamsoft.pydroid.core.singleDisposable
import com.pyamsoft.pydroid.core.tryDispose
import com.pyamsoft.pydroid.ui.about.LicenseLoadState.Begin
import com.pyamsoft.pydroid.ui.about.LicenseLoadState.Complete
import com.pyamsoft.pydroid.ui.about.LicenseLoadState.Error
import com.pyamsoft.pydroid.ui.about.LicenseLoadState.Loaded
import com.pyamsoft.pydroid.ui.arch.BasePresenter
import com.pyamsoft.pydroid.ui.arch.destroy
import timber.log.Timber

internal class AboutPresenterImpl internal constructor(
  private val interactor: AboutInteractor,
  private val schedulerProvider: SchedulerProvider,
  bus: EventBus<LicenseLoadState>
) : BasePresenter<LicenseLoadState, AboutPresenter.Callback>(bus),
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
        .destroy(owner)
  }

  override fun onUnbind() {
    licenseDisposable.tryDispose()
  }

  fun loadLicenses(force: Boolean) {
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
