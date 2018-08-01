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

package com.pyamsoft.pydroid.bootstrap.rating

import com.pyamsoft.pydroid.bootstrap.rating.RatingPresenter.View
import com.pyamsoft.pydroid.core.bus.EventBus
import com.pyamsoft.pydroid.core.presenter.Presenter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class RatingPresenter internal constructor(
  private val currentVersion: Int,
  private val interactor: RatingInteractor,
  private val ratingErrorBus: EventBus<Throwable>
) : Presenter<View>() {

  override fun onCreate() {
    super.onCreate()

    dispose {
      ratingErrorBus.listen()
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe { view?.onRatingError(it) }
    }
  }

  fun loadRatingDialog(force: Boolean) {
    dispose {
      interactor.needsToViewRating(force, currentVersion)
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe({
            if (it) {
              view?.onShowRating()
            }
          }, {
            Timber.e(it, "on error loading rating dialog")
            view?.onShowRatingError(it)
          })
    }
  }

  interface View : RatingLoadCallback, RatingErrorCallback

  interface RatingErrorCallback {

    fun onRatingError(throwable: Throwable)

  }

  interface RatingLoadCallback {

    fun onShowRating()

    fun onShowRatingError(throwable: Throwable)
  }
}