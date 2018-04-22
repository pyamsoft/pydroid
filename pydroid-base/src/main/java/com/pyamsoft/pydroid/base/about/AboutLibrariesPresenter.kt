/*
 * Copyright (C) 2018 Peter Kenji Yamanaka
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pyamsoft.pydroid.base.about

import com.pyamsoft.pydroid.bus.EventBus
import com.pyamsoft.pydroid.presenter.Presenter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class AboutLibrariesPresenter internal constructor(
  private val interactor: AboutLibrariesInteractor,
  private val bus: EventBus<List<AboutLibrariesModel>>
) : Presenter<AboutLibrariesPresenter.View>() {

  override fun onCreate() {
    super.onCreate()

    dispose {
      bus.listen()
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe { view?.onLicenseLoaded(it) }
    }

    loadLicenses(false)
  }

  private fun loadLicenses(force: Boolean) {
    dispose {
      interactor.loadLicenses(force)
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .doOnSubscribe { view?.onLicenseLoadBegin() }
          .doAfterTerminate { view?.onLicenseLoadComplete() }
          .subscribe({ bus.publish(it) }, {
            Timber.e(it, "onError loading licenses")
            view?.onLicenseLoadError(it)
          })
    }
  }

  interface View : LoadCallback

  interface LoadCallback {

    fun onLicenseLoadBegin()

    fun onLicenseLoaded(licenses: List<AboutLibrariesModel>)

    fun onLicenseLoadError(throwable: Throwable)

    fun onLicenseLoadComplete()
  }
}
