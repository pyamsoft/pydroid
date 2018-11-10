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

package com.pyamsoft.pydroid.util

import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.annotation.CheckResult
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService

@CheckResult
fun isConnected(c: Context): Boolean {
  val appContext = c.applicationContext
  val connMan = requireNotNull(appContext.getSystemService<ConnectivityManager>())
  val permissionCheck = ContextCompat.checkSelfPermission(
      appContext,
      android.Manifest.permission.ACCESS_NETWORK_STATE
  )

  if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
    val activeNetwork: NetworkInfo? = connMan.activeNetworkInfo
    return activeNetwork != null && activeNetwork.isConnected
  } else {
    return false
  }
}

object NoNetworkException : Exception("No Internet connection, please try again later")
