/*
 *     Copyright (C) 2017 Peter Kenji Yamanaka
 *
 *     This program is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation; either version 2 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License along
 *     with this program; if not, write to the Free Software Foundation, Inc.,
 *     51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
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
