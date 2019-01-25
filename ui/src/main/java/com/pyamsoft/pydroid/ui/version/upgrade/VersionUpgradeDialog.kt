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

package com.pyamsoft.pydroid.ui.version.upgrade

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CheckResult
import com.pyamsoft.pydroid.ui.PYDroid
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.app.fragment.ToolbarDialog
import com.pyamsoft.pydroid.ui.app.fragment.requireArguments
import com.pyamsoft.pydroid.ui.arch.destroy
import com.pyamsoft.pydroid.ui.util.MarketLinker
import com.pyamsoft.pydroid.ui.version.upgrade.VersionUpgradeViewEvent.Cancel
import com.pyamsoft.pydroid.ui.version.upgrade.VersionUpgradeViewEvent.Upgrade

internal class VersionUpgradeDialog : ToolbarDialog() {

  internal lateinit var controlsComponent: VersionUpgradeControlsUiComponent
  internal lateinit var contentComponent: VersionUpgradeContentUiComponent
  internal lateinit var worker: VersionUpgradeWorker

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    val root = inflater.inflate(R.layout.layout_linear_vertical, container, false)

    val latestVersion = requireArguments().getInt(KEY_LATEST_VERSION, 0)
    require(latestVersion > 0)

    PYDroid.obtain(root.context.applicationContext)
        .plusVersionUpgradeComponent(viewLifecycleOwner, root as ViewGroup, latestVersion)
        .inject(this)

    return root
  }

  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    super.onViewCreated(view, savedInstanceState)

    listenForUiEvents(view)
    contentComponent.create(savedInstanceState)
    controlsComponent.create(savedInstanceState)
  }

  private fun listenForUiEvents(view: View) {
    controlsComponent.onUiEvent()
        .subscribe {
          return@subscribe when (it) {
            is Cancel -> dismiss()
            is Upgrade -> onUpgradeClicked(view)
          }
        }
        .destroy(viewLifecycleOwner)
  }

  private fun onUpgradeClicked(view: View) {
    view.context.also {
      val error = MarketLinker.linkToMarketPage(it, it.packageName)

      dismiss()
      if (error != null) {
        worker.failedMarketLink(error)
      }
    }
  }

  override fun onResume() {
    super.onResume()
    dialog.window?.setLayout(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    )
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    contentComponent.saveState(outState)
    controlsComponent.saveState(outState)
  }

  companion object {

    internal const val TAG = "VersionUpgradeDialog"
    private const val KEY_LATEST_VERSION = "key_latest_version"

    @JvmStatic
    @CheckResult
    fun newInstance(latestVersion: Int): VersionUpgradeDialog {
      return VersionUpgradeDialog()
          .apply {
            arguments = Bundle().apply {
              putInt(KEY_LATEST_VERSION, latestVersion)
            }
          }
    }
  }
}
