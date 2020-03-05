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

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CheckResult
import androidx.annotation.StringRes
import com.pyamsoft.pydroid.arch.BindingUiView
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.databinding.VersionUpgradeContentBinding

internal class VersionUpgradeContentView internal constructor(
    parent: ViewGroup
) : BindingUiView<VersionUpgradeViewState, VersionUpgradeViewEvent, VersionUpgradeContentBinding>(
    parent
) {

    init {
        doOnTeardown {
            binding.upgradeMessage.text = ""
            binding.upgradeCurrentValue.text = ""
            binding.upgradeNewValue.text = ""
        }
    }

    override fun provideBindingInflater(): (LayoutInflater, ViewGroup) -> VersionUpgradeContentBinding {
        return VersionUpgradeContentBinding::inflate
    }

    override fun provideBindingRoot(binding: VersionUpgradeContentBinding): View {
        return binding.versionContentRoot
    }

    override fun onRender(state: VersionUpgradeViewState) {
        state.applicationName.let { name ->
            if (name.isNotBlank()) {
                binding.upgradeMessage.text = getString(R.string.upgrade_available_message, name)
            } else {
                binding.upgradeMessage.text = ""
            }
        }

        state.currentVersion.let { version ->
            if (version == 0) {
                binding.upgradeCurrentValue.text = ""
            } else {
                binding.upgradeCurrentValue.text = "$version"
            }
        }

        state.newVersion.let { version ->
            if (version == 0) {
                binding.upgradeNewValue.text = ""
            } else {
                binding.upgradeNewValue.text = "$version"
            }
        }
    }

    @CheckResult
    private fun getString(@StringRes id: Int, vararg formatArgs: Any): String {
        return layoutRoot.context.getString(id, *formatArgs)
    }
}
