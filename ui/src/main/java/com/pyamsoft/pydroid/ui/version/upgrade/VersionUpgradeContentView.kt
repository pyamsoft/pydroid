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
import androidx.annotation.CheckResult
import androidx.annotation.StringRes
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.arch.UiView
import com.pyamsoft.pydroid.ui.databinding.VersionUpgradeContentBinding

internal class VersionUpgradeContentView internal constructor(
  private val parent: ViewGroup,
  private val applicationName: String,
  private val currentVersion: Int,
  private val newVersion: Int
) : UiView {

  private lateinit var binding: VersionUpgradeContentBinding

  override fun id(): Int {
    return binding.layoutRoot.id
  }

  override fun inflate(savedInstanceState: Bundle?) {
    binding = VersionUpgradeContentBinding.inflate(parent.inflater(), parent, false)
    parent.addView(binding.root)

    setApplicationMessage()
    setVersions()
  }

  override fun saveState(outState: Bundle) {
  }

  @CheckResult
  private fun getString(@StringRes id: Int, vararg formatArgs: Any): String {
    return binding.root.context.getString(id, *formatArgs)
  }

  private fun setApplicationMessage() {
    binding.upgradeMessage.text = getString(R.string.upgrade_available_message, applicationName)
  }

  private fun setVersions() {
    binding.upgradeCurrentValue.text = "$currentVersion"
    binding.upgradeNewValue.text = "$newVersion"
  }

}