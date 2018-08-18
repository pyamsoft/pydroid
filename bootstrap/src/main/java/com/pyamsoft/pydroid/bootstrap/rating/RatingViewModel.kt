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

import androidx.lifecycle.LifecycleOwner
import com.pyamsoft.pydroid.core.bus.EventBus
import com.pyamsoft.pydroid.core.viewmodel.DataWrapper
import com.pyamsoft.pydroid.core.viewmodel.LifecycleViewModel
import com.pyamsoft.pydroid.core.viewmodel.ViewModelBus
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class RatingViewModel internal constructor(
  private val ratingDialogBus: ViewModelBus<Unit>,
  private val ratingSavBus: ViewModelBus<Boolean>,
  private val currentVersion: Int,
  private val interactor: RatingInteractor,
  private val ratingErrorBus: EventBus<Throwable>
) : LifecycleViewModel {

  fun onRatingDialogLoaded(
    owner: LifecycleOwner,
    func: (DataWrapper<Unit>) -> Unit
  ) {
    ratingDialogBus.listen()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(func)
        .bind(owner)
  }

  fun onRatingSaved(
    owner: LifecycleOwner,
    func: (DataWrapper<Boolean>) -> Unit
  ) {
    ratingSavBus.listen()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(func)
        .bind(owner)
  }

  fun onRatingError(
    owner: LifecycleOwner,
    func: (Throwable) -> Unit
  ) {
    ratingErrorBus.listen()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(func)
        .bind(owner)
  }

  fun loadRatingDialog(
    owner: LifecycleOwner,
    force: Boolean
  ) {
    interactor.needsToViewRating(force, currentVersion)
        .filter { it }
        .map { Unit }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnSubscribe { ratingDialogBus.loading(force) }
        .doAfterTerminate { ratingDialogBus.complete() }
        .subscribe({ ratingDialogBus.success(it) }, {
          Timber.e(it, "on error loading rating dialog")
          ratingDialogBus.error(it)
        })
        .disposeOnClear(owner)
  }

  fun saveRating(
    owner: LifecycleOwner,
    accept: Boolean
  ) {
    interactor.saveRating(currentVersion)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnSubscribe { ratingSavBus.loading(false) }
        .doAfterTerminate { ratingSavBus.complete() }
        .subscribe({
          Timber.d("Saved current version code: %d", currentVersion)
          ratingSavBus.success(accept)
        }, {
          Timber.e(it, "Error saving rating dialog")
          ratingSavBus.error(it)
        })
        .disposeOnClear(owner)
  }

}
