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
import com.pyamsoft.pydroid.core.singleDisposable
import com.pyamsoft.pydroid.core.tryDispose
import com.pyamsoft.pydroid.core.viewmodel.BaseViewModel
import com.pyamsoft.pydroid.core.viewmodel.DataBus
import com.pyamsoft.pydroid.core.viewmodel.DataWrapper
import io.reactivex.Scheduler
import timber.log.Timber

class RatingViewModel internal constructor(
  owner: LifecycleOwner,
  private val ratingDialogBus: DataBus<Unit>,
  private val ratingSaveBus: DataBus<Boolean>,
  private val currentVersion: Int,
  private val interactor: RatingInteractor,
  private val ratingErrorBus: EventBus<Throwable>,
  private val foregroundScheduler: Scheduler,
  private val backgroundScheduler: Scheduler
) : BaseViewModel(owner) {

  private var loadRatingDisposable by singleDisposable()
  private var saveRatingDisposable by singleDisposable()

  override fun onCleared() {
    super.onCleared()
    loadRatingDisposable.tryDispose()
    saveRatingDisposable.tryDispose()
  }

  fun onRatingDialogLoaded(func: (DataWrapper<Unit>) -> Unit) {
    dispose {
      ratingDialogBus.listen()
          .subscribeOn(backgroundScheduler)
          .observeOn(foregroundScheduler)
          .subscribe(func)
    }
  }

  fun onRatingSaved(func: (DataWrapper<Boolean>) -> Unit) {
    dispose {
      ratingSaveBus.listen()
          .subscribeOn(backgroundScheduler)
          .observeOn(foregroundScheduler)
          .subscribe(func)
    }
  }

  fun onRatingError(func: (Throwable) -> Unit) {
    dispose {
      ratingErrorBus.listen()
          .subscribeOn(backgroundScheduler)
          .observeOn(foregroundScheduler)
          .subscribe(func)
    }
  }

  fun loadRatingDialog(force: Boolean) {
    loadRatingDisposable = interactor.needsToViewRating(force, currentVersion)
        .filter { it }
        .map { Unit }
        .subscribeOn(backgroundScheduler)
        .observeOn(foregroundScheduler)
        .doOnSubscribe { ratingDialogBus.publishLoading(force) }
        .doAfterTerminate { ratingDialogBus.publishComplete() }
        .subscribe({ ratingDialogBus.publishSuccess(it) }, {
          Timber.e(it, "on error loading rating dialog")
          ratingDialogBus.publishError(it)
        })
  }

  fun saveRating(accept: Boolean) {
    saveRatingDisposable = interactor.saveRating(currentVersion)
        .subscribeOn(backgroundScheduler)
        .observeOn(foregroundScheduler)
        .doOnSubscribe { ratingSaveBus.publishLoading(false) }
        .doAfterTerminate { ratingSaveBus.publishComplete() }
        .subscribe({
          Timber.d("Saved current version code: %d", currentVersion)
          ratingSaveBus.publishSuccess(accept)
        }, {
          Timber.e(it, "Error saving rating dialog")
          ratingSaveBus.publishError(it)
        })
  }

}
