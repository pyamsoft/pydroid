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
import androidx.annotation.CheckResult
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.pyamsoft.pydroid.arch.StateSaver
import com.pyamsoft.pydroid.arch.bindController
import com.pyamsoft.pydroid.billing.BillingLauncher
import com.pyamsoft.pydroid.billing.BillingSku
import com.pyamsoft.pydroid.ui.Injector
import com.pyamsoft.pydroid.ui.arch.fromViewModelFactory
import com.pyamsoft.pydroid.ui.databinding.ChangelogDialogBinding
import com.pyamsoft.pydroid.ui.internal.app.AppProvider
import com.pyamsoft.pydroid.ui.internal.dialog.IconDialog
import com.pyamsoft.pydroid.ui.util.show
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

internal class BillingDialog : IconDialog() {

    private var stateSaver: StateSaver? = null

    internal var nameView: BillingName? = null
    internal var iconView: BillingIcon? = null
    internal var listView: BillingList? = null
    internal var closeView: BillingClose? = null

    internal var purchaseClient: BillingLauncher? = null

    internal var factory: ViewModelProvider.Factory? = null
    private val viewModel by fromViewModelFactory<BillingViewModel>(activity = true) { factory }

    @CheckResult
    private fun getApplicationProvider(): AppProvider {
        return requireActivity() as AppProvider
    }

    override fun onBindingCreated(binding: ChangelogDialogBinding, savedInstanceState: Bundle?) {
        Injector.obtainFromActivity<BillingComponent>(requireActivity())
            .plusDialog()
            .create(
                binding.dialogRoot,
                viewLifecycleOwner,
                binding.changelogIcon,
                getApplicationProvider(),
            )
            .inject(this)

        stateSaver = viewModel.bindController(
            savedInstanceState,
            viewLifecycleOwner,
            requireNotNull(iconView),
            requireNotNull(nameView),
            requireNotNull(listView),
            requireNotNull(closeView),
        ) {
            return@bindController when (it) {
                is BillingDialogViewEvent.Close -> dismiss()
                is BillingDialogViewEvent.ClearError -> viewModel.handleClearError()
                is BillingDialogViewEvent.Purchase -> viewModel.handlePurchase(
                    this,
                    it.index
                ) { sku ->
                    launchPurchase(sku)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.handleRefresh()
    }

    private fun CoroutineScope.launchPurchase(sku: BillingSku) {
        launch(context = Dispatchers.Main) {
            Timber.d("Start purchase flow for $sku")
            requireNotNull(purchaseClient).purchase(requireActivity(), sku)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        factory = null
        stateSaver = null
        purchaseClient = null

        nameView = null
        iconView = null
        listView = null
        closeView = null
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        stateSaver?.saveState(outState)
    }

    companion object {

        private const val TAG = "BillingDialog"

        @JvmStatic
        fun open(activity: FragmentActivity) {
            BillingDialog().apply {
                arguments = Bundle().apply { }
            }.show(activity, TAG)
        }

        @JvmStatic
        @CheckResult
        fun isNotShown(activity: FragmentActivity): Boolean {
            return activity.supportFragmentManager.findFragmentByTag(TAG) == null
        }
    }
}
