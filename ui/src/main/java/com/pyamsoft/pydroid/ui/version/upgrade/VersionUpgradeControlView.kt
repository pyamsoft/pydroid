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
import android.view.ViewGroup
import android.widget.Button
import com.pyamsoft.pydroid.core.bus.Publisher
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.arch.UiView
import com.pyamsoft.pydroid.ui.util.setOnDebouncedClickListener
import com.pyamsoft.pydroid.ui.version.upgrade.VersionUpgradeViewEvent.Cancel
import com.pyamsoft.pydroid.ui.version.upgrade.VersionUpgradeViewEvent.Upgrade

internal class VersionUpgradeControlView internal constructor(
  private val parent: ViewGroup,
  bus: Publisher<VersionUpgradeViewEvent>
) : UiView<VersionUpgradeViewEvent>(bus) {

  private lateinit var layoutRoot: ViewGroup
  private lateinit var upgradeButton: Button
  private lateinit var laterButton: Button

  override fun id(): Int {
    return layoutRoot.id
  }

  override fun inflate(savedInstanceState: Bundle?) {
    parent.inflateAndAdd(R.layout.version_upgrade_controls) {
      layoutRoot = findViewById(R.id.version_control_root)
      upgradeButton = findViewById(R.id.upgrade_button)
      laterButton = findViewById(R.id.later_button)
    }

    bindButtons()
  }

  override fun teardown() {
    upgradeButton.setOnClickListener(null)
    laterButton.setOnClickListener(null)
  }

  override fun saveState(outState: Bundle) {
  }

  private fun bindButtons() {
    upgradeButton.setOnDebouncedClickListener { publish(Upgrade) }
    laterButton.setOnDebouncedClickListener { publish(Cancel) }
  }

}