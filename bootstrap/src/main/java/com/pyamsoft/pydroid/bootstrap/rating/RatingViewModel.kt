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

package com.pyamsoft.pydroid.bootstrap.rating

import androidx.annotation.CheckResult
import com.pyamsoft.pydroid.core.bus.EventBus
import io.reactivex.Scheduler
import io.reactivex.disposables.Disposable
import timber.log.Timber

class RatingViewModel internal constructor(
  private val interactor: RatingInteractor,
  private val currentVersion: Int,
  private val showRatingBus: EventBus<RatingEvents.ShowEvent>,
  private val showErrorRatingBus: EventBus<RatingEvents.ShowErrorEvent>,
  private val saveRatingErrorBus: EventBus<RatingEvents.SaveErrorEvent>,
  private val foregroundScheduler: Scheduler,
  private val backgroundScheduler: Scheduler
) {

  @CheckResult
  fun onShowRatingDialog(onShow: () -> Unit): Disposable {
    return showRatingBus.listen()
        .subscribeOn(backgroundScheduler)
        .observeOn(foregroundScheduler)
        .subscribe { onShow() }
  }

  @CheckResult
  fun onShowErrorRatingDialog(onError: (error: Throwable) -> Unit): Disposable {
    return showErrorRatingBus.listen()
        .subscribeOn(backgroundScheduler)
        .observeOn(foregroundScheduler)
        .subscribe { onError(it.error) }
  }

  fun publishShowRatingDialog() {
    showRatingBus.publish(RatingEvents.ShowEvent)
  }

  fun publishShowErrorRatingDialog(error: Throwable) {
    showErrorRatingBus.publish(RatingEvents.ShowErrorEvent(error))
  }

  fun publishSaveRatingError(error: Throwable) {
    saveRatingErrorBus.publish(RatingEvents.SaveErrorEvent(error))
  }

  @CheckResult
  fun loadRatingDialog(
    force: Boolean,
    onLoadBegin: (forced: Boolean) -> Unit,
    onLoadSuccess: () -> Unit,
    onLoadError: (error: Throwable) -> Unit,
    onLoadComplete: () -> Unit
  ): Disposable {
    return interactor.needsToViewRating(force, currentVersion)
        .filter { it }
        .map { Unit }
        .subscribeOn(backgroundScheduler)
        .observeOn(foregroundScheduler)
        .doOnSubscribe { onLoadBegin(force) }
        .doAfterTerminate { onLoadComplete() }
        .subscribe({ onLoadSuccess() }, {
          Timber.e(it, "Error loading rating dialog")
          onLoadError(it)
        })
  }

  @CheckResult
  fun saveRating(
    onSaveBegin: () -> Unit,
    onSaveSuccess: () -> Unit,
    onSaveError: (error: Throwable) -> Unit,
    onSaveComplete: () -> Unit
  ): Disposable {
    return interactor.saveRating(currentVersion)
        .subscribeOn(backgroundScheduler)
        .observeOn(foregroundScheduler)
        .doOnSubscribe { onSaveBegin() }
        .doAfterTerminate { onSaveComplete() }
        .subscribe({
          Timber.d("Saved current version code: %d", currentVersion)
          onSaveSuccess()
        }, {
          Timber.e(it, "Error saving rating dialog")
          onSaveError(it)
        })
  }

}
