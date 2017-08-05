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

import android.support.v7.preference.Preference
import io.reactivex.Scheduler

internal interface PreferencePresenterContract {

  fun clickEvent(preference: Preference, func: (Preference) -> Unit)

  fun clickEvent(preference: Preference, func: (Preference) -> Unit, returnCondition: () -> Boolean)

  fun clickEvent(preference: Preference, func: (Preference) -> Unit, returnCondition: () -> Boolean,
      scheduler: Scheduler)

  fun <T : Any> preferenceChangedEvent(preference: Preference, func: (Preference, T) -> Unit)

  fun <T : Any> preferenceChangedEvent(preference: Preference, func: (Preference, T) -> Unit,
      returnCondition: () -> Boolean)

  fun <T : Any> preferenceChangedEvent(preference: Preference, func: (Preference, T) -> Unit,
      returnCondition: () -> Boolean, scheduler: Scheduler)
}

