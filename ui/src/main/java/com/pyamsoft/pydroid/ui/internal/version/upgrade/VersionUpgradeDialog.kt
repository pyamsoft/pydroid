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

package com.pyamsoft.pydroid.ui.internal.version.upgrade

import android.app.Dialog
import android.os.Bundle
import androidx.annotation.CheckResult
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.pyamsoft.pydroid.ui.Injector
import com.pyamsoft.pydroid.ui.arch.fromViewModelFactory
import com.pyamsoft.pydroid.ui.internal.version.VersionCheckComponent
import com.pyamsoft.pydroid.ui.util.show
import timber.log.Timber

internal class VersionUpgradeDialog internal constructor() : AppCompatDialogFragment() {

    internal var factory: ViewModelProvider.Factory? = null
    private val viewModel by fromViewModelFactory<VersionUpgradeViewModel>(activity = true) { factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = false
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        Injector.obtainFromActivity<VersionCheckComponent>(requireActivity())
            .inject(this)

        return AlertDialog.Builder(requireActivity())
            .setTitle("Upgrade Available")
            .setMessage(
                """
                    |A new version has been downloaded!
                    |
                    |Click to restart the app and upgrade to the latest version!""".trimMargin()
            )
            .setNegativeButton("Later") { _, _ -> dismiss() }
            .setPositiveButton("Restart") { _, _ ->
                viewModel.completeUpgrade {
                    Timber.d("Upgrade completed, dismiss")
                    dismiss()
                }
            }
            .setCancelable(false)
            .create()
    }

    override fun onDestroy() {
        super.onDestroy()

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
