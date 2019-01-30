/*
 * Copyright 2019 Peter Kenji Yamanaka
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
 *
 */

package com.pyamsoft.pydroid.bootstrap.network

import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.annotation.CheckResult
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import timber.log.Timber

internal class NetworkStatusProviderImpl internal constructor(private val context: Context) :
    NetworkStatusProvider {

  private val connMan by lazy { requireNotNull(context.getSystemService<ConnectivityManager>()) }

  override fun hasConnection(): Boolean {
    val permission = hasPermission()
    val networkInfo = getNetworkInfo(permission)
    when {
      networkInfo == null -> {
        Timber.w("Missing network info - unknown connection state")
        return false
      }
      isMeteredNetwork(permission) -> {
        Timber.w("Network connection is metered - treat as disconnected for all intents and purposes")
        return false
      }
      else -> {
        val isConnected = networkInfo.isConnected
        Timber.d("Network is connected: $isConnected")
        return isConnected
      }
    }
  }

  @CheckResult
  private fun hasPermission(): Boolean {
    val check = ContextCompat.checkSelfPermission(
        context,
        android.Manifest.permission.ACCESS_NETWORK_STATE
    )
    return check == PackageManager.PERMISSION_GRANTED
  }

  @CheckResult
  private fun getNetworkInfo(permission: Boolean): NetworkInfo? {
    if (permission) {
      return connMan.activeNetworkInfo
    } else {
      return null
    }
  }

  @CheckResult
  private fun isMeteredNetwork(permission: Boolean): Boolean {
    if (permission) {
      return connMan.isActiveNetworkMetered
    } else {
      return true
    }
  }
}
