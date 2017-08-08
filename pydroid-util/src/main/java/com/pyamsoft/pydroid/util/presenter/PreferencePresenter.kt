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

import android.support.v7.preference.Preference
import com.pyamsoft.pydroid.presenter.Presenter
import com.pyamsoft.pydroid.util.rx.RxPreferences
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers

abstract class PreferencePresenter<in T : Any> : Presenter<T>(), PreferencePresenterContract {

  final override fun clickEvent(preference: Preference, func: (Preference) -> Unit) {
    clickEvent(preference, func) { true }
  }

  final override fun clickEvent(preference: Preference, func: (Preference) -> Unit,
      returnCondition: () -> Boolean) {
    clickEvent(preference, func, returnCondition, AndroidSchedulers.mainThread())
  }

  final override fun clickEvent(preference: Preference, func: (Preference) -> Unit,
      returnCondition: () -> Boolean, scheduler: Scheduler) {
    disposeOnStop {
      RxPreferences.onClick(preference, returnCondition, scheduler).subscribe {
        func(it)
      }
    }
  }

  final override fun <T : Any> preferenceChangedEvent(preference: Preference,
      func: (Preference, T) -> Unit) {
    preferenceChangedEvent(preference, func) { true }
  }

  final override fun <T : Any> preferenceChangedEvent(preference: Preference,
      func: (Preference, T) -> Unit, returnCondition: () -> Boolean) {
    preferenceChangedEvent(preference, func, returnCondition, AndroidSchedulers.mainThread())
  }

  final override fun <T : Any> preferenceChangedEvent(preference: Preference,
      func: (Preference, T) -> Unit, returnCondition: () -> Boolean, scheduler: Scheduler) {
    disposeOnStop {
      RxPreferences.onPreferenceChanged<T>(preference, returnCondition, scheduler).subscribe {
        func(it.preference, it.value)
      }
    }
  }
}

