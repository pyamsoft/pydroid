/*
 * Copyright 2024 pyamsoft
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pyamsoft.pydroid.ui.billing

import androidx.activity.ComponentActivity
import androidx.annotation.CheckResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.pyamsoft.pydroid.arch.SaveStateDisposableEffect
import com.pyamsoft.pydroid.core.Logger
import com.pyamsoft.pydroid.core.requireNotNull
import com.pyamsoft.pydroid.ui.internal.billing.BillingViewModeler
import com.pyamsoft.pydroid.ui.internal.billing.ShowBillingUpsell
import com.pyamsoft.pydroid.ui.internal.billing.dialog.BillingDialog
import com.pyamsoft.pydroid.ui.internal.pydroid.ObjectGraph
import com.pyamsoft.pydroid.ui.util.rememberNotNull
import com.pyamsoft.pydroid.util.doOnCreate
import com.pyamsoft.pydroid.util.doOnDestroy

/** Handles Billing display in app */
public typealias OnShowBilling = () -> Unit

/** Dismiss the Billnig display in app */
public typealias OnDismissBilling = () -> Unit

/** Handles Billing display in app */
public typealias ShowBillingWidget =
    @Composable
    (
        BillingViewState,
        OnShowBilling,
        OnDismissBilling,
    ) -> Unit

/** Handles Billing related work in an Activity */
public class BillingUpsell
internal constructor(
    activity: ComponentActivity,
    private val disabled: Boolean,
) {

  internal var viewModel: BillingViewModeler? = null

  init {
    if (disabled) {
      Logger.w { "Application has disabled the billing component" }
    } else {
      // Need to wait until after onCreate so that the ObjectGraph.ActivityScope is
      // correctly set up otherwise we crash.
      activity.doOnCreate {
        ObjectGraph.ActivityScope.retrieve(activity).injector().plusBilling().create().inject(this)

        viewModel
            .requireNotNull()
            .bind(
                scope = activity.lifecycleScope,
            )
      }
    }

    val repeatedActions =
        object : DefaultLifecycleObserver {

          // Do on each start
          override fun onStart(owner: LifecycleOwner) {
            viewModel.requireNotNull().handleMaybeShowUpsell()
          }
        }

    activity.lifecycle.addObserver(repeatedActions)
    activity.doOnDestroy {
      activity.lifecycle.removeObserver(repeatedActions)

      viewModel = null
    }
  }

  @Composable
  private fun RenderContent(
      modifier: Modifier = Modifier,
      state: BillingViewState,
      onDismissDialog: () -> Unit,
      content: @Composable () -> Unit,
  ) {
    val showDialog by state.isShowingDialog.collectAsStateWithLifecycle()

    content()

    if (showDialog) {
      BillingDialog(
          modifier = modifier,
          onDismiss = onDismissDialog,
      )
    }
  }

  /**
   * Render into a composable the version check screen upsell
   *
   * Using custom UI
   */
  @Composable
  public fun Render(
      modifier: Modifier = Modifier,
      content: ShowBillingWidget,
  ) {
    if (disabled) {
      // Log in a LE so that we only log once per lifecycle instead of per-render
      LaunchedEffect(Unit) { Logger.w { "Application has disabled the Billing component" } }
      return
    }

    val vm = rememberNotNull(viewModel)
    SaveStateDisposableEffect(vm)

    RenderContent(
        modifier = modifier,
        state = vm,
        onDismissDialog = { vm.handleCloseDialog() },
    ) {
      content(
          vm,
          {
            vm.handleDismissUpsell()
            vm.handleOpenDialog()
          },
          { vm.handleDismissUpsell() },
      )
    }
  }

  /** Render into a composable the default version check screen upsell */
  @Composable
  public fun RenderBillingUpsellWidget(
      modifier: Modifier = Modifier,
      dialogModifier: Modifier = Modifier,
  ) {
    Render(
        modifier = dialogModifier,
    ) { state, onShowBilling, onDismiss ->
      ShowBillingUpsell(
          modifier = modifier,
          state = state,
          onShowBilling = onShowBilling,
          onDismiss = onDismiss,
      )
    }
  }

  public companion object {

    /** Create a new Billing upsell UI component */
    @JvmStatic
    @CheckResult
    @JvmOverloads
    public fun create(
        activity: ComponentActivity,
        disabled: Boolean = false,
    ): BillingUpsell {
      return BillingUpsell(activity, disabled)
    }
  }
}
