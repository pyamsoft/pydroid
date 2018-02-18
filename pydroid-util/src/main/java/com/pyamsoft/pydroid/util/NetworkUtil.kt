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

object NetworkUtil {

  @JvmStatic
  @CheckResult
  fun asHyperlink(
    c: Context,
    link: String,
    navigate: Boolean = true
  ): HyperlinkIntent {
    val intent = Intent(Intent.ACTION_VIEW).apply {
      addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      data = Uri.parse(link)
    }

    return HyperlinkIntent(c.applicationContext, intent, link).also {
      if (navigate) {
        it.navigate()
      }
    }
  }

  @JvmStatic
  @CheckResult
  fun isConnected(c: Context): Boolean {
    val connMan =
      c.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetwork: NetworkInfo? = connMan.activeNetworkInfo
    return activeNetwork != null && activeNetwork.isConnectedOrConnecting
  }
}

fun String.navigate(context: Context): HyperlinkIntent {
  return NetworkUtil.asHyperlink(context, this, navigate = true)
}
