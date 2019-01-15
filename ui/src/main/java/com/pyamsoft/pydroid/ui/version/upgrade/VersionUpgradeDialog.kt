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
import com.google.android.material.snackbar.Snackbar
import com.pyamsoft.pydroid.ui.PYDroid
import com.pyamsoft.pydroid.ui.app.fragment.ToolbarDialog
import com.pyamsoft.pydroid.ui.app.fragment.requireArguments
import com.pyamsoft.pydroid.ui.arch.destroy
import com.pyamsoft.pydroid.ui.databinding.LayoutLinearVerticalBinding
import com.pyamsoft.pydroid.ui.util.MarketLinker
import com.pyamsoft.pydroid.ui.util.Snackbreak
import com.pyamsoft.pydroid.ui.version.upgrade.VersionViewEvent.Cancel
import com.pyamsoft.pydroid.ui.version.upgrade.VersionViewEvent.Upgrade

internal class VersionUpgradeDialog : ToolbarDialog() {

  internal lateinit var controlsComponent: VersionUpgradeControlsUiComponent
  internal lateinit var contentComponent: VersionUpgradeContentUiComponent

  private var marketSnackbar: Snackbar? = null

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    val binding = LayoutLinearVerticalBinding.inflate(inflater, container, false)

    val latestVersion = requireArguments().getInt(KEY_LATEST_VERSION, 0)
    require(latestVersion > 0)

    PYDroid.obtain(binding.layoutRoot.context.applicationContext)
        .plusVersionUpgradeComponent(binding.layoutRoot, latestVersion)
        .inject(this)

    return binding.root
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
            is Upgrade -> {
              val error = MarketLinker.linkToMarketPage(view.context, view.context.packageName)
              if (error == null) {
                dismiss()
              } else {
                showMarketSnackbar(view)
              }
            }
          }
        }
        .destroy(viewLifecycleOwner)
  }

  private fun showMarketSnackbar(view: View) {
    dismissSnackbar()
    marketSnackbar = Snackbreak.short(
        view,
        "No application is able to handle Store URLs."
    )
        .also { bar -> bar.show() }
  }

  private fun dismissSnackbar() {
    marketSnackbar?.dismiss()
    marketSnackbar = null
  }

  override fun onDestroyView() {
    super.onDestroyView()
    dismissSnackbar()
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
