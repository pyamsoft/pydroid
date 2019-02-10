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
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.arch.BaseUiView
import com.pyamsoft.pydroid.ui.util.setOnDebouncedClickListener

internal class VersionUpgradeControlView internal constructor(
  parent: ViewGroup,
  callback: VersionUpgradeControlView.Callback
) : BaseUiView<VersionUpgradeControlView.Callback>(parent, callback) {

  private val layoutRoot by lazyView<View>(R.id.version_control_root)
  private val upgradeButton by lazyView<Button>(R.id.upgrade_button)
  private val laterButton by lazyView<Button>(R.id.later_button)

  override val layout: Int = R.layout.version_upgrade_controls

  override fun id(): Int {
    return layoutRoot.id
  }

  override fun onInflated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    bindButtons()
  }

  override fun teardown() {
    upgradeButton.setOnClickListener(null)
    laterButton.setOnClickListener(null)
  }

  private fun bindButtons() {
    upgradeButton.setOnDebouncedClickListener { callback.onUpgradeClicked() }
    laterButton.setOnDebouncedClickListener { callback.onCancelClicked() }
  }

  interface Callback {

    fun onUpgradeClicked()

    fun onCancelClicked()
  }
}