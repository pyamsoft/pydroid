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
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.util.Collections
import java.util.HashMap

class AboutLibrariesInteractor(context: Context, licenses: List<AboutLibrariesModel>) {

  internal val licenses: List<AboutLibrariesModel> = Collections.unmodifiableList(licenses)
  private val assetManager: AssetManager = context.applicationContext.assets
  internal val cachedLicenses: MutableMap<String, String>

  init {
    cachedLicenses = HashMap<String, String>()
  }

  /**
   * public
   */
  @CheckResult internal fun loadLicenses(): Observable<AboutLibrariesModel> {
    return Observable.defer {
      Observable.fromIterable(licenses)
    }.toSortedList { (name1), (name2) ->
      name1.compareTo(name2)
    }.toObservable().concatMap<AboutLibrariesModel>({
      Observable.fromIterable(it)
    }).map {
      AboutLibrariesModel.create(it.name, it.homepage, loadLicenseText(it))
    }
  }

  @CheckResult internal fun loadLicenseText(model: AboutLibrariesModel): String {
    return Single.fromCallable<String> {
      val name = model.name
      if (cachedLicenses.containsKey(name)) {
        Timber.d("Fetch from cache for name: %s", name)
        cachedLicenses[name]
      } else {
        if (model.customContent.isEmpty()) {
          Timber.d("Load from asset location: %s (%s)", name, model.license)
          loadNewLicense(model.license)
        } else {
          Timber.d("License: %s provides custom content", name)
          model.customContent
        }
      }
    }.doOnSuccess { license ->
      if (!license.isEmpty()) {
        Timber.d("Put license into cache for model: %s", model.name)
        cachedLicenses.put(model.name, license)
      }
    }.blockingGet()
  }

  @CheckResult @Throws(IOException::class) internal fun loadNewLicense(
      licenseLocation: String): String {
    if (licenseLocation.isEmpty()) {
      Timber.w("Empty license passed")
      return ""
    }

    val licenseText: String
    var fileInputStream: InputStream? = null
    try {
      fileInputStream = assetManager.open(licenseLocation)
      // Standard Charsets is only KitKat, add this extra check to support Home Button
      val inputStreamReader: InputStreamReader
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        inputStreamReader = InputStreamReader(fileInputStream!!, StandardCharsets.UTF_8)
      } else {
        inputStreamReader = InputStreamReader(fileInputStream!!, "UTF-8")
      }

      val br = BufferedReader(inputStreamReader)
      val text = StringBuilder()
      var line: String? = br.readLine()
      while (line != null) {
        text.append(line).append('\n')
        line = br.readLine()
      }
      br.close()

      licenseText = text.toString()
    } finally {
      fileInputStream?.close()
    }

    return licenseText
  }
}
