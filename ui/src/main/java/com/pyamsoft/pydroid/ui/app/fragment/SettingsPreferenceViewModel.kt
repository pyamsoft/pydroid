/*
 * Copyright (C) 2018 Peter Kenji Yamanaka
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pyamsoft.pydroid.ui.app.fragment

import android.content.ActivityNotFoundException
import androidx.lifecycle.LifecycleOwner
import com.pyamsoft.pydroid.core.bus.EventBus
import com.pyamsoft.pydroid.core.viewmodel.BaseViewModel
import io.reactivex.Scheduler

class SettingsPreferenceViewModel internal constructor(
  owner: LifecycleOwner,
  private val linkerErrorBus: EventBus<ActivityNotFoundException>,
  private val foregroundScheduler: Scheduler,
  private val backgroundScheduler: Scheduler
) : BaseViewModel(owner) {

  fun onLinkerError(func: (Throwable) -> Unit) {
    dispose {
      linkerErrorBus.listen()
          .subscribeOn(backgroundScheduler)
          .observeOn(foregroundScheduler)
          .subscribe(func)
    }
  }
}
