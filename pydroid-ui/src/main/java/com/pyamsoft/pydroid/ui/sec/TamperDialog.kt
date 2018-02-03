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

package com.pyamsoft.pydroid.ui.sec

import android.app.ActivityManager
import android.app.Dialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AlertDialog
import com.pyamsoft.pydroid.ui.app.fragment.ToolbarDialog
import com.pyamsoft.pydroid.ui.social.Linker

internal class TamperDialog : ToolbarDialog() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    isCancelable = false
  }

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    activity!!.let {
      return AlertDialog.Builder(it)
          .setTitle(
              "WARNING: THIS APPLICATION IS NOT OFFICIAL"
          )
          .setMessage(
              R.string.tamper_msg
          )
          .setCancelable(
              false
          )
          .setPositiveButton("Take Me") { _, _ ->
            Linker.clickGooglePlay(it)
            killApp()
          }
          .setNegativeButton("Close") { _, _ -> killApp() }
          .create()
    }
  }

  /**
   * Kills the app and clears the data to prevent any malicious services or code from possibly
   * running in the background
   */
  private fun killApp() {
    dismiss()
    activity?.let {
      it.finish()
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        val activityManager = it.applicationContext.getSystemService(
            Context.ACTIVITY_SERVICE
        ) as ActivityManager
        activityManager.clearApplicationUserData()
      }
    }
  }
}
