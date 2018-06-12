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

import android.content.Context
import android.content.res.AssetManager
import androidx.annotation.CheckResult
import okio.BufferedSource
import okio.Okio
import timber.log.Timber
import java.io.InputStream

internal class AboutLibrariesDataSourceImpl internal constructor(
  context: Context
) : AboutLibrariesDataSource {

  private val assetManager: AssetManager = context.applicationContext.assets

  @CheckResult
  private fun createStreamReader(inputStream: InputStream): BufferedSource {
    return Okio.buffer(Okio.source(inputStream))
  }

  @CheckResult
  private fun readLines(source: BufferedSource): String {
    source.use {
      val lines = ArrayList<String>()
      while (true) {
        val line = it.readUtf8Line()
        if (line == null) {
          break
        } else {
          lines.add(line.trim())
        }
      }

      return lines.joinToString(separator = "\n") { it }
    }
  }

  @CheckResult
  override fun loadNewLicense(licenseLocation: String): String {
    if (licenseLocation.isEmpty()) {
      Timber.w("Empty license passed")
      return ""
    }

    assetManager.open(licenseLocation)
        .use { return readLines(createStreamReader(it)) }
  }
}
