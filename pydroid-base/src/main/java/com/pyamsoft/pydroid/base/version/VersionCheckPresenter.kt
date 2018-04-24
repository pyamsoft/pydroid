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

package com.pyamsoft.pydroid.base.version

import com.pyamsoft.pydroid.bus.EventBus
import com.pyamsoft.pydroid.presenter.Presenter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import timber.log.Timber

class VersionCheckPresenter internal constructor(
  private val packageName: String,
  private val currentVersionCode: Int,
  private val interactor: VersionCheckInteractor,
  private val bus: EventBus<Int>
) : Presenter<VersionCheckPresenter.View>() {

  override fun onCreate() {
    super.onCreate()
    dispose {
      bus.listen()
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe { view?.onUpdatedVersionFound(currentVersionCode, it) }
    }
  }

  fun checkForUpdates(force: Boolean) {
    dispose {
      interactor.checkVersion(force, packageName)
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .doOnSuccess {
            Timber.i("Update check finished")
            Timber.i("Current version: %d", currentVersionCode)
            Timber.i("Latest version: %d", it)
          }
          .subscribe({
            if (currentVersionCode < it) {
              bus.publish(it)
            }
          }, {
            if (it is HttpException) {
              Timber.e(it, "Network Failure: %d", it.code())
            } else {
              Timber.e(it, "onError")
            }
          })
    }
  }

  interface View : UpdateCallback

  interface UpdateCallback {

    fun onUpdatedVersionFound(
      current: Int,
      updated: Int
    )
  }
}
