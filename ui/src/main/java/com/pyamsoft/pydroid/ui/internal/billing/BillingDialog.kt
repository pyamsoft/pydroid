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

package com.pyamsoft.pydroid.ui.internal.billing

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
import com.pyamsoft.pydroid.billing.BillingLauncher
import com.pyamsoft.pydroid.billing.BillingSku
import com.pyamsoft.pydroid.core.Logger
import com.pyamsoft.pydroid.core.requireNotNull
import com.pyamsoft.pydroid.inject.Injector
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.app.ComposeTheme
import com.pyamsoft.pydroid.ui.app.makeFullWidth
import com.pyamsoft.pydroid.ui.internal.app.AppComponent
import com.pyamsoft.pydroid.ui.internal.app.AppProvider
import com.pyamsoft.pydroid.ui.internal.app.NoopTheme
import com.pyamsoft.pydroid.ui.util.show
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

internal class BillingDialog : AppCompatDialogFragment() {

  /** May be provided by PYDroid, otherwise this is just a noop */
  internal var composeTheme: ComposeTheme = NoopTheme

  internal var purchaseClient: BillingLauncher? = null

  internal var viewModel: BillingViewModeler? = null

  internal var imageLoader: ImageLoader? = null

  @CheckResult
  private fun getApplicationProvider(): AppProvider {
    return requireActivity() as AppProvider
  }

  private fun launchPurchase(sku: BillingSku) {
    requireActivity().also { a ->
      // Enforce on main thread
      a.lifecycleScope.launch(context = Dispatchers.Main) {
        Logger.d("Start purchase flow for $sku")
        purchaseClient.requireNotNull().purchase(a, sku)
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
        .plusBilling()
        .create(getApplicationProvider())
        .inject(this)

    return ComposeView(act).apply {
      id = R.id.dialog_billing

      val vm = viewModel.requireNotNull()
      val imageLoader = imageLoader.requireNotNull()
      setContent {
        vm.Render { state ->
          composeTheme(act) {
            BillingScreen(
                state = state,
                imageLoader = imageLoader,
                onPurchase = { launchPurchase(it) },
                onBillingErrorDismissed = { vm.handleClearError() },
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

  override fun onResume() {
    super.onResume()
    viewModel
        .requireNotNull()
        .handleRefresh(
            scope = viewLifecycleOwner.lifecycleScope,
        )
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    viewModel?.saveState(outState)
  }

  override fun onDestroyView() {
    super.onDestroyView()
    (view as? ComposeView)?.disposeComposition()

    viewModel = null
    imageLoader = null
    purchaseClient = null
  }

  companion object {

    private const val TAG = "BillingDialog"

    @JvmStatic
    internal fun open(activity: FragmentActivity) {
      BillingDialog().apply { arguments = Bundle().apply {} }.show(activity, TAG)
    }
  }
}
