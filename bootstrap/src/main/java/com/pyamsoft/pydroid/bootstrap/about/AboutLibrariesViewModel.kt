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

import androidx.lifecycle.LifecycleOwner
import com.pyamsoft.pydroid.core.viewmodel.DataWrapper
import com.pyamsoft.pydroid.core.viewmodel.LifecycleViewModel
import com.pyamsoft.pydroid.core.viewmodel.LiveDataWrapper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class AboutLibrariesViewModel internal constructor(
  private val licenseBus: LiveDataWrapper<List<AboutLibrariesModel>>,
  private val interactor: AboutLibrariesInteractor
) : LifecycleViewModel {

  fun onLicensesLoaded(
    owner: LifecycleOwner,
    func: (DataWrapper<List<AboutLibrariesModel>>) -> Unit
  ) {
    licenseBus.observe(owner, func)
  }

  fun loadLicenses(
    owner: LifecycleOwner,
    force: Boolean
  ) {
    interactor.loadLicenses(force)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnSubscribe { licenseBus.publishLoading(force) }
        .doAfterTerminate { licenseBus.publishComplete() }
        .subscribe({ licenseBus.publishSuccess(it) }, {
          Timber.e(it, "Error loading licenses")
          licenseBus.publishError(it)
        })
        .bind(owner)
  }
}
