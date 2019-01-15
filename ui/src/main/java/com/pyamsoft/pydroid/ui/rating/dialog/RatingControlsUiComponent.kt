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

import android.os.Bundle
import com.pyamsoft.pydroid.bootstrap.SchedulerProvider
import com.pyamsoft.pydroid.core.bus.Listener
import com.pyamsoft.pydroid.ui.arch.UiComponent
import io.reactivex.Observable

internal class RatingControlsUiComponent internal constructor(
  private val controlsView: RatingControlsView,
  private val uiBus: Listener<RatingDialogViewEvent>,
  private val schedulerProvider: SchedulerProvider
) : UiComponent<RatingDialogViewEvent> {

  override fun id(): Int {
    return controlsView.id()
  }

  override fun create(savedInstanceState: Bundle?) {
    controlsView.inflate(savedInstanceState)
  }

  override fun saveState(outState: Bundle) {
    controlsView.saveState(outState)
  }

  override fun onUiEvent(): Observable<RatingDialogViewEvent> {
    return uiBus.listen()
        .subscribeOn(schedulerProvider.backgroundScheduler)
        .observeOn(schedulerProvider.foregroundScheduler)
  }

}