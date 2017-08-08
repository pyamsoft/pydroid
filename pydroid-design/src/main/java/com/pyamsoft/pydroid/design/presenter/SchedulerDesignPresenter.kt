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

package com.pyamsoft.pydroid.design.presenter

import android.support.design.widget.BottomNavigationView
import android.view.MenuItem
import com.pyamsoft.pydroid.presenter.SchedulerPresenter
import io.reactivex.Scheduler

abstract class SchedulerDesignPresenter<in T : Any>(foregroundScheduler: Scheduler,
    backgroundScheduler: Scheduler) : SchedulerPresenter<T>(foregroundScheduler,
    backgroundScheduler), DesignPresenterContract {

  private val delegate: DesignPresenter<T> = DelegateDesignPresenter()

  override fun onStop() {
    super.onStop()
    delegate.stop()
  }

  final override fun clickBottomNavigation(bottomBar: BottomNavigationView,
      func: (MenuItem) -> Unit) {
    clickBottomNavigation(bottomBar, func, { true })
  }

  final override fun clickBottomNavigation(bottomBar: BottomNavigationView,
      func: (MenuItem) -> Unit, condition: (MenuItem) -> Boolean) {
    clickBottomNavigation(bottomBar, func, condition, foregroundScheduler)
  }

  final override fun clickBottomNavigation(bottomBar: BottomNavigationView,
      func: (MenuItem) -> Unit, condition: (MenuItem) -> Boolean, scheduler: Scheduler) {
    delegate.clickBottomNavigation(bottomBar, func, condition, scheduler)
  }

  private class DelegateDesignPresenter<in T : Any> : DesignPresenter<T>()

}

