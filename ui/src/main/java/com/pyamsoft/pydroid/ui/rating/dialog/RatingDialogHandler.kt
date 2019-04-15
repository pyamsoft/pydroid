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

import androidx.annotation.CheckResult
import com.pyamsoft.pydroid.arch.UiEventHandler
import com.pyamsoft.pydroid.bootstrap.SchedulerProvider
import com.pyamsoft.pydroid.core.bus.EventBus
import com.pyamsoft.pydroid.ui.rating.dialog.RatingDialogHandler.RatingEvent
import com.pyamsoft.pydroid.ui.rating.dialog.RatingDialogHandler.RatingEvent.Ignore
import com.pyamsoft.pydroid.ui.rating.dialog.RatingDialogHandler.RatingEvent.Rate
import io.reactivex.disposables.Disposable

internal class RatingDialogHandler internal constructor(
  private val schedulerProvider: SchedulerProvider,
  bus: EventBus<RatingEvent>
) : UiEventHandler<RatingEvent, RatingControlsView.Callback>(bus),
    RatingControlsView.Callback {

  override fun onNotRatingApplication() {
    publish(Ignore)
  }

  override fun onRateApplicationClicked(link: String) {
    publish(Rate(link))
  }

  @CheckResult
  override fun handle(delegate: RatingControlsView.Callback): Disposable {
    return listen()
        .subscribeOn(schedulerProvider.backgroundScheduler)
        .observeOn(schedulerProvider.foregroundScheduler)
        .subscribe {
          return@subscribe when (it) {
            is Rate -> delegate.onRateApplicationClicked(it.link)
            is Ignore -> delegate.onNotRatingApplication()
          }
        }
  }

  sealed class RatingEvent {
    data class Rate(val link: String) : RatingEvent()
    object Ignore : RatingEvent()
  }

}
