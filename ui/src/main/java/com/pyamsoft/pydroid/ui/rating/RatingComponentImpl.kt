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

package com.pyamsoft.pydroid.ui.rating

import android.view.View
import androidx.lifecycle.LifecycleOwner
import com.pyamsoft.pydroid.bootstrap.SchedulerProvider
import com.pyamsoft.pydroid.bootstrap.rating.RatingInteractor
import com.pyamsoft.pydroid.core.bus.EventBus

internal class RatingComponentImpl internal constructor(
  private val owner: LifecycleOwner,
  private val view: View,
  private val bus: EventBus<RatingStateEvent>,
  private val dialogBus: EventBus<RatingDialogStateEvent>,
  private val interactor: RatingInteractor,
  private val schedulerProvider: SchedulerProvider
) : RatingComponent {

  override fun inject(activity: RatingActivity) {
    val ratingView = RatingView(view, owner)
    activity.ratingUiComponent = RatingUiComponent(dialogBus, schedulerProvider, ratingView, owner)
    activity.ratingPresenter = RatingPresenter(interactor, schedulerProvider, bus)
  }

}
