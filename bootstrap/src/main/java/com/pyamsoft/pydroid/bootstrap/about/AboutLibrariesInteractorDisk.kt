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
import io.reactivex.Observable
import io.reactivex.Single

internal class AboutLibrariesInteractorDisk internal constructor(
  private val dataSource: AboutLibrariesDataSource
) : AboutLibrariesInteractor {

  @CheckResult
  private fun createLicenseStream(): Single<Set<AboutLibrariesModel>> {
    return Single.fromCallable { AboutLibraries.getLicenses() }
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
  private fun loadTextForAboutModel(model: AboutLibrariesModel): Single<AboutLibrariesModel> {
    return loadLicenseText(model).map { AboutLibrariesModel.create(model.name, model.homepage, it) }
  }

  override fun loadLicenses(bypass: Boolean): Single<List<AboutLibrariesModel>> {
    return createLicenseStream()
        .flatMapObservable { Observable.fromIterable(it) }
        .flatMapSingle { loadTextForAboutModel(it) }
        .toSortedList { o1, o2 -> o1.name.compareTo(o2.name, ignoreCase = true) }
  }
}
