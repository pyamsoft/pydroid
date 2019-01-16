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
import com.pyamsoft.pydroid.core.bus.Publisher
import com.pyamsoft.pydroid.ui.arch.UiView
import com.pyamsoft.pydroid.ui.databinding.VersionUpgradeControlsBinding
import com.pyamsoft.pydroid.ui.util.setOnDebouncedClickListener
import com.pyamsoft.pydroid.ui.version.upgrade.VersionViewEvent.Cancel
import com.pyamsoft.pydroid.ui.version.upgrade.VersionViewEvent.Upgrade

internal class VersionUpgradeControlView internal constructor(
  private val parent: ViewGroup,
  bus: Publisher<VersionViewEvent>
) : UiView<VersionViewEvent>(bus) {

  private lateinit var binding: VersionUpgradeControlsBinding

  override fun id(): Int {
    return binding.versionControlRoot.id
  }

  override fun inflate(savedInstanceState: Bundle?) {
    binding = VersionUpgradeControlsBinding.inflate(parent.inflater(), parent, true)

    bindPositiveClick()
    bindNegativeClick()
  }

  override fun teardown() {
    binding.unbind()
  }

  override fun saveState(outState: Bundle) {
  }

  private fun bindPositiveClick() {
    binding.upgradeButton.setOnDebouncedClickListener { publish(Upgrade) }
  }

  private fun bindNegativeClick() {
    binding.laterButton.setOnDebouncedClickListener { publish(Cancel) }
  }

}