/*
 * Copyright 2020 Peter Kenji Yamanaka
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
 */

package com.pyamsoft.pydroid.ui.version.upgrade

import android.view.ViewGroup
import androidx.annotation.CheckResult
import androidx.annotation.StringRes
import com.pyamsoft.pydroid.arch.BaseUiView
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.databinding.VersionUpgradeContentBinding

internal class VersionUpgradeContentView internal constructor(
    parent: ViewGroup
) : BaseUiView<VersionUpgradeViewState, VersionUpgradeViewEvent, VersionUpgradeContentBinding>(
    parent
) {

    override val viewBinding = VersionUpgradeContentBinding::inflate

    override val layoutRoot by boundView { versionContentRoot }

    init {
        doOnTeardown {
            binding.upgradeMessage.text = ""
            binding.upgradeCurrentValue.text = ""
            binding.upgradeNewValue.text = ""
        }
    }

    override fun onRender(state: VersionUpgradeViewState) {
        handleName(state)
        handleCurrentVersion(state)
        handleNewVersion(state)
    }

    private fun handleNewVersion(state: VersionUpgradeViewState) {
        state.newVersion.let { version ->
            if (version == 0) {
                binding.upgradeNewValue.text = ""
            } else {
                binding.upgradeNewValue.text = "$version"
            }
        }
    }

    private fun handleCurrentVersion(state: VersionUpgradeViewState) {
        state.currentVersion.let { version ->
            if (version == 0) {
                binding.upgradeCurrentValue.text = ""
            } else {
                binding.upgradeCurrentValue.text = "$version"
            }
        }
    }

    private fun handleName(state: VersionUpgradeViewState) {
        state.applicationName.let { name ->
            if (name.isNotBlank()) {
                binding.upgradeMessage.text = getString(R.string.upgrade_available_message, name)
            } else {
                binding.upgradeMessage.text = ""
            }
        }
    }

    @CheckResult
    private fun getString(@StringRes id: Int, vararg formatArgs: Any): String {
        return layoutRoot.context.getString(id, *formatArgs)
    }
}
