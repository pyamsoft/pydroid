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

import android.view.ViewGroup
import com.pyamsoft.pydroid.core.bus.Publisher
import com.pyamsoft.pydroid.ui.arch.UiView
import com.pyamsoft.pydroid.ui.databinding.DialogVersionUpgradeControlsBinding
import com.pyamsoft.pydroid.ui.util.setOnDebouncedClickListener
import com.pyamsoft.pydroid.ui.version.upgrade.VersionUpgradeViewEvents.Cancel
import com.pyamsoft.pydroid.ui.version.upgrade.VersionUpgradeViewEvents.Upgrade

class VersionUpgradeControlView internal constructor(
  private val parent: ViewGroup,
  private val bus: Publisher<VersionUpgradeViewEvents>
) : UiView {

  private lateinit var binding: DialogVersionUpgradeControlsBinding

  override fun inflate() {
    binding = DialogVersionUpgradeControlsBinding.inflate(parent.inflater(), parent, false)
    parent.addView(binding.root)

    bindPositiveClick()
    bindNegativeClick()
  }

  private fun bindPositiveClick() {
    binding.upgradeButton.setOnDebouncedClickListener { bus.publish(Upgrade) }
  }

  private fun bindNegativeClick() {
    binding.laterButton.setOnDebouncedClickListener { bus.publish(Cancel) }
  }

}