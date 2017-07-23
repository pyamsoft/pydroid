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

import android.content.Context
import android.content.res.AssetManager
import android.os.Build
import android.support.annotation.CheckResult
import io.reactivex.Observable
import io.reactivex.Single
import timber.log.Timber
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.util.HashMap

class AboutLibrariesInteractor(context: Context) {

  private val assetManager: AssetManager = context.applicationContext.assets
  @JvmField protected val cachedLicenses: MutableMap<String, String> = HashMap()

  @CheckResult internal fun loadLicenses(): Observable<AboutLibrariesModel> {
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

  @CheckResult protected fun loadLicenseText(model: AboutLibrariesModel): Single<String> {
    return Single.fromCallable<String> {
      val name = model.name
      val result: String
      if (cachedLicenses.containsKey(name)) {
        Timber.d("Fetch from cache for name: %s", name)
        result = cachedLicenses.getOrDefault(name, "")
      } else {
        if (model.customContent.isEmpty()) {
          Timber.d("Load from asset location: %s (%s)", name, model.license)
          result = loadNewLicense(model.license)
        } else {
          Timber.d("License: %s provides custom content", name)
          result = model.customContent
        }
      }

      return@fromCallable result
    }.doOnSuccess {
      if (it.isNotEmpty()) {
        Timber.d("Put license into cache for model: %s", model.name)
        cachedLicenses.put(model.name, it)
      }
    }
  }

  @CheckResult protected fun loadNewLicense(licenseLocation: String): String {
    if (licenseLocation.isEmpty()) {
      Timber.w("Empty license passed")
      return ""
    }

    assetManager.open(licenseLocation).use {
      // Standard Charsets is only KitKat, add this extra check to support Home Button
      val inputStreamReader: InputStreamReader
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        inputStreamReader = InputStreamReader(it, StandardCharsets.UTF_8)
      } else {
        inputStreamReader = InputStreamReader(it, "UTF-8")
      }

      BufferedReader(inputStreamReader).use {
        val text = StringBuilder()
        var line: String? = it.readLine()
        while (line != null) {
          text.append(line).append('\n')
          line = it.readLine()
        }
        return text.toString()
      }
    }
  }
}
