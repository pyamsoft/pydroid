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
import android.widget.LinearLayout
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.Unbinder
import com.pyamsoft.pydroid.core.bus.Publisher
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.R2
import com.pyamsoft.pydroid.ui.arch.UiView
import com.pyamsoft.pydroid.ui.util.setOnDebouncedClickListener
import com.pyamsoft.pydroid.ui.version.upgrade.VersionViewEvent.Cancel
import com.pyamsoft.pydroid.ui.version.upgrade.VersionViewEvent.Upgrade

internal class VersionUpgradeControlView internal constructor(
  private val parent: ViewGroup,
  bus: Publisher<VersionViewEvent>
) : UiView<VersionViewEvent>(bus) {

  private lateinit var unbinder: Unbinder
  @field:BindView(R2.id.layout_root) internal lateinit var layoutRoot: LinearLayout
  @field:BindView(R2.id.upgrade_button) internal lateinit var upgradeButton: Button
  @field:BindView(R2.id.later_button) internal lateinit var laterButton: Button

  override fun id(): Int {
    return layoutRoot.id
  }

  override fun inflate(savedInstanceState: Bundle?) {
    val root = parent.inflateAndAdd(R.layout.version_upgrade_controls)
    unbinder = ButterKnife.bind(this, root)

    bindPositiveClick()
    bindNegativeClick()
  }

  override fun teardown() {
    unbinder.unbind()
  }

  override fun saveState(outState: Bundle) {
  }

  private fun bindPositiveClick() {
    upgradeButton.setOnDebouncedClickListener { publish(Upgrade) }
  }

  private fun bindNegativeClick() {
    laterButton.setOnDebouncedClickListener { publish(Cancel) }
  }

}