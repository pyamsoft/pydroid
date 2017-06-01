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

package com.pyamsoft.pydroid.about

import android.support.annotation.CheckResult
import android.support.annotation.RestrictTo
import com.pyamsoft.pydroid.PYDroidModule
import io.reactivex.Scheduler

@RestrictTo(RestrictTo.Scope.LIBRARY) class AboutLibrariesModule(pyDroidModule: PYDroidModule) {

  private val interactor: AboutLibrariesInteractor = AboutLibrariesInteractor(
      pyDroidModule.provideContext(), pyDroidModule.provideLicenseMap())
  private val obsScheduler: Scheduler = pyDroidModule.provideObsScheduler()
  private val subScheduler: Scheduler = pyDroidModule.provideSubScheduler()

  @CheckResult fun getPresenter(): AboutLibrariesPresenter {
    return AboutLibrariesPresenter(interactor, obsScheduler, subScheduler)
  }
}
