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

package com.pyamsoft.pydroid.util.presenter

import android.support.annotation.CallSuper
import android.support.v4.widget.SwipeRefreshLayout
import android.view.View
import android.widget.CompoundButton
import android.widget.RadioGroup
import com.pyamsoft.pydroid.presenter.SchedulerPresenter
import io.reactivex.Scheduler

abstract class SchedulerViewPresenter(foregroundScheduler: Scheduler,
    backgroundScheduler: Scheduler) : SchedulerPresenter(foregroundScheduler,
    backgroundScheduler), ViewPresenterContract {

  private val delegate = DelegateViewPresenter()

  final override fun clickEvent(view: View, func: (View) -> Unit) {
    clickEvent(view, func, foregroundScheduler)
  }

  final override fun clickEvent(view: View, func: (View) -> Unit, scheduler: Scheduler) {
    delegate.clickEvent(view, func, scheduler)
  }

  final override fun checkChangedEvent(view: CompoundButton,
      func: (CompoundButton, Boolean) -> Unit) {
    checkChangedEvent(view, func, foregroundScheduler)
  }

  final override fun checkChangedEvent(view: CompoundButton,
      func: (CompoundButton, Boolean) -> Unit, scheduler: Scheduler) {
    delegate.checkChangedEvent(view, func, scheduler)
  }

  final override fun checkChangedEvent(view: RadioGroup, func: (RadioGroup, Int) -> Unit) {
    checkChangedEvent(view, func, foregroundScheduler)
  }

  final override fun checkChangedEvent(view: RadioGroup, func: (RadioGroup, Int) -> Unit,
      scheduler: Scheduler) {
    delegate.checkChangedEvent(view, func, scheduler)
  }

  final override fun swipeRefresh(view: SwipeRefreshLayout, func: () -> Unit) {
    swipeRefresh(view, func, foregroundScheduler)
  }

  final override fun swipeRefresh(view: SwipeRefreshLayout, func: () -> Unit,
      scheduler: Scheduler) {
    delegate.swipeRefresh(view, func, scheduler)
  }

  @CallSuper override fun onStop() {
    super.onStop()
    delegate.stop()
  }

  @CallSuper override fun onDestroy() {
    super.onDestroy()
    delegate.destroy()
  }

  private class DelegateViewPresenter : ViewPresenter()

}

