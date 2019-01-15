/*
 * Copyright 2019 Peter Kenji Yamanaka
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
 *
 */

package com.pyamsoft.pydroid.ui.settings

import android.os.Bundle
import com.pyamsoft.pydroid.bootstrap.SchedulerProvider
import com.pyamsoft.pydroid.core.bus.Listener
import com.pyamsoft.pydroid.ui.arch.UiComponent
import io.reactivex.Observable
import java.lang.RuntimeException

internal class AppSettingsUiComponent internal constructor(
  private val settingsView: AppSettingsView,
  private val uiBus: Listener<AppSettingsViewEvent>,
  private val schedulerProvider: SchedulerProvider
) : UiComponent<AppSettingsViewEvent> {

  override fun id(): Int {
    throw RuntimeException("""
      |The View which powers this UiComponent is in turn powered
      |by a PreferenceFragment from the AndroidX framework which
      |is a strange beast and does not fit into the UiComponent
      |architecture that the rest of the application has tried to
      |establish. This view has no id(), and to attempt to use it
      |is incorrect.
    """.trimMargin())
  }

  override fun create(savedInstanceState: Bundle?) {
    settingsView.inflate(savedInstanceState)
  }

  override fun saveState(outState: Bundle) {
    settingsView.saveState(outState)
  }

  override fun onUiEvent(): Observable<AppSettingsViewEvent> {
    return uiBus.listen()
        .subscribeOn(schedulerProvider.backgroundScheduler)
        .observeOn(schedulerProvider.foregroundScheduler)
  }

}