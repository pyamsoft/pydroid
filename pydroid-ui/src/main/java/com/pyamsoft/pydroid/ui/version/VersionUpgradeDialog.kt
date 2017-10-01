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

package com.pyamsoft.pydroid.ui.version

import android.app.Dialog
import android.os.Bundle
import android.support.annotation.CheckResult
import android.support.v7.app.AlertDialog
import com.pyamsoft.pydroid.ui.app.fragment.DialogFragmentBase
import com.pyamsoft.pydroid.ui.social.Linker

class VersionUpgradeDialog : DialogFragmentBase() {

  private var latestVersion: Int = 0
  private var currentVersion: Int = 0
  private var applicationName: String? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    latestVersion = arguments.getInt(KEY_LATEST_VERSION, 0)
    if (latestVersion == 0) {
      throw RuntimeException("Could not find latest version")
    }

    currentVersion = arguments.getInt(KEY_CURRENT_VERSION, 0)
    if (currentVersion == 0) {
      throw RuntimeException("Could not find current version")
    }

    applicationName = arguments.getString(KEY_NAME, null)
    if (applicationName == null) {
      throw RuntimeException("Could not find application name")
    }
  }

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    val message = """|A new version of $applicationName is available!
                     |Current version: $currentVersion
                     |Latest verson: $latestVersion""".trimMargin()
    return AlertDialog.Builder(activity).setTitle("New version available").setMessage(
        message).setPositiveButton("Update") { _, _ ->
      Linker.clickAppPage(context, context.packageName)
      dismiss()
    }.setNegativeButton("Later") { _, _ -> dismiss() }.create()
  }

  companion object {

    const val TAG = "VersionUpgradeDialog"
    private const val KEY_NAME = "key_name"
    private const val KEY_LATEST_VERSION = "key_latest_version"
    private const val KEY_CURRENT_VERSION = "key_current_version"

    @CheckResult
    fun newInstance(applicationName: String, currentVersion: Int,
        latestVersion: Int): VersionUpgradeDialog {
      val args = Bundle()
      val fragment = VersionUpgradeDialog()
      args.putString(KEY_NAME, applicationName)
      args.putInt(KEY_CURRENT_VERSION, currentVersion)
      args.putInt(KEY_LATEST_VERSION, latestVersion)
      fragment.arguments = args
      return fragment
    }
  }
}
