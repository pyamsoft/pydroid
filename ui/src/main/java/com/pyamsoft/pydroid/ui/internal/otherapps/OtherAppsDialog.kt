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

package com.pyamsoft.pydroid.ui.internal.otherapps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.composethemeadapter.MdcTheme
import com.pyamsoft.pydroid.core.ResultWrapper
import com.pyamsoft.pydroid.core.requireNotNull
import com.pyamsoft.pydroid.inject.Injector
import com.pyamsoft.pydroid.ui.PYDroidComponent
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.app.makeFullWidth
import com.pyamsoft.pydroid.ui.util.show
import com.pyamsoft.pydroid.util.MarketLinker
import com.pyamsoft.pydroid.util.hyperlink

internal class OtherAppsDialog : AppCompatDialogFragment() {

  internal var factory: ViewModelProvider.Factory? = null
  private val viewModel by activityViewModels<OtherAppsViewModel> { factory.requireNotNull() }

  override fun onCreateView(
      inflater: LayoutInflater,
      container: ViewGroup?,
      savedInstanceState: Bundle?
  ): View {
    val act = requireActivity()
    Injector.obtainFromApplication<PYDroidComponent>(act).plusOtherApps().create().inject(this)

    return ComposeView(act).apply {
      id = R.id.dialog_otherapps

      setContent {
        MdcTheme {
          val state by viewModel.compose()

          OtherAppsScreen(
              state = state,
              onNavigationErrorDismissed = { viewModel.handleHideNavigation() },
              onViewStorePage = { viewModel.handleOpenStoreUrl(it) },
              onViewSourceCode = { viewModel.handleOpenSourceCodeUrl(it) },
              onClose = { dismiss() },
          )
        }
      }
    }
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    makeFullWidth()

    viewModel.bindController(viewLifecycleOwner) { event ->
      return@bindController when (event) {
        is OtherAppsControllerEvent.LaunchFallback -> openDeveloperPage()
        is OtherAppsControllerEvent.OpenUrl -> navigateToExternalUrl(event.url)
      }
    }
  }

  override fun onDestroyView() {
    super.onDestroyView()
    factory = null
  }

  private fun ResultWrapper<Unit>.handleNavigation() {
    this.onSuccess { viewModel.handleNavigationSuccess() }.onFailure {
      viewModel.handleNavigationFailed(it)
    }
  }

  private fun openDeveloperPage() {
    // If we cannot load we have nothing to do here
    MarketLinker.linkToDeveloperPage(requireActivity()).handleNavigation()
  }

  private fun navigateToExternalUrl(url: String) {
    url.hyperlink(requireActivity()).navigate().handleNavigation()
  }

  companion object {

    private const val TAG = "OtherAppsDialog"

    @JvmStatic
    internal fun show(activity: FragmentActivity) {
      OtherAppsDialog().apply { arguments = Bundle().apply {} }.show(activity, TAG)
    }
  }
}
