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

package com.pyamsoft.pydroid.rx

import android.support.annotation.CheckResult
import android.support.v7.preference.Preference
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers

object RxPreferences {

  @JvmStatic @JvmOverloads @CheckResult fun onClick(preference: android.support.v7.preference.Preference,
      returnCondition: () -> Boolean = { true },
      scheduler: Scheduler = AndroidSchedulers.mainThread()): Observable<Preference> {
    return Observable.create { emitter: ObservableEmitter<Preference> ->

      emitter.setCancellable {
        preference.onPreferenceClickListener = null
      }

      preference.setOnPreferenceClickListener {
        if (!emitter.isDisposed) {
          emitter.onNext(it)
          return@setOnPreferenceClickListener returnCondition()
        } else {
          return@setOnPreferenceClickListener false
        }
      }
    }.subscribeOn(scheduler)
  }

  @JvmStatic @JvmOverloads @CheckResult fun <T : Any> onPreferenceChanged(preference: android.support.v7.preference.Preference,
      returnCondition: () -> Boolean = { true },
      scheduler: Scheduler = AndroidSchedulers.mainThread()): Observable<PreferenceChangedEvent<T>> {
    return Observable.create { emitter: ObservableEmitter<PreferenceChangedEvent<T>> ->

      emitter.setCancellable {
        preference.onPreferenceChangeListener = null
      }

      preference.setOnPreferenceChangeListener { preference, any ->
        if (!emitter.isDisposed) {
          @Suppress("UNCHECKED_CAST") emitter.onNext(PreferenceChangedEvent(preference, any as T))
          return@setOnPreferenceChangeListener returnCondition()
        } else {
          return@setOnPreferenceChangeListener false
        }
      }
    }.subscribeOn(scheduler)
  }

  data class PreferenceChangedEvent<out T : Any>(val preference: android.support.v7.preference.Preference, val value: T)

}

