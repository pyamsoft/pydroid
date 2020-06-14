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

package com.pyamsoft.pydroid.ui.rating.dialog

import android.app.Dialog
import android.os.Bundle
import android.text.SpannedString
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
import com.pyamsoft.pydroid.ui.app.noTitle
import com.pyamsoft.pydroid.ui.arch.viewModelFactory
import com.pyamsoft.pydroid.ui.databinding.LayoutLinearVerticalBinding
import com.pyamsoft.pydroid.ui.rating.ChangeLogProvider
import com.pyamsoft.pydroid.ui.rating.dialog.RatingDialogControllerEvent.CancelDialog
import com.pyamsoft.pydroid.ui.rating.dialog.RatingDialogControllerEvent.NavigateRating
import com.pyamsoft.pydroid.ui.util.MarketLinker
import com.pyamsoft.pydroid.util.valueFromCurrentTheme
import kotlin.LazyThreadSafetyMode.NONE

class RatingDialog : DialogFragment() {

    private var stateSaver: StateSaver? = null
    internal var changelogView: RatingChangelogView? = null
    internal var controlsView: RatingControlsView? = null
    internal var iconView: RatingIconView? = null

    internal var factory: ViewModelProvider.Factory? = null
    private val viewModel by viewModelFactory<RatingDialogViewModel>(activity = true) { factory }

    private val themeFromAttrs: Int by lazy(NONE) {
        requireActivity().valueFromCurrentTheme(R.attr.dialogTheme)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = false
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
        return inflater.inflate(R.layout.layout_linear_vertical, container, false)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        val rateLink = requireArguments().getString(RATE_LINK, "")
        val changeLogIcon = requireArguments().getInt(CHANGE_LOG_ICON, 0)
        val changelog = requireArguments().getCharSequence(CHANGE_LOG_TEXT, "") as? SpannedString
        requireNotNull(rateLink)
        requireNotNull(changelog)
        require(rateLink.isNotBlank())
        require(changeLogIcon > 0)
        require(changelog.isNotBlank())

        val binding = LayoutLinearVerticalBinding.bind(view)
        Injector.obtain<PYDroidComponent>(view.context.applicationContext)
            .plusRatingDialog()
            .create(binding.layoutLinearV, viewLifecycleOwner, rateLink)
            .inject(this)

        stateSaver = createComponent(
            savedInstanceState, viewLifecycleOwner,
            viewModel,
            requireNotNull(iconView),
            requireNotNull(changelogView),
            requireNotNull(controlsView)
        ) {
            return@createComponent when (it) {
                is NavigateRating -> navigateToApplicationPage(it.link)
                is CancelDialog -> dismiss()
            }
        }

        viewModel.initialize(changelog, changeLogIcon)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        changelogView = null
        controlsView = null
        iconView = null
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

    private fun navigateToApplicationPage(link: String) {
        val error = MarketLinker.linkToMarketPage(requireContext(), link)

        if (error != null) {
            viewModel.navigationFailed(error)
        } else {
            viewModel.navigationSuccess()
            dismiss()
        }
    }

    companion object {

        internal const val TAG = "RatingDialog"
        private const val CHANGE_LOG_TEXT = "change_log_text"
        private const val CHANGE_LOG_ICON = "change_log_icon"
        private const val RATE_LINK = "rate_link"

        @JvmStatic
        @CheckResult
        fun newInstance(provider: ChangeLogProvider): DialogFragment {
            return RatingDialog().apply {
                arguments = Bundle().apply {
                    putString(RATE_LINK, provider.changeLogPackageName)
                    putCharSequence(CHANGE_LOG_TEXT, provider.changelog)
                    putInt(CHANGE_LOG_ICON, provider.applicationIcon)
                }
            }
        }
    }
}
