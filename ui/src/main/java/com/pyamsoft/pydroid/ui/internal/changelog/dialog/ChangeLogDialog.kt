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

package com.pyamsoft.pydroid.ui.internal.changelog.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CheckResult
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.pyamsoft.pydroid.arch.StateSaver
import com.pyamsoft.pydroid.arch.createComponent
import com.pyamsoft.pydroid.ui.Injector
import com.pyamsoft.pydroid.ui.PYDroidComponent
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.arch.viewModelFactory
import com.pyamsoft.pydroid.ui.databinding.LayoutLinearVerticalBinding
import com.pyamsoft.pydroid.ui.internal.changelog.ChangeLogProvider
import com.pyamsoft.pydroid.ui.internal.dialog.FullscreenDialog
import com.pyamsoft.pydroid.ui.util.show

internal class ChangeLogDialog : FullscreenDialog() {

    private var stateSaver: StateSaver? = null

    internal var listView: ChangeLogList? = null
    internal var nameView: ChangeLogName? = null
    internal var closeView: ChangeLogClose? = null
    internal var iconView: ChangeLogIcon? = null

    internal var factory: ViewModelProvider.Factory? = null
    private val viewModel by viewModelFactory<ChangeLogDialogViewModel>(activity = true) { factory }

    @CheckResult
    private fun getChangelogProvider(): ChangeLogProvider {
        return requireActivity() as ChangeLogProvider
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

        val binding = LayoutLinearVerticalBinding.bind(view)
        Injector.obtain<PYDroidComponent>(view.context.applicationContext)
            .plusChangeLogDialog()
            .create(binding.layoutLinearV, getChangelogProvider())
            .inject(this)

        stateSaver = createComponent(
            savedInstanceState, viewLifecycleOwner,
            viewModel,
            requireNotNull(iconView),
            requireNotNull(nameView),
            requireNotNull(listView),
            requireNotNull(closeView)
        ) {
            return@createComponent when (it) {
                is ChangeLogDialogControllerEvent.Close -> dismiss()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        factory = null
        stateSaver = null

        listView = null
        nameView = null
        iconView = null
        closeView = null
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        stateSaver?.saveState(outState)
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
