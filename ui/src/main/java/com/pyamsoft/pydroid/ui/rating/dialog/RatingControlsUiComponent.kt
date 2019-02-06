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

import androidx.lifecycle.LifecycleOwner
import com.pyamsoft.pydroid.bootstrap.SchedulerProvider
import io.reactivex.ObservableTransformer

internal class RatingControlsUiComponent internal constructor(
  private val schedulerProvider: SchedulerProvider,
  view: RatingControlsView,
  owner: LifecycleOwner
) : BaseUiComponent<RatingDialogViewEvent, RatingControlsView>(view, owner) {

  override fun onUiEvent(): ObservableTransformer<in RatingDialogViewEvent, out RatingDialogViewEvent> {
    return ObservableTransformer {
      return@ObservableTransformer it
          .subscribeOn(schedulerProvider.backgroundScheduler)
          .observeOn(schedulerProvider.foregroundScheduler)
    }
  }

}
