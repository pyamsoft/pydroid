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

package com.pyamsoft.pydroid.ui.internal.about

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CheckResult
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.composethemeadapter.MdcTheme
import com.pyamsoft.pydroid.core.requireNotNull
import com.pyamsoft.pydroid.inject.Injector
import com.pyamsoft.pydroid.ui.PYDroidComponent
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.app.makeFullscreen
import com.pyamsoft.pydroid.ui.util.show
import com.pyamsoft.pydroid.util.hyperlink

internal class AboutDialog : AppCompatDialogFragment() {

  internal var factory: ViewModelProvider.Factory? = null
  private val viewModel by activityViewModels<AboutViewModel> { factory.requireNotNull() }

  override fun onCreateView(
      inflater: LayoutInflater,
      container: ViewGroup?,
      savedInstanceState: Bundle?
  ): View {
    val context = inflater.context

    Injector.obtainFromApplication<PYDroidComponent>(context).plusAbout().create().inject(this)

    return ComposeView(context).apply {
      id = R.id.about_fragment

      layoutParams =
          ViewGroup.LayoutParams(
              ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

      setContent {
        MdcTheme {
          val state by viewModel.compose()

          AboutScreen(
              state = state,
              onViewHomePage = { viewModel.handleOpenLibrary(it) },
              onViewLicense = { viewModel.handleOpenLicense(it) },
              onNavigationErrorDismissed = { viewModel.handleHideNavigationError() },
              onClose = { dismiss() })
        }
      }
    }
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    makeFullscreen()

    viewModel.bindController(viewLifecycleOwner) { event ->
      return@bindController when (event) {
        is AboutControllerEvent.OpenUrl -> handleOpenUrl(event.url)
      }
    }

    viewModel.handleLoadLicenses()
  }

  override fun onDestroyView() {
    super.onDestroyView()
    factory = null
    (view as? ComposeView)?.disposeComposition()
  }

  private fun handleOpenUrl(url: String) {
    url
        .hyperlink(requireActivity())
        .navigate()
        .onSuccess { viewModel.navigationSuccess() }
        .onFailure { viewModel.navigationFailed(it) }
  }

  companion object {

    private const val TAG = "AboutDialog"

    @JvmStatic
    @CheckResult
    private fun newInstance(): DialogFragment {
      return AboutDialog().apply { arguments = Bundle().apply {} }
    }

    @JvmStatic
    internal fun show(activity: FragmentActivity) {
      newInstance().show(activity, TAG)
    }
  }
}
