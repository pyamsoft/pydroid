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
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.pyamsoft.pydroid.bootstrap.libraries.OssLibrary
import com.pyamsoft.pydroid.core.requireNotNull
import com.pyamsoft.pydroid.inject.Injector
import com.pyamsoft.pydroid.ui.PYDroidComponent
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.app.ComposeTheme
import com.pyamsoft.pydroid.ui.app.makeFullscreen
import com.pyamsoft.pydroid.ui.internal.app.NoopTheme
import com.pyamsoft.pydroid.ui.util.show

internal class AboutDialog : AppCompatDialogFragment() {

  /** May be provided by PYDroid, otherwise this is just a noop */
  internal var composeTheme: ComposeTheme = NoopTheme

  /** Provided by PYDroid */
  internal var viewModel: AboutViewModel? = null

  override fun onCreateView(
      inflater: LayoutInflater,
      container: ViewGroup?,
      savedInstanceState: Bundle?
  ): View {
    val act = requireActivity()

    Injector.obtainFromApplication<PYDroidComponent>(act).plusAbout().create().inject(this)
    val vm = viewModel.requireNotNull()

    return ComposeView(act).apply {
      id = R.id.dialog_about

      setContent {
        val handler = LocalUriHandler.current

        composeTheme(act) {
          AboutScreen(
              state = vm.state(),
              onViewHomePage = { openLibrary(handler, it) },
              onViewLicense = { openLicense(handler, it) },
              onNavigationErrorDismissed = { vm.handleDismissFailedNavigation() },
              onClose = { dismiss() },
          )
        }
      }
    }
  }

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

  private fun openPage(handler: UriHandler, url: String) {
    try {
      handler.openUri(url)
    } catch (e: Throwable) {
      viewModel.requireNotNull().handleFailedNavigation(e)
    }
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    makeFullscreen()

    viewModel.requireNotNull().handleLoadLicenses(viewLifecycleOwner.lifecycleScope)
  }

  override fun onDestroyView() {
    super.onDestroyView()
    (view as? ComposeView)?.disposeComposition()

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
