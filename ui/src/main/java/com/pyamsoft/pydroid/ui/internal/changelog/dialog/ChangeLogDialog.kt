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
import androidx.annotation.CheckResult
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.pyamsoft.pydroid.arch.StateSaver
import com.pyamsoft.pydroid.arch.createComponent
import com.pyamsoft.pydroid.arch.newUiController
import com.pyamsoft.pydroid.core.requireNotNull
import com.pyamsoft.pydroid.inject.Injector
import com.pyamsoft.pydroid.ui.PYDroidComponent
import com.pyamsoft.pydroid.ui.databinding.ChangelogDialogBinding
import com.pyamsoft.pydroid.ui.internal.changelog.ChangeLogProvider
import com.pyamsoft.pydroid.ui.internal.dialog.IconDialog
import com.pyamsoft.pydroid.ui.internal.rating.RatingViewModel
import com.pyamsoft.pydroid.ui.util.show

internal class ChangeLogDialog : IconDialog() {

  private var stateSaver: StateSaver? = null

  internal var listView: ChangeLogList? = null
  internal var nameView: ChangeLogName? = null
  internal var closeView: ChangeLogClose? = null
  internal var iconView: ChangeLogIcon? = null

  internal var factory: ViewModelProvider.Factory? = null
  private val viewModel by activityViewModels<ChangeLogDialogViewModel> { factory.requireNotNull() }

  // Don't need to create a component or bind this to the controller, since RatingActivity should
  // be bound for us.
  private val ratingViewModel by activityViewModels<RatingViewModel> { factory.requireNotNull() }

  @CheckResult
  private fun getChangelogProvider(): ChangeLogProvider {
    return requireActivity() as ChangeLogProvider
  }

  override fun onBindingCreated(binding: ChangelogDialogBinding, savedInstanceState: Bundle?) {
    Injector.obtainFromApplication<PYDroidComponent>(binding.root.context)
        .plusChangeLogDialog()
        .create(
            viewLifecycleOwner, binding.dialogRoot, binding.changelogIcon, getChangelogProvider())
        .inject(this)

    stateSaver =
        createComponent(
            savedInstanceState,
            viewLifecycleOwner,
            viewModel,
            controller = newUiController {},
            iconView.requireNotNull(),
            nameView.requireNotNull(),
            listView.requireNotNull(),
            closeView.requireNotNull(),
        ) {
          return@createComponent when (it) {
            is ChangeLogDialogViewEvent.Close -> dismiss()
            is ChangeLogDialogViewEvent.Rate -> ratingViewModel.loadMarketPage()
          }
        }
  }

  override fun onDestroyView() {
    super.onDestroyView()
    factory = null
    stateSaver = null

    listView = null
    nameView = null
    iconView = null
    closeView = null
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    stateSaver?.saveState(outState)
  }

  companion object {

    private const val TAG = "ChangeLogDialog"

    @JvmStatic
    fun open(activity: FragmentActivity) {
      ChangeLogDialog().apply { arguments = Bundle().apply {} }.show(activity, TAG)
    }
  }
}
