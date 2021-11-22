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
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import coil.ImageLoader
import com.pyamsoft.pydroid.core.Logger
import com.pyamsoft.pydroid.core.requireNotNull
import com.pyamsoft.pydroid.inject.Injector
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.app.ComposeTheme
import com.pyamsoft.pydroid.ui.app.makeFullWidth
import com.pyamsoft.pydroid.ui.internal.app.AppComponent
import com.pyamsoft.pydroid.ui.internal.app.NoopTheme
import com.pyamsoft.pydroid.ui.internal.changelog.ChangeLogProvider
import com.pyamsoft.pydroid.ui.internal.rating.RatingViewModeler
import com.pyamsoft.pydroid.ui.util.show
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

internal class ChangeLogDialog : AppCompatDialogFragment() {

  /** May be provided by PYDroid, otherwise this is just a noop */
  internal var composeTheme: ComposeTheme = NoopTheme

  internal var viewModel: ChangeLogDialogViewModeler? = null

  // Don't need to create a component or bind this to the controller, since RatingActivity should
  // be bound for us.
  internal var ratingViewModel: RatingViewModeler? = null

  internal var imageLoader: ImageLoader? = null

  @CheckResult
  private fun getChangelogProvider(): ChangeLogProvider {
    return requireActivity() as ChangeLogProvider
  }

  private fun handleLaunchMarket() {
    ratingViewModel.requireNotNull().handleViewMarketPage(
            viewLifecycleOwner.lifecycleScope,
        ) { launcher ->
      val act = requireActivity()
      act.lifecycleScope.launch(context = Dispatchers.Main) {
        launcher.rate(act).onFailure { Logger.e(it, "Unable to show Market page from changelog") }
      }
    }
  }

  override fun onCreateView(
      inflater: LayoutInflater,
      container: ViewGroup?,
      savedInstanceState: Bundle?
  ): View {
    val act = requireActivity()

    Injector.obtainFromActivity<AppComponent>(act)
        .plusChangeLogDialog()
        .create(getChangelogProvider())
        .inject(this)

    return ComposeView(act).apply {
      id = R.id.dialog_changelog

      val vm = viewModel.requireNotNull()
      val imageLoader = imageLoader.requireNotNull()
      setContent {
        vm.Render { state ->
          composeTheme(act) {
            ChangeLogScreen(
                state = state,
                imageLoader = imageLoader,
                onRateApp = { handleLaunchMarket() },
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

    ratingViewModel.requireNotNull().restoreState(savedInstanceState)
    viewModel.requireNotNull().also { vm ->
      vm.restoreState(savedInstanceState)
      vm.bind(scope = viewLifecycleOwner.lifecycleScope)
    }
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    viewModel?.saveState(outState)
    ratingViewModel?.saveState(outState)
  }

  override fun onDestroyView() {
    super.onDestroyView()
    (view as? ComposeView)?.disposeComposition()

    viewModel = null
    ratingViewModel = null
    imageLoader = null
  }

  companion object {

    private const val TAG = "ChangeLogDialog"

    @JvmStatic
    internal fun open(activity: FragmentActivity) {
      ChangeLogDialog().apply { arguments = Bundle().apply {} }.show(activity, TAG)
    }
  }
}
