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

package com.pyamsoft.pydroid.base.version

import android.os.Build
import io.reactivex.Single

internal class VersionCheckInteractorImpl internal constructor(
  private val versionCheckService: VersionCheckService
) : VersionCheckInteractor {

  override fun checkVersion(
    packageName: String,
    force: Boolean
  ): Single<Int> {
    return versionCheckService.checkVersion(packageName)
        .map {
          val apiVersion: Int = Build.VERSION.SDK_INT
          var lowestApplicableVersionCode = 0
          it.responseObjects()
              .sortedBy { it.minApi() }
              .forEach {
                if (it.minApi() <= apiVersion) {
                  lowestApplicableVersionCode = it.version()
                }
              }

          return@map lowestApplicableVersionCode
        }
  }
}
