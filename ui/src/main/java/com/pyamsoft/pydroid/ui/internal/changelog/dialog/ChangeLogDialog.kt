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
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import coil.ImageLoader
import com.pyamsoft.pydroid.core.requireNotNull
import com.pyamsoft.pydroid.inject.Injector
import com.pyamsoft.pydroid.ui.PYDroidComponent
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.app.ComposeTheme
import com.pyamsoft.pydroid.ui.app.makeFullWidth
import com.pyamsoft.pydroid.ui.internal.app.NoopTheme
import com.pyamsoft.pydroid.ui.internal.changelog.ChangeLogProvider
import com.pyamsoft.pydroid.ui.internal.rating.RatingViewModel
import com.pyamsoft.pydroid.ui.util.show

internal class ChangeLogDialog : AppCompatDialogFragment() {

  /** May be provided by PYDroid, otherwise this is just a noop */
  internal var composeTheme: ComposeTheme = NoopTheme

  internal var factory: ViewModelProvider.Factory? = null
  private val viewModel by activityViewModels<ChangeLogDialogViewModel> { factory.requireNotNull() }

  // Don't need to create a component or bind this to the controller, since RatingActivity should
  // be bound for us.
  private val ratingViewModel by activityViewModels<RatingViewModel> { factory.requireNotNull() }

  internal var imageLoader: ImageLoader? = null

  @CheckResult
  private fun getChangelogProvider(): ChangeLogProvider {
    return requireActivity() as ChangeLogProvider
  }

  override fun onCreateView(
      inflater: LayoutInflater,
      container: ViewGroup?,
      savedInstanceState: Bundle?
  ): View {
    val act = requireActivity()

    Injector.obtainFromApplication<PYDroidComponent>(act)
        .plusChangeLogDialog()
        .create(getChangelogProvider())
        .inject(this)

    return ComposeView(act).apply {
      id = R.id.dialog_changelog

      setContent {
        val state by viewModel.compose()

        composeTheme(act) {
          ChangeLogScreen(
              state = state,
              imageLoader = imageLoader.requireNotNull(),
              onRateApp = { ratingViewModel.handleViewMarketPage() },
              onClose = { dismiss() },
          )
        }
      }
    }
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    makeFullWidth()

    viewModel.bindController(viewLifecycleOwner) {
      // TODO any events
    }
  }

  override fun onDestroyView() {
    super.onDestroyView()
    (view as? ComposeView)?.disposeComposition()

    factory = null
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
