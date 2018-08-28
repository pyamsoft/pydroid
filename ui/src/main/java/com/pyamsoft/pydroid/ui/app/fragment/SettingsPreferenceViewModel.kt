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
import androidx.annotation.CheckResult
import com.pyamsoft.pydroid.core.bus.EventBus
import io.reactivex.Scheduler
import io.reactivex.disposables.Disposable

class SettingsPreferenceViewModel internal constructor(
  private val linkerErrorBus: EventBus<ActivityNotFoundException>,
  private val foregroundScheduler: Scheduler,
  private val backgroundScheduler: Scheduler
) {

  @CheckResult
  fun onLinkerError(func: (Throwable) -> Unit): Disposable {
    return linkerErrorBus.listen()
        .subscribeOn(backgroundScheduler)
        .observeOn(foregroundScheduler)
        .subscribe(func)
  }
}
