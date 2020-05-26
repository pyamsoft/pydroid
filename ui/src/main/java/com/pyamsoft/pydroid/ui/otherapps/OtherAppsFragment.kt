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

package com.pyamsoft.pydroid.ui.otherapps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CheckResult
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.pyamsoft.pydroid.arch.StateSaver
import com.pyamsoft.pydroid.arch.createComponent
import com.pyamsoft.pydroid.ui.Injector
import com.pyamsoft.pydroid.ui.PYDroidComponent
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.arch.viewModelFactory
import com.pyamsoft.pydroid.ui.databinding.LayoutFrameBinding
import com.pyamsoft.pydroid.util.hyperlink

internal class OtherAppsFragment : Fragment() {

    private var stateSaver: StateSaver? = null
    internal var listView: OtherAppsList? = null

    internal var factory: ViewModelProvider.Factory? = null
    private val viewModel by viewModelFactory<OtherAppsViewModel>(activity = true) { factory }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.layout_frame, container, false)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        val binding = LayoutFrameBinding.bind(view)
        Injector.obtain<PYDroidComponent>(view.context.applicationContext)
            .plusOtherApps()
            .create(binding.layoutFrame, viewLifecycleOwner)
            .inject(this)

        stateSaver = createComponent(
            savedInstanceState, viewLifecycleOwner,
            viewModel,
            requireNotNull(listView)
        ) {
            return@createComponent when (it) {
                is OtherAppsControllerEvent.ExternalUrl -> navigateToExternalUrl(it.url)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        listView = null
        factory = null
        stateSaver = null
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        stateSaver?.saveState(outState)
    }

    private fun navigateToExternalUrl(url: String) {
        val error = url.hyperlink(requireActivity())
            .navigate()
        if (error == null) {
            viewModel.navigationSuccess()
        } else {
            viewModel.navigationFailed(error)
        }
    }

    companion object {

        @JvmStatic
        @CheckResult
        internal fun newInstance(): Fragment {
            return OtherAppsFragment().apply {
                arguments = Bundle().apply {}
            }
        }
    }
}
