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

package com.pyamsoft.pydroid.design.rx

import android.support.annotation.CheckResult
import android.support.design.widget.BottomNavigationView
import android.view.MenuItem
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers

object RxDesign {

  @JvmOverloads @CheckResult fun onClickBottomNavigation(bottomBar: BottomNavigationView,
      condition: (MenuItem) -> Boolean = { true },
      scheduler: Scheduler = AndroidSchedulers.mainThread()): Observable<MenuItem> {
    return Observable.create<MenuItem> { emitter: ObservableEmitter<MenuItem> ->

      emitter.setCancellable {
        bottomBar.setOnNavigationItemSelectedListener(null)
      }

      bottomBar.setOnNavigationItemSelectedListener {
        val active = !emitter.isDisposed
        if (active) {
          emitter.onNext(it)
        }

        return@setOnNavigationItemSelectedListener condition(it)
      }

    }.subscribeOn(scheduler)
  }
}

