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

package com.pyamsoft.pydroid.ui.internal.otherapps

import android.content.res.Configuration
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
import coil.ImageLoader
import com.pyamsoft.pydroid.bootstrap.otherapps.api.OtherApp
import com.pyamsoft.pydroid.core.requireNotNull
import com.pyamsoft.pydroid.inject.Injector
import com.pyamsoft.pydroid.ui.PYDroidComponent
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.app.ComposeTheme
import com.pyamsoft.pydroid.ui.app.makeFullWidth
import com.pyamsoft.pydroid.ui.internal.app.NoopTheme
import com.pyamsoft.pydroid.ui.util.dispose
import com.pyamsoft.pydroid.ui.util.recompose
import com.pyamsoft.pydroid.ui.util.show

internal class OtherAppsDialog : AppCompatDialogFragment() {

  /** May be provided by PYDroid, otherwise this is just a noop */
  internal var composeTheme: ComposeTheme = NoopTheme

  internal var viewModel: OtherAppsViewModeler? = null

  internal var imageLoader: ImageLoader? = null

  private fun openSourceCode(handler: UriHandler, app: OtherApp) {
    openPage(
        handler = handler,
        url = app.sourceUrl,
    )
  }

  private fun openStorePage(handler: UriHandler, app: OtherApp) {
    openPage(
        handler = handler,
        url = app.storeUrl,
    )
  }

  private fun openPage(handler: UriHandler, url: String) {
    val vm = viewModel.requireNotNull()

    try {
      vm.handleHideNavigation()
      handler.openUri(url)
    } catch (e: Throwable) {
      vm.handleNavigationFailed(e)
    }
  }

  override fun onCreateView(
      inflater: LayoutInflater,
      container: ViewGroup?,
      savedInstanceState: Bundle?
  ): View {
    val act = requireActivity()
    Injector.obtainFromApplication<PYDroidComponent>(act).plusOtherApps().create().inject(this)

    return ComposeView(act).apply {
      id = R.id.dialog_otherapps

      val vm = viewModel.requireNotNull()
      val imageLoader = imageLoader.requireNotNull()
      setContent {
        val handler = LocalUriHandler.current

        vm.Render { state ->
          composeTheme(act) {
            OtherAppsScreen(
                state = state,
                imageLoader = imageLoader,
                onNavigationErrorDismissed = { vm.handleHideNavigation() },
                onViewStorePage = { openStorePage(handler, it) },
                onViewSourceCode = { openSourceCode(handler, it) },
                onClose = { dismiss() },
            )
          }
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
    makeFullWidth()
    recompose()
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

    private const val TAG = "OtherAppsDialog"

    @JvmStatic
    internal fun show(activity: FragmentActivity) {
      OtherAppsDialog().apply { arguments = Bundle().apply {} }.show(activity, TAG)
    }
  }
}
