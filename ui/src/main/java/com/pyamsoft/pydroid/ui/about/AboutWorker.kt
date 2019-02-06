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
import com.pyamsoft.pydroid.core.bus.EventBus
import com.pyamsoft.pydroid.ui.about.AboutStateEvent.BroadcastViewLicense
import com.pyamsoft.pydroid.ui.about.AboutStateEvent.BroadcastVisitHomepage
import com.pyamsoft.pydroid.ui.about.AboutStateEvent.LicensesLoaded
import com.pyamsoft.pydroid.ui.about.AboutStateEvent.LoadComplete
import com.pyamsoft.pydroid.ui.about.AboutStateEvent.LoadError
import com.pyamsoft.pydroid.ui.about.AboutStateEvent.Loading
import io.reactivex.disposables.Disposable
import timber.log.Timber

class AboutWorker internal constructor(
  private val interactor: AboutInteractor,
  private val schedulerProvider: SchedulerProvider,
  bus: EventBus<AboutStateEvent>
) : Worker<AboutStateEvent>(bus) {

  @CheckResult
  fun onViewLicenseEvent(func: (payload: BroadcastViewLicense) -> Unit): Disposable {
    return listen()
        .ofType(BroadcastViewLicense::class.java)
        .subscribeOn(schedulerProvider.backgroundScheduler)
        .observeOn(schedulerProvider.foregroundScheduler)
        .subscribe(func)
  }

  @CheckResult
  fun onVisitHomepageEvent(func: (payload: BroadcastVisitHomepage) -> Unit): Disposable {
    return listen()
        .ofType(BroadcastVisitHomepage::class.java)
        .subscribeOn(schedulerProvider.backgroundScheduler)
        .observeOn(schedulerProvider.foregroundScheduler)
        .subscribe(func)
  }

  @CheckResult
  fun loadLicenses(force: Boolean): Disposable {
    return interactor.loadLicenses(force)
        .subscribeOn(schedulerProvider.backgroundScheduler)
        .observeOn(schedulerProvider.foregroundScheduler)
        .doOnSubscribe { publish(Loading) }
        .doAfterTerminate { publish(LoadComplete) }
        .subscribe({ publish(LicensesLoaded(it)) }, {
          Timber.e(it, "Error loading licenses")
          publish(LoadError(it))
        })
  }
}
