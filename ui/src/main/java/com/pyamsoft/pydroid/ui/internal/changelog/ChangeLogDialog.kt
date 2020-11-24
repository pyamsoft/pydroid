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

package com.pyamsoft.pydroid.ui.internal.changelog

import android.app.Dialog
import android.os.Bundle
import androidx.annotation.CheckResult
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentActivity
import com.pyamsoft.pydroid.ui.changelog.ChangeLogProvider
import com.pyamsoft.pydroid.ui.internal.dialog.FullscreenDialog
import com.pyamsoft.pydroid.ui.util.show

internal class ChangeLogDialog : FullscreenDialog() {

    @CheckResult
    private fun getChangelogProvider(): ChangeLogProvider {
        return requireActivity() as ChangeLogProvider
    }

    @CheckResult
    private fun getApplicationName(packageName: String): CharSequence {
        val pm = requireActivity().applicationContext.packageManager
        return pm.getApplicationInfo(packageName, 0).loadLabel(pm)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val provider = getChangelogProvider()
        return AlertDialog.Builder(requireActivity())
            .setTitle(getApplicationName(provider.changeLogPackageName))
            .setMessage(provider.changelog)
            .setPositiveButton("Close") { _, _ -> dismiss() }
            .create()
    }

    companion object {

        private const val TAG = "ChangeLogDialog"

        @JvmStatic
        fun open(activity: FragmentActivity) {
            ChangeLogDialog().apply {
                arguments = Bundle().apply { }
            }.show(activity, TAG)
        }

        @JvmStatic
        @CheckResult
        fun isNotShown(activity: FragmentActivity): Boolean {
            return activity.supportFragmentManager.findFragmentByTag(TAG) == null
        }
    }

}
