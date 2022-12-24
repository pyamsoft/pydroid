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

package com.pyamsoft.pydroid.ui.internal.about

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.pyamsoft.pydroid.bootstrap.libraries.OssLibrary
import com.pyamsoft.pydroid.core.requireNotNull
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.app.makeFullscreen
import com.pyamsoft.pydroid.ui.internal.app.ComposeTheme
import com.pyamsoft.pydroid.ui.internal.app.NoopTheme
import com.pyamsoft.pydroid.ui.internal.app.invoke
import com.pyamsoft.pydroid.ui.internal.pydroid.ObjectGraph
import com.pyamsoft.pydroid.ui.util.dispose
import com.pyamsoft.pydroid.ui.util.recompose
import com.pyamsoft.pydroid.ui.util.show

internal class AboutDialog : AppCompatDialogFragment() {

  /** May be provided by PYDroid, otherwise this is just a noop */
  internal var composeTheme: ComposeTheme = NoopTheme

  /** Provided by PYDroid */
  internal var viewModel: AboutViewModeler? = null

  private fun openLicense(handler: UriHandler, library: OssLibrary) {
    openPage(
        handler = handler,
        url = library.libraryUrl,
    )
  }

  private fun openLibrary(handler: UriHandler, library: OssLibrary) {
    openPage(
        handler = handler,
        url = library.libraryUrl,
    )
  }

  private fun handleConfigurationChanged() {
    makeFullscreen()
    recompose()
  }

  private fun openPage(handler: UriHandler, url: String) {
    val vm = viewModel.requireNotNull()

    try {
      vm.handleDismissFailedNavigation()
      handler.openUri(url)
    } catch (e: Throwable) {
      vm.handleFailedNavigation(e)
    }
  }

  override fun onCreateView(
      inflater: LayoutInflater,
      container: ViewGroup?,
      savedInstanceState: Bundle?
  ): View {
    val act = requireActivity()

    ObjectGraph.ApplicationScope.retrieve(act.application)
        .injector()
        .plusAbout()
        .create()
        .inject(this)
    val vm = viewModel.requireNotNull()

    return ComposeView(act).apply {
      id = R.id.dialog_about

      setContent {
        val handler = LocalUriHandler.current

        val handleViewHomePage by rememberUpdatedState { library: OssLibrary ->
          openLibrary(handler, library)
        }

        val handleViewLicense by rememberUpdatedState { library: OssLibrary ->
          openLicense(handler, library)
        }

        val handleDismissNavigationError by rememberUpdatedState {
          vm.handleDismissFailedNavigation()
        }

        val handleDismiss by rememberUpdatedState { dismiss() }

        composeTheme(act) {
          AboutScreen(
              state = vm.state(),
              onViewHomePage = handleViewHomePage,
              onViewLicense = handleViewLicense,
              onNavigationErrorDismissed = handleDismissNavigationError,
              onClose = handleDismiss,
          )
        }
      }
    }
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    makeFullscreen()

    viewModel.requireNotNull().also { vm ->
      vm.restoreState(savedInstanceState)
      vm.handleLoadLicenses(scope = viewLifecycleOwner.lifecycleScope)
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
  }

  companion object {

    private const val TAG = "AboutDialog"

    @JvmStatic
    internal fun show(activity: FragmentActivity) {
      AboutDialog().apply { arguments = Bundle().apply {} }.show(activity, TAG)
    }
  }
}
