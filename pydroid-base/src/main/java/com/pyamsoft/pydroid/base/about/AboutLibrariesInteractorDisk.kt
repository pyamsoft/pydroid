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

package com.pyamsoft.pydroid.base.about

import android.support.annotation.CheckResult
import io.reactivex.Observable
import io.reactivex.Single

internal class AboutLibrariesInteractorDisk internal constructor(
  private val dataSource: AboutLibrariesDataSource
) : AboutLibrariesInteractor {

  @CheckResult
  private fun createLicenseStream(): Observable<AboutLibrariesModel> {
    return Observable.defer { Observable.fromIterable(Licenses.getLicenses()) }
  }

  @CheckResult
  private fun loadLicenseText(model: AboutLibrariesModel): Single<String> {
    return Single.fromCallable<String> {
      if (model.customContent.isEmpty()) {
        return@fromCallable dataSource.loadNewLicense(model.license)
      } else {
        return@fromCallable model.customContent
      }
    }
  }

  @CheckResult
  private fun loadTextForAboutModel(model: AboutLibrariesModel): Observable<AboutLibrariesModel> {
    return loadLicenseText(model).map { AboutLibrariesModel.create(model.name, model.homepage, it) }
        .toObservable()
  }

  override fun loadLicenses(bypass: Boolean): Observable<AboutLibrariesModel> {
    return createLicenseStream()
        .sorted { o1, o2 -> o1.name.compareTo(o2.name, ignoreCase = true) }
        .concatMap { loadTextForAboutModel(it) }
  }
}
