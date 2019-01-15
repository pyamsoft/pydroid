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

package com.pyamsoft.pydroid.ui.about

import androidx.annotation.CheckResult
import com.pyamsoft.pydroid.bootstrap.SchedulerProvider
import com.pyamsoft.pydroid.bootstrap.about.AboutInteractor
import com.pyamsoft.pydroid.core.bus.Publisher
import com.pyamsoft.pydroid.ui.about.AboutStateEvent.LicensesLoaded
import com.pyamsoft.pydroid.ui.about.AboutStateEvent.LoadComplete
import com.pyamsoft.pydroid.ui.about.AboutStateEvent.LoadError
import com.pyamsoft.pydroid.ui.about.AboutStateEvent.Loading
import com.pyamsoft.pydroid.ui.arch.Worker
import io.reactivex.disposables.Disposable
import timber.log.Timber

class AboutWorker internal constructor(
  private val interactor: AboutInteractor,
  private val bus: Publisher<AboutStateEvent>,
  private val schedulerProvider: SchedulerProvider
) : Worker<AboutStateEvent> {

  @CheckResult
  fun loadLicenses(force: Boolean): Disposable {
    return interactor.loadLicenses(force)
        .subscribeOn(schedulerProvider.backgroundScheduler)
        .observeOn(schedulerProvider.foregroundScheduler)
        .doOnSubscribe { bus.publish(Loading) }
        .doAfterTerminate { bus.publish(LoadComplete) }
        .subscribe({ bus.publish(LicensesLoaded(it)) }, {
          Timber.e(it, "Error loading licenses")
          bus.publish(LoadError(it))
        })
  }
}
