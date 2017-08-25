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
import io.reactivex.Observable
import io.reactivex.Single
import timber.log.Timber

internal class AboutLibrariesInteractorImpl internal constructor(
    private val dataSource: AboutLibrariesDataSource) : AboutLibrariesInteractor {

  override fun loadLicenses(force: Boolean): Observable<AboutLibrariesModel> {
    return Observable.defer {
      Observable.fromIterable(Licenses.getLicenses())
    }.toSortedList { (name1), (name2) ->
      name1.compareTo(name2)
    }.flatMapObservable { Observable.fromIterable(it) }.concatMap { model ->
      loadLicenseText(model).flatMapObservable {
        Observable.just(AboutLibrariesModel.create(model.name, model.homepage, it))
      }
    }
  }

  @CheckResult private fun loadLicenseText(model: AboutLibrariesModel): Single<String> {
    return Single.fromCallable<String> {
      val name = model.name

      if (model.customContent.isEmpty()) {
        Timber.d("Load from asset location: %s (%s)", name, model.license)
        return@fromCallable dataSource.loadNewLicense(model.license)
      } else {
        Timber.d("License: %s provides custom content", name)
        return@fromCallable model.customContent
      }
    }
  }
}
