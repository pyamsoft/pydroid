/*
 * Copyright 2017 Peter Kenji Yamanaka
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
 */

package com.pyamsoft.pydroid.ui.version

import android.app.Dialog
import android.os.Bundle
import android.support.annotation.CheckResult
import android.support.annotation.RestrictTo
import android.support.v7.app.AlertDialog
import com.pyamsoft.pydroid.ui.app.fragment.DialogFragmentBase
import com.pyamsoft.pydroid.ui.social.Linker
import java.util.Locale

@RestrictTo(RestrictTo.Scope.LIBRARY) class VersionUpgradeDialog : DialogFragmentBase() {

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
      Linker.with(context).clickAppPage(context.packageName)
      dismiss()
    }.setNegativeButton("Later") { _, _ -> dismiss() }.create()
  }

  companion object {

    const val TAG = "VersionUpgradeDialog"
    private const val KEY_NAME = "key_name"
    private const val KEY_LATEST_VERSION = "key_latest_version"
    private const val KEY_CURRENT_VERSION = "key_current_version"

    @CheckResult fun newInstance(applicationName: String, currentVersion: Int,
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
