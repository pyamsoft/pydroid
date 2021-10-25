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

package com.pyamsoft.pydroid.ui.internal.version.upgrade

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
import com.pyamsoft.pydroid.core.Logger
import com.pyamsoft.pydroid.core.Logger.d
import com.pyamsoft.pydroid.core.requireNotNull
import com.pyamsoft.pydroid.inject.Injector
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.app.ComposeTheme
import com.pyamsoft.pydroid.ui.app.makeFullWidth
import com.pyamsoft.pydroid.ui.internal.app.NoopTheme
import com.pyamsoft.pydroid.ui.internal.version.VersionCheckComponent
import com.pyamsoft.pydroid.ui.util.show

internal class VersionUpgradeDialog internal constructor() : AppCompatDialogFragment() {

  /** May be provided by PYDroid, otherwise this is just a noop */
  internal var composeTheme: ComposeTheme = NoopTheme

  internal var factory: ViewModelProvider.Factory? = null
  private val viewModel by activityViewModels<VersionUpgradeViewModel> { factory.requireNotNull() }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    isCancelable = false
  }

  override fun onCreateView(
      inflater: LayoutInflater,
      container: ViewGroup?,
      savedInstanceState: Bundle?
  ): View {
    val act = requireActivity()
    Injector.obtainFromActivity<VersionCheckComponent>(act).inject(this)

    return ComposeView(act).apply {
      id = R.id.dialog_upgrade

      setContent {
        val state by viewModel.compose()

        composeTheme {
          VersionUpgradeScreen(
              state = state,
              onUpgrade = { viewModel.completeUpgrade() },
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
        is VersionUpgradeControllerEvent.UpgradeComplete -> {
          Logger.d("Upgrade complete, dismiss")
          dismiss()
        }
      }
    }
  }

  override fun onDestroyView() {
    super.onDestroyView()
    factory = null
  }

  companion object {

    private const val TAG = "VersionUpgradeDialog"

    @JvmStatic
    @CheckResult
    private fun newInstance(): DialogFragment {
      return VersionUpgradeDialog().apply { arguments = Bundle().apply {} }
    }

    @JvmStatic
    fun show(activity: FragmentActivity) {
      return newInstance().show(activity, TAG)
    }
  }
}
