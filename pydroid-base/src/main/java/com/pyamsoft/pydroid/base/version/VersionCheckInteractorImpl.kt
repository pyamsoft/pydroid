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

package com.pyamsoft.pydroid.base.version

import android.content.Context
import android.os.Build
import com.pyamsoft.pydroid.util.NoNetworkException
import com.pyamsoft.pydroid.util.isConnected
import io.reactivex.Single

internal class VersionCheckInteractorImpl internal constructor(
  private val context: Context,
  private val versionCheckService: VersionCheckService
) : VersionCheckInteractor {

  override fun checkVersion(
    packageName: String,
    force: Boolean
  ): Single<Int> {
    return Single.defer {
      if (!isConnected(context)) {
        throw NoNetworkException
      }

      return@defer versionCheckService.checkVersion(packageName)
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
}
