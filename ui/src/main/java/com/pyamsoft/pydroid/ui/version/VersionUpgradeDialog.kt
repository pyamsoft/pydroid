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

package com.pyamsoft.pydroid.ui.version

import android.app.Dialog
import android.os.Bundle
import androidx.annotation.CheckResult
import androidx.appcompat.app.AlertDialog
import com.pyamsoft.pydroid.ui.PYDroid
import com.pyamsoft.pydroid.ui.app.fragment.ToolbarDialog
import com.pyamsoft.pydroid.ui.app.fragment.requireArguments
import com.pyamsoft.pydroid.ui.util.MarketLinker

internal class VersionUpgradeDialog : ToolbarDialog() {

  private var latestVersion: Int = 0
  private var currentVersion: Int = 0
  internal lateinit var applicationName: String

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    PYDroid.obtain(requireContext().applicationContext)
        .inject(this)

    latestVersion = requireArguments().getInt(KEY_LATEST_VERSION, 0)
    currentVersion = requireArguments().getInt(KEY_CURRENT_VERSION, 0)
    require(latestVersion > 0)
    require(currentVersion > 0)

    val message = """|A new version of $applicationName is available!
                     |Current version: $currentVersion
                     |Latest verson: $latestVersion""".trimMargin()
    return AlertDialog.Builder(requireActivity())
        .setTitle("New version available")
        .setMessage(message)
        .setPositiveButton("Update") { _, _ ->
          // Show on the Activity window because we may have dismissed at the resolution point.
          requireActivity().also {
            MarketLinker.linkToMarketPage(it.packageName, it.window.decorView)
          }
          dismiss()
        }
        .setNegativeButton("Later") { _, _ -> dismiss() }
        .create()
  }

  companion object {

    internal const val TAG = "VersionUpgradeDialog"
    private const val KEY_LATEST_VERSION = "key_latest_version"
    private const val KEY_CURRENT_VERSION = "key_current_version"

    @JvmStatic
    @CheckResult
    fun newInstance(
      currentVersion: Int,
      latestVersion: Int
    ): VersionUpgradeDialog {
      return VersionUpgradeDialog().apply {
        arguments = Bundle().apply {
          putInt(KEY_CURRENT_VERSION, currentVersion)
          putInt(KEY_LATEST_VERSION, latestVersion)
        }
      }
    }
  }
}
