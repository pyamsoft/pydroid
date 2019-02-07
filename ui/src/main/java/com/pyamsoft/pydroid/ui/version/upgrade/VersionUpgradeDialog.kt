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

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CheckResult
import androidx.fragment.app.DialogFragment
import com.pyamsoft.pydroid.ui.PYDroid
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.app.noTitle
import com.pyamsoft.pydroid.ui.app.requireArguments
import com.pyamsoft.pydroid.ui.navigation.FailedNavigationPresenter
import com.pyamsoft.pydroid.ui.util.MarketLinker
import com.pyamsoft.pydroid.ui.version.upgrade.VersionUpgradePresenter.Callback

internal class VersionUpgradeDialog : DialogFragment(), Callback {

  internal lateinit var controlsView: VersionUpgradeControlView
  internal lateinit var contentView: VersionUpgradeContentView
  internal lateinit var failedNavigationPresenter: FailedNavigationPresenter
  internal lateinit var presenter: VersionUpgradePresenter

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    return super.onCreateDialog(savedInstanceState)
        .noTitle()
  }

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
    contentView.inflate(savedInstanceState)
    controlsView.inflate(savedInstanceState)
    presenter.bind(this)
  }

  override fun onResume() {
    super.onResume()
    dialog.window?.setLayout(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    )
  }

  override fun onDestroyView() {
    super.onDestroyView()
    contentView.teardown()
    controlsView.teardown()
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    contentView.saveState(outState)
    controlsView.saveState(outState)
  }

  override fun onUpgradeBegin() {
    val error = MarketLinker.linkToMarketPage(requireContext(), requireContext().packageName)
    if (error != null) {
      failedNavigationPresenter.failedNavigation(error)
    }
  }

  override fun onUpgradeCancel() {
    dismiss()
  }

  companion object {

    internal const val TAG = "VersionUpgradeDialog"
    private const val KEY_LATEST_VERSION = "key_latest_version"

    @JvmStatic
    @CheckResult
    fun newInstance(latestVersion: Int): VersionUpgradeDialog {
      return VersionUpgradeDialog().apply {
        arguments = Bundle().apply {
          putInt(KEY_LATEST_VERSION, latestVersion)
        }
      }
    }
  }
}
