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

import android.content.Context
import android.content.res.AssetManager
import android.os.Build
import android.support.annotation.CheckResult
import timber.log.Timber
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.io.Reader
import java.nio.charset.StandardCharsets

internal class AboutLibrariesDataSourceImpl internal constructor(
  context: Context
) : AboutLibrariesDataSource {

  private val assetManager: AssetManager = context.applicationContext.assets

  @CheckResult
  private fun createStreamReader(inputStream: InputStream): Reader {
    // Standard Charsets is only KitKat, add this extra check to support API 16 apps
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      return InputStreamReader(inputStream, StandardCharsets.UTF_8)
    } else {
      return InputStreamReader(inputStream, "UTF-8")
    }
  }

  @CheckResult
  override fun loadNewLicense(licenseLocation: String): String {
    if (licenseLocation.isEmpty()) {
      Timber.w("Empty license passed")
      return ""
    }

    assetManager.open(licenseLocation)
        .use {
          BufferedReader(createStreamReader(it)).useLines {
            return it.joinToString(separator = "\n") { it }
          }
        }
  }
}
