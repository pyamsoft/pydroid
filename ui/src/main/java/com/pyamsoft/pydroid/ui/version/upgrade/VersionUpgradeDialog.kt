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

import android.app.Dialog
import android.os.Bundle
import androidx.annotation.CheckResult
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ContextThemeWrapper
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.pyamsoft.pydroid.arch.StateSaver
import com.pyamsoft.pydroid.arch.createComponent
import com.pyamsoft.pydroid.ui.Injector
import com.pyamsoft.pydroid.ui.PYDroidComponent
import com.pyamsoft.pydroid.ui.app.dialog.ThemeDialog
import com.pyamsoft.pydroid.ui.arch.viewModelFactory
import com.pyamsoft.pydroid.ui.util.show

internal class VersionUpgradeDialog internal constructor() : ThemeDialog() {

    private var stateSaver: StateSaver? = null

    internal var factory: ViewModelProvider.Factory? = null
    private val viewModel by viewModelFactory<VersionUpgradeViewModel> { factory }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        Injector.obtain<PYDroidComponent>(requireContext().applicationContext)
            .plusVersionUpgrade()
            .inject(this)

        stateSaver = createComponent(savedInstanceState, viewLifecycleOwner, viewModel) {
            return@createComponent when (it) {
                is VersionUpgradeControllerEvent.FinishedUpgrade -> dismiss()
            }
        }

        return AlertDialog.Builder(ContextThemeWrapper(requireActivity(), theme), theme)
            .setTitle("Upgrade Available")
            .setMessage(
                """
                    |A new version has been downloaded!
                    |
                    |Click to restart the app and upgrade to the latest version!""".trimMargin()
            )
            .setNegativeButton("Cancel") { _, _ -> dismiss() }
            .setPositiveButton("Restart") { _, _ -> viewModel.completeUpgrade() }
            .create()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        stateSaver?.saveState(outState)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        stateSaver = null
        factory = null
    }

    companion object {

        private const val TAG = "VersionUpgradeDialog"

        @JvmStatic
        @CheckResult
        private fun newInstance(): DialogFragment {
            return VersionUpgradeDialog().apply {
                arguments = Bundle().apply { }
            }
        }

        @JvmStatic
        fun show(activity: FragmentActivity) {
            return newInstance().show(activity, TAG)
        }
    }
}
