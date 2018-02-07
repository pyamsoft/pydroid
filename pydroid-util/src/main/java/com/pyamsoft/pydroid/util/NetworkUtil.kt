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
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.support.annotation.CheckResult
import android.widget.Toast

object NetworkUtil {

  @JvmStatic
  fun newLink(
    c: Context,
    link: String
  ) {
    val intent = Intent(Intent.ACTION_VIEW).apply {
      addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      data = Uri.parse(link)
    }

    try {
      c.applicationContext.startActivity(intent)
    } catch (e: Exception) {
      Toasty.makeText(
          c.applicationContext, "No activity available to handle link: " + link,
          Toast.LENGTH_SHORT
      )
          .show()
    }
  }

  @JvmStatic
  @CheckResult
  fun hasConnection(c: Context): Boolean {
    val connMan =
      c.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetwork: NetworkInfo? = connMan.activeNetworkInfo
    return activeNetwork != null && activeNetwork.isConnectedOrConnecting
  }
}
