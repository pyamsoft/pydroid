/*
 * Copyright 2022 Peter Kenji Yamanaka
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

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CheckResult
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import coil.ImageLoader
import com.pyamsoft.pydroid.core.Logger
import com.pyamsoft.pydroid.core.requireNotNull
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.app.makeFullWidth
import com.pyamsoft.pydroid.ui.app.AppProvider
import com.pyamsoft.pydroid.ui.internal.app.ComposeTheme
import com.pyamsoft.pydroid.ui.internal.app.NoopTheme
import com.pyamsoft.pydroid.ui.internal.app.invoke
import com.pyamsoft.pydroid.ui.internal.pydroid.PYDroidApplicationInstallTracker
import com.pyamsoft.pydroid.ui.util.dispose
import com.pyamsoft.pydroid.ui.util.recompose
import com.pyamsoft.pydroid.ui.util.show

internal class DataPolicyDisclosureDialog : AppCompatDialogFragment() {

  /** May be provided by PYDroid, otherwise this is just a noop */
  internal var composeTheme: ComposeTheme = NoopTheme

  internal var viewModel: DataPolicyDialogViewModeler? = null

  internal var imageLoader: ImageLoader? = null

  @CheckResult
  private fun getAppProvider(): AppProvider {
    return requireActivity() as AppProvider
  }

  private fun openPage(handler: UriHandler, url: String) {
    val vm = viewModel.requireNotNull()

    try {
      vm.handleHideNavigationError()
      handler.openUri(url)
    } catch (e: Throwable) {
      vm.handleNavigationFailed(e)
    }
  }

  private fun handleAcceptDataPolicy() {
    viewModel
        .requireNotNull()
        .handleAccept(
            scope = viewLifecycleOwner.lifecycleScope,
            onAccepted = { dismiss() },
        )
  }

  private fun handleRejectDataPolicy() {
    viewModel
        .requireNotNull()
        .handleReject(
            scope = viewLifecycleOwner.lifecycleScope,
            onRejected = { requireActivity().finish() },
        )
  }

  private fun handleViewPrivacy(handler: UriHandler) {
    viewModel.requireNotNull().handleViewPrivacyPolicy { url ->
      openPage(
          handler = handler,
          url = url,
      )
    }
  }

  private fun handleViewTos(handler: UriHandler) {
    viewModel.requireNotNull().handleViewTermsOfService { url ->
      openPage(
          handler = handler,
          url = url,
      )
    }
  }

  private fun handleConfigurationChanged() {
    makeFullWidth()
    recompose()
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
    PYDroidApplicationInstallTracker.retrieve(act.application)
        .injector()
        .plusDataPolicyDialog()
        .create(getAppProvider())
        .inject(this)

    return ComposeView(act).apply {
      id = R.id.dialog_dpd

      val vm = viewModel.requireNotNull()
      val imageLoader = imageLoader.requireNotNull()
      setContent {
        val handler = LocalUriHandler.current

        composeTheme(act) {
          DataPolicyDisclosureScreen(
              state = vm.state(),
              imageLoader = imageLoader.requireNotNull(),
              onNavigationErrorDismissed = { vm.handleHideNavigationError() },
              onAccept = { handleAcceptDataPolicy() },
              onReject = { handleRejectDataPolicy() },
              onPrivacyPolicyClicked = { handleViewPrivacy(handler) },
              onTermsOfServiceClicked = { handleViewTos(handler) },
          )
        }
      }
    }
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    makeFullWidth()

    viewModel.requireNotNull().also { vm ->
      vm.restoreState(savedInstanceState)
      vm.bind(scope = viewLifecycleOwner.lifecycleScope)
    }
  }

  override fun onConfigurationChanged(newConfig: Configuration) {
    super.onConfigurationChanged(newConfig)
    handleConfigurationChanged()
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    viewModel?.saveState(outState)
  }

  override fun onDestroyView() {
    super.onDestroyView()
    dispose()

    viewModel = null
    imageLoader = null
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
