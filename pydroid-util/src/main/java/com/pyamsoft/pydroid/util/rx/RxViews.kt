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

package com.pyamsoft.pydroid.util.rx

import android.support.annotation.CheckResult
import android.support.v4.widget.SwipeRefreshLayout
import android.view.View
import android.widget.CompoundButton
import android.widget.RadioGroup
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers

object RxViews : RxDebounce {

  override var enabled: Boolean = true

  @JvmStatic @JvmOverloads @CheckResult fun onClick(view: View,
      scheduler: Scheduler = AndroidSchedulers.mainThread()): Observable<View> {
    return Observable.create { emitter: ObservableEmitter<View> ->

      emitter.setCancellable {
        view.setOnClickListener(null)
      }

      view.setOnClickListener {
        debounce(it, emitter, it)
      }
    }.subscribeOn(scheduler)
  }

  @JvmStatic @JvmOverloads @CheckResult fun onCheckChanged(view: CompoundButton,
      scheduler: Scheduler = AndroidSchedulers.mainThread()): Observable<CheckedChangedEvent> {
    return Observable.create { emitter: ObservableEmitter<CheckedChangedEvent> ->

      emitter.setCancellable {
        view.setOnCheckedChangeListener(null)
      }

      view.setOnCheckedChangeListener { buttonView, isChecked ->
        debounce(buttonView, emitter, CheckedChangedEvent(buttonView, isChecked))
      }
    }.subscribeOn(scheduler)
  }

  @JvmOverloads @CheckResult fun onCheckChanged(group: RadioGroup,
      scheduler: Scheduler = AndroidSchedulers.mainThread()): Observable<GroupChangedEvent> {
    return Observable.create { emitter: ObservableEmitter<GroupChangedEvent> ->

      emitter.setCancellable {
        group.setOnCheckedChangeListener(null)
      }

      group.setOnCheckedChangeListener { grp, checkedId ->
        debounce(grp, emitter, GroupChangedEvent(group, checkedId))
      }
    }.subscribeOn(scheduler)
  }

  @JvmOverloads @CheckResult fun onRefreshed(view: SwipeRefreshLayout,
      scheduler: Scheduler = AndroidSchedulers.mainThread()): Observable<Unit> {
    return Observable.create { emitter: ObservableEmitter<Unit> ->

      emitter.setCancellable {
        view.setOnRefreshListener(null)
      }

      view.setOnRefreshListener {
        debounce(view, emitter, Unit)
      }

    }.subscribeOn(scheduler)
  }

  data class CheckedChangedEvent(val view: CompoundButton, val checked: Boolean)

  data class GroupChangedEvent(val group: RadioGroup, val checkedId: Int)
}

