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

package com.pyamsoft.pydroid.ui.internal.settings.clear

import android.app.Dialog
import android.os.Bundle
import androidx.annotation.CheckResult
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.pyamsoft.pydroid.core.requireNotNull
import com.pyamsoft.pydroid.inject.Injector
import com.pyamsoft.pydroid.ui.PYDroidComponent

internal class SettingsClearConfigDialog : AppCompatDialogFragment() {

  internal var factory: ViewModelProvider.Factory? = null
  private val viewModel by activityViewModels<SettingsClearConfigViewModel> {
    factory.requireNotNull()
  }

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    Injector.obtainFromApplication<PYDroidComponent>(requireContext())
        .plusClearConfirm()
        .create()
        .inject(this)

    return AlertDialog.Builder(requireActivity())
        .setMessage(
            """
        Really reset all application settings?

        All saved data will be cleared and all settings reset to default.
        The app will act as if you are launching it for the first time.

        This cannot be undone.
            """.trimIndent())
        .setNegativeButton("Cancel") { _, _ -> dismiss() }
        .setPositiveButton("Reset") { _, _ -> viewModel.reset() }
        .create()
  }

  override fun onDestroyView() {
    super.onDestroyView()
    factory = null
  }

  companion object {

    internal const val TAG = "SettingsClearConfigDialog"

    @JvmStatic
    @CheckResult
    fun newInstance(): DialogFragment {
      return SettingsClearConfigDialog().apply { arguments = Bundle().apply {} }
    }
  }
}
