/*
 * Copyright 2023 pyamsoft
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

package com.pyamsoft.pydroid.ui.datapolicy

import androidx.activity.ComponentActivity
import androidx.annotation.CheckResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.pyamsoft.pydroid.arch.SaveStateDisposableEffect
import com.pyamsoft.pydroid.core.Logger
import com.pyamsoft.pydroid.core.requireNotNull
import com.pyamsoft.pydroid.ui.internal.datapolicy.DataPolicyViewModeler
import com.pyamsoft.pydroid.ui.internal.datapolicy.DataPolicyViewState
import com.pyamsoft.pydroid.ui.internal.datapolicy.dialog.DataPolicyDisclosureDialog
import com.pyamsoft.pydroid.ui.internal.pydroid.ObjectGraph
import com.pyamsoft.pydroid.ui.util.fillUpToPortraitSize
import com.pyamsoft.pydroid.util.doOnCreate
import com.pyamsoft.pydroid.util.doOnDestroy

/** Handles Change Log display in app */
public typealias OnShowDataPolicy = () -> Unit

/** Dismiss the change log display in app */
public typealias OnDismissDataPolicy = () -> Unit

/**
 * A self contained class which is able to check for updates and prompt the user to install them
 * in-app. Adopts the theme from whichever composable it is rendered into
 */
public class ShowDataPolicy
internal constructor(
    activity: ComponentActivity,
    private val disabled: Boolean,
) {

  internal var viewModel: DataPolicyViewModeler? = null

  init {
    if (disabled) {
      Logger.w("Application has disabled the DataPolicy component")
    } else {
      // Need to wait until after onCreate so that the ObjectGraph.ActivityScope is
      // correctly set up otherwise we crash.
      activity.doOnCreate {
        ObjectGraph.ActivityScope.retrieve(activity)
            .injector()
            .plusDataPolicy()
            .create()
            .inject(this)
      }
    }

    activity.doOnDestroy { viewModel = null }
  }

  @Composable
  private fun RenderContent(
      state: DataPolicyViewState,
      onDismissDialog: () -> Unit,
  ) {
    val acceptedState by state.isAccepted.collectAsState()

    if (acceptedState != DataPolicyViewState.AcceptedState.NONE &&
        acceptedState != DataPolicyViewState.AcceptedState.ACCEPTED) {
      DataPolicyDisclosureDialog(
          modifier = Modifier.fillUpToPortraitSize(),
          onDismiss = onDismissDialog,
      )
    }
  }

  /** Render into a composable the data policy dialog */
  @Composable
  public fun Render() {
    if (disabled) {
      // Log in a LE so that we only log once per lifecycle instead of per-render
      LaunchedEffect(Unit) { Logger.w("Application has disabled the DataPolicy component") }
      return
    }

    val vm = viewModel.requireNotNull()

    LaunchedEffect(vm) { vm.bind(scope = this) }
    SaveStateDisposableEffect(vm)

    RenderContent(
        state = vm,
        onDismissDialog = {
          Logger.d("DPD accepted, this will be dismissed once the Preferences update")
        },
    )
  }

  public companion object {

    /** Create a new show update changelog UI component */
    @JvmStatic
    @CheckResult
    @JvmOverloads
    public fun create(
        activity: ComponentActivity,
        disabled: Boolean = false,
    ): ShowDataPolicy {
      return ShowDataPolicy(activity, disabled)
    }
  }
}
