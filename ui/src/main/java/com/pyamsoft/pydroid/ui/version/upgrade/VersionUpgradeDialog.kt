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

package com.pyamsoft.pydroid.ui.version.upgrade

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CheckResult
import androidx.appcompat.view.ContextThemeWrapper
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.pyamsoft.pydroid.arch.StateSaver
import com.pyamsoft.pydroid.arch.createComponent
import com.pyamsoft.pydroid.ui.Injector
import com.pyamsoft.pydroid.ui.PYDroidComponent
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.R.layout
import com.pyamsoft.pydroid.ui.app.noTitle
import com.pyamsoft.pydroid.ui.arch.viewModelFactory
import com.pyamsoft.pydroid.ui.databinding.LayoutLinearVerticalBinding
import com.pyamsoft.pydroid.ui.util.MarketLinker
import com.pyamsoft.pydroid.ui.version.upgrade.VersionUpgradeControllerEvent.CancelDialog
import com.pyamsoft.pydroid.ui.version.upgrade.VersionUpgradeControllerEvent.OpenMarket
import com.pyamsoft.pydroid.util.valueFromCurrentTheme
import kotlin.LazyThreadSafetyMode.NONE

class VersionUpgradeDialog : DialogFragment() {

    private var stateSaver: StateSaver? = null
    internal var content: VersionUpgradeContentView? = null
    internal var control: VersionUpgradeControlView? = null

    internal var factory: ViewModelProvider.Factory? = null
    private val viewModel by viewModelFactory<VersionUpgradeViewModel>(activity = true) { factory }

    private val themeFromAttrs: Int by lazy(NONE) {
        requireActivity().valueFromCurrentTheme(R.attr.dialogTheme)
    }

    override fun getTheme(): Int {
        return themeFromAttrs
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(ContextThemeWrapper(requireActivity(), theme), theme).noTitle()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(layout.layout_linear_vertical, container, false)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        val latestVersion = requireArguments().getInt(KEY_LATEST_VERSION, 0)
        require(latestVersion > 0)
        val binding = LayoutLinearVerticalBinding.bind(view)
        Injector.obtain<PYDroidComponent>(view.context.applicationContext)
            .plusUpgrade()
            .create(binding.layoutLinearV, viewLifecycleOwner)
            .inject(this)

        stateSaver = createComponent(
            savedInstanceState, viewLifecycleOwner,
            viewModel,
            requireNotNull(content),
            requireNotNull(control)
        ) {
            return@createComponent when (it) {
                is OpenMarket -> navigateToMarket()
                is CancelDialog -> dismiss()
            }
        }

        viewModel.initialize(latestVersion)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        content = null
        control = null
        factory = null
        stateSaver = null
    }

    override fun onResume() {
        super.onResume()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        stateSaver?.saveState(outState)
    }

    private fun navigateToMarket() {
        val error = MarketLinker.linkToMarketPage(requireContext(), requireContext().packageName)
        if (error != null) {
            viewModel.navigationFailed(error)
        } else {
            viewModel.navigationSuccess()
            dismiss()
        }
    }

    companion object {

        internal const val TAG = "VersionUpgradeDialog"
        private const val KEY_LATEST_VERSION = "key_latest_version"

        @JvmStatic
        @CheckResult
        fun newInstance(latestVersion: Int): DialogFragment {
            return VersionUpgradeDialog().apply {
                arguments = Bundle().apply {
                    putInt(KEY_LATEST_VERSION, latestVersion)
                }
            }
        }
    }
}
