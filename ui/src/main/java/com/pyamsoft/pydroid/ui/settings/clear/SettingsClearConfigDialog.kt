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
 *
 */

package com.pyamsoft.pydroid.ui.settings.clear

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CheckResult
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.pyamsoft.pydroid.arch.StateSaver
import com.pyamsoft.pydroid.arch.createComponent
import com.pyamsoft.pydroid.ui.Injector
import com.pyamsoft.pydroid.ui.PYDroidComponent
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.app.dialog.ThemeDialog
import com.pyamsoft.pydroid.ui.arch.viewModelFactory
import com.pyamsoft.pydroid.ui.databinding.LayoutLinearVerticalBinding
import com.pyamsoft.pydroid.ui.settings.clear.SettingsClearConfigControllerEvent.CancelPrompt

internal class SettingsClearConfigDialog : ThemeDialog() {

    private var stateSaver: StateSaver? = null
    internal var message: SettingsClearConfigMessage? = null
    internal var actions: SettingsClearConfigActions? = null

    internal var factory: ViewModelProvider.Factory? = null
    private val viewModel by viewModelFactory<SettingsClearConfigViewModel>(activity = true) { factory }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.layout_linear_vertical, container, false)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        val binding = LayoutLinearVerticalBinding.bind(view)
        Injector.obtain<PYDroidComponent>(view.context.applicationContext)
            .plusClearConfirmDialog()
            .create(binding.layoutLinearV)
            .inject(this)

        stateSaver = createComponent(
            savedInstanceState, viewLifecycleOwner,
            viewModel,
            requireNotNull(message),
            requireNotNull(actions)
        ) {
            return@createComponent when (it) {
                is CancelPrompt -> dismiss()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        message = null
        actions = null
        factory = null
        stateSaver = null
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        stateSaver?.saveState(outState)
    }

    override fun onResume() {
        super.onResume()
        // The dialog is super small for some reason. We have to set the size manually, in onResume
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    companion object {

        internal const val TAG = "SettingsClearConfigDialog"

        @JvmStatic
        @CheckResult
        fun newInstance(): DialogFragment {
            return SettingsClearConfigDialog().apply {
                arguments = Bundle().apply {}
            }
        }
    }
}

