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

package com.pyamsoft.pydroid.bootstrap.about

import androidx.annotation.CheckResult
import androidx.lifecycle.LifecycleOwner
import com.pyamsoft.pydroid.bootstrap.SchedulerProvider
import com.pyamsoft.pydroid.bootstrap.libraries.OssLibrary
import com.pyamsoft.pydroid.core.threads.Enforcer
import com.pyamsoft.pydroid.core.viewmodel.DataBus

class AboutLibrariesModule(
  enforcer: Enforcer,
  private val schedulerProvider: SchedulerProvider
) {

  private val interactor: AboutLibrariesInteractor
  private val viewBus = DataBus<List<OssLibrary>>()

  init {
    interactor = AboutLibrariesInteractorImpl(enforcer)
  }

  @CheckResult
  fun getViewModel(owner: LifecycleOwner): AboutLibrariesViewModel {
    return AboutLibrariesViewModel(
        owner,
        viewBus,
        interactor,
        schedulerProvider.foregroundScheduler,
        schedulerProvider.backgroundScheduler
    )
  }
}
