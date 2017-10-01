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

package com.pyamsoft.pydroid.util

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import android.support.annotation.CheckResult
import android.widget.Toast
import timber.log.Timber

object NetworkUtil {

  @JvmStatic fun newLink(c: Context, link: String) {
    val uri = Uri.parse(link)
    val intent = Intent(Intent.ACTION_VIEW)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    intent.data = uri
    Timber.d("Start intent for URI: %s", uri)
    try {
      c.applicationContext.startActivity(intent)
    } catch (e: Exception) {
      Timber.e(e, "Error")
      Toast.makeText(c.applicationContext, "No activity available to handle link: " + link,
          Toast.LENGTH_SHORT).show()
    }
  }

  @JvmStatic @CheckResult fun hasConnection(c: Context): Boolean {
    val connMan = c.applicationContext.getSystemService(
        Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetwork = connMan.activeNetworkInfo
    return activeNetwork != null && activeNetwork.isConnectedOrConnecting
  }
}
