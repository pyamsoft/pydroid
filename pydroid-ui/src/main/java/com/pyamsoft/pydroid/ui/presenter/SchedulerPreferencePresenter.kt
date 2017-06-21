/*
 * Copyright 2017 Peter Kenji Yamanaka
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
 */

package com.pyamsoft.pydroid.ui.presenter

import android.support.annotation.CallSuper
import android.support.v7.preference.Preference
import com.pyamsoft.pydroid.presenter.SchedulerPresenter
import io.reactivex.Scheduler

abstract class SchedulerPreferencePresenter(foregroundScheduler: Scheduler,
    backgroundScheduler: Scheduler) : SchedulerPresenter(foregroundScheduler,
    backgroundScheduler), PreferencePresenterContract {

  private val delegate = DelegatePreferencePresenter()

  override final fun clickEvent(preference: Preference, func: (Preference) -> Unit) {
    delegate.clickEvent(preference, func)
  }

  override final fun <T : Any> preferenceChangedEvent(preference: Preference,
      func: (Preference, T) -> Unit) {
    delegate.preferenceChangedEvent(preference, func)
  }

  @CallSuper override fun onStop() {
    super.onStop()
    delegate.stop()
  }

  @CallSuper override fun onDestroy() {
    super.onDestroy()
    delegate.destroy()
  }

  private class DelegatePreferencePresenter : PreferencePresenter()

}

