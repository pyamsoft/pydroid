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

import android.content.Context
import android.content.res.AssetManager
import android.os.Build
import android.support.annotation.CheckResult
import timber.log.Timber
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

internal class AboutLibrariesDataSourceImpl internal constructor(
    context: Context) : AboutLibrariesDataSource {

  private val assetManager: AssetManager = context.applicationContext.assets

  @CheckResult override fun loadNewLicense(licenseLocation: String): String {
    if (licenseLocation.isEmpty()) {
      Timber.w("Empty license passed")
      return ""
    }

    assetManager.open(licenseLocation).use {
      // Standard Charsets is only KitKat, add this extra check to support Home Button
      val inputStreamReader = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        // Assign
        InputStreamReader(it, StandardCharsets.UTF_8)
      } else {
        // Assign
        InputStreamReader(it, "UTF-8")
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
