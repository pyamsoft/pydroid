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
import timber.log.Timber
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

class AboutLibrariesDataSourceImpl(context: Context) : AboutLibrariesDataSource {

  private val assetManager: AssetManager = context.applicationContext.assets

  @CheckResult override fun loadNewLicense(licenseLocation: String): String {
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
