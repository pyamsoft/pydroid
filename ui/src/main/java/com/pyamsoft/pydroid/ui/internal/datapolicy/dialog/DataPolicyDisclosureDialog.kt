/*
 * Copyright 2021 Peter Kenji Yamanaka
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

package com.pyamsoft.pydroid.ui.internal.datapolicy.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CheckResult
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import coil.ImageLoader
import com.pyamsoft.pydroid.core.Logger
import com.pyamsoft.pydroid.core.ResultWrapper
import com.pyamsoft.pydroid.core.requireNotNull
import com.pyamsoft.pydroid.inject.Injector
import com.pyamsoft.pydroid.ui.PYDroidComponent
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.app.ComposeTheme
import com.pyamsoft.pydroid.ui.app.makeFullWidth
import com.pyamsoft.pydroid.ui.internal.app.AppProvider
import com.pyamsoft.pydroid.ui.internal.app.NoopTheme
import com.pyamsoft.pydroid.ui.util.show
import com.pyamsoft.pydroid.util.hyperlink

internal class DataPolicyDisclosureDialog : AppCompatDialogFragment() {

  /** May be provided by PYDroid, otherwise this is just a noop */
  internal var composeTheme: ComposeTheme = NoopTheme

  internal var factory: ViewModelProvider.Factory? = null
  private val viewModel by
      activityViewModels<DataPolicyDialogViewModel> { factory.requireNotNull() }

  internal var imageLoader: ImageLoader? = null

  @CheckResult
  private fun getAppProvider(): AppProvider {
    return requireActivity() as AppProvider
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    // The DPD is not cancellable except by the button interaction
    isCancelable = false
  }

  override fun onCreateView(
      inflater: LayoutInflater,
      container: ViewGroup?,
      savedInstanceState: Bundle?
  ): View {
    val act = requireActivity()
    Injector.obtainFromApplication<PYDroidComponent>(act)
        .plusDataPolicyDialog()
        .create(getAppProvider())
        .inject(this)

    return ComposeView(act).apply {
      id = R.id.dialog_dpd

      setContent {
        val state by viewModel.compose()

        composeTheme(act) {
          DataPolicyDisclosureScreen(
              state = state,
              imageLoader = imageLoader.requireNotNull(),
              onNavigationErrorDismissed = { viewModel.handleHideNavigation() },
              onAccept = { viewModel.handleAccept() },
              onReject = { viewModel.handleReject() },
              onPrivacyPolicyClicked = { viewModel.handleViewPrivacyPolicy() },
              onTermsOfServiceClicked = { viewModel.handleViewTermsOfService() },
              onUrlClicked = { viewModel.handleOpenUrl(it) },
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
        is DataPolicyDialogControllerEvent.AcceptPolicy -> dismiss()
        is DataPolicyDialogControllerEvent.RejectPolicy -> requireActivity().finish()
        is DataPolicyDialogControllerEvent.OpenUrl -> navigateToExternalUrl(event.url)
      }
    }
  }

  override fun onDestroyView() {
    super.onDestroyView()
    (view as? ComposeView)?.disposeComposition()

    factory = null
    imageLoader = null
  }

  private fun ResultWrapper<Unit>.handleNavigation() {
    this.onSuccess { viewModel.handleNavigationSuccess() }.onFailure {
      viewModel.handleNavigationFailed(it)
    }
  }

  private fun navigateToExternalUrl(url: String) {
    url.hyperlink(requireActivity()).navigate().handleNavigation()
  }

  companion object {

    private const val TAG = "DataPolicyDisclosureDialog"

    @JvmStatic
    internal fun show(activity: FragmentActivity) {
      if (activity.supportFragmentManager.findFragmentByTag(TAG) == null) {
        DataPolicyDisclosureDialog().apply { arguments = Bundle().apply {} }.show(activity, TAG)
      } else {
        Logger.w("DPD Dialog already shown")
      }
    }
  }
}
