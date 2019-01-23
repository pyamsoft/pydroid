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

package com.pyamsoft.pydroid.ui.rating.dialog

import android.content.ActivityNotFoundException
import androidx.annotation.CheckResult
import com.pyamsoft.pydroid.bootstrap.SchedulerProvider
import com.pyamsoft.pydroid.bootstrap.rating.RatingInteractor
import com.pyamsoft.pydroid.core.bus.EventBus
import com.pyamsoft.pydroid.ui.arch.Worker
import com.pyamsoft.pydroid.ui.rating.dialog.RatingDialogStateEvent.FailedMarketLink
import io.reactivex.disposables.Disposable

internal class RatingDialogWorker internal constructor(
  private val interactor: RatingInteractor,
  private val schedulerProvider: SchedulerProvider,
  bus: EventBus<RatingDialogStateEvent>
) : Worker<RatingDialogStateEvent>(bus) {

  @CheckResult
  fun saveRating(onSave: () -> Unit): Disposable {
    return interactor.saveRating()
        .subscribeOn(schedulerProvider.backgroundScheduler)
        .observeOn(schedulerProvider.foregroundScheduler)
        .subscribe { onSave() }
  }

  fun failedMarketLink(error: ActivityNotFoundException) {
    publish(FailedMarketLink(error))
  }

}
