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
import com.pyamsoft.pydroid.core.bus.Publisher
import com.pyamsoft.pydroid.ui.arch.UiView
import com.pyamsoft.pydroid.ui.databinding.VersionUpgradeControlsBinding
import com.pyamsoft.pydroid.ui.util.setOnDebouncedClickListener
import com.pyamsoft.pydroid.ui.version.upgrade.VersionViewEvents.Cancel
import com.pyamsoft.pydroid.ui.version.upgrade.VersionViewEvents.Upgrade

class VersionUpgradeControlView internal constructor(
  private val parent: ViewGroup,
  private val bus: Publisher<VersionViewEvents>
) : UiView {

  private lateinit var binding: VersionUpgradeControlsBinding

  override fun id(): Int {
    return View.NO_ID
  }

  override fun inflate(savedInstanceState: Bundle?) {
    binding = VersionUpgradeControlsBinding.inflate(parent.inflater(), parent, false)
    parent.addView(binding.root)

    bindPositiveClick()
    bindNegativeClick()
  }

  override fun saveState(outState: Bundle) {
  }

  private fun bindPositiveClick() {
    binding.upgradeButton.setOnDebouncedClickListener { bus.publish(Upgrade) }
  }

  private fun bindNegativeClick() {
    binding.laterButton.setOnDebouncedClickListener { bus.publish(Cancel) }
  }

}