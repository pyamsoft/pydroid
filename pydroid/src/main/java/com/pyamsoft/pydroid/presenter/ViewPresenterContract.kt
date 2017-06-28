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

package com.pyamsoft.pydroid.presenter

import android.support.v4.widget.SwipeRefreshLayout
import android.view.View
import android.widget.CompoundButton
import android.widget.RadioGroup
import io.reactivex.Scheduler

internal interface ViewPresenterContract {

  fun clickEvent(view: View, func: (View) -> Unit)

  fun clickEvent(view: View, func: (View) -> Unit, scheduler: Scheduler)

  fun checkChangedEvent(view: CompoundButton, func: (CompoundButton, Boolean) -> Unit)

  fun checkChangedEvent(view: CompoundButton, func: (CompoundButton, Boolean) -> Unit,
      scheduler: Scheduler)

  fun checkChangedEvent(view: RadioGroup, func: (RadioGroup, Int) -> Unit)

  fun checkChangedEvent(view: RadioGroup, func: (RadioGroup, Int) -> Unit, scheduler: Scheduler)

  fun swipeRefresh(view: SwipeRefreshLayout, func: () -> Unit)

  fun swipeRefresh(view: SwipeRefreshLayout, func: () -> Unit, scheduler: Scheduler)
}

