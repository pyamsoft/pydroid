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
import com.pyamsoft.pydroid.core.DataBus
import com.pyamsoft.pydroid.core.DataWrapper
import io.reactivex.Scheduler
import io.reactivex.disposables.Disposable
import timber.log.Timber

class AboutLibrariesViewModel internal constructor(
  private val licenseBus: DataBus<List<AboutLibrariesModel>>,
  private val interactor: AboutLibrariesInteractor,
  private val foregroundScheduler: Scheduler,
  private val backgroundScheduler: Scheduler
) {

  @CheckResult
  fun onLicensesLoaded(func: (DataWrapper<List<AboutLibrariesModel>>) -> Unit): Disposable {
    return licenseBus.listen()
        .subscribeOn(backgroundScheduler)
        .observeOn(foregroundScheduler)
        .subscribe(func)
  }

  fun loadLicenses(force: Boolean): Disposable {
    return interactor.loadLicenses(force)
        .subscribeOn(backgroundScheduler)
        .observeOn(foregroundScheduler)
        .doOnSubscribe { licenseBus.publishLoading(force) }
        .doAfterTerminate { licenseBus.publishComplete() }
        .subscribe({ licenseBus.publishSuccess(it) }, {
          Timber.e(it, "Error loading licenses")
          licenseBus.publishError(it)
        })
  }
}
