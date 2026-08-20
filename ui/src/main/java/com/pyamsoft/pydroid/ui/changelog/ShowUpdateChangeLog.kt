/*
 * Copyright 2026 pyamsoft
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

package com.pyamsoft.pydroid.ui.changelog

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.pyamsoft.pydroid.arch.SaveStateDisposableEffect
import com.pyamsoft.pydroid.core.requireNotNull
import com.pyamsoft.pydroid.ui.internal.app.PYDroidActivityState
import com.pyamsoft.pydroid.ui.internal.changelog.ChangeLogViewModeler
import com.pyamsoft.pydroid.ui.internal.changelog.ShowChangeLogScreen
import com.pyamsoft.pydroid.ui.internal.changelog.dialog.ChangeLogDialog
import com.pyamsoft.pydroid.ui.internal.pydroid.ObjectGraph
import com.pyamsoft.pydroid.ui.internal.util.rememberPYDroidDelegate
import com.pyamsoft.pydroid.ui.util.rememberNotNull
import com.pyamsoft.pydroid.util.doOnCreate
import com.pyamsoft.pydroid.util.doOnDestroy

/** Handles Change Log display in app */
public typealias OnShowChangeLog = () -> Unit

/** Dismiss the change log display in app */
public typealias OnDismissChangeLog = () -> Unit

/** Handles Change Log display in app */
public typealias ShowUpdateChangeLogWidget =
    @Composable
    (
        ChangeLogViewState,
        OnShowChangeLog,
        OnDismissChangeLog,
    ) -> Unit

/**
 * A self contained class which is able to check for updates and prompt the user to install them
 * in-app. Adopts the theme from whichever composable it is rendered into
 */
internal class ShowUpdateChangeLog
internal constructor(
    activity: ComponentActivity,
) {

  internal var viewModel: ChangeLogViewModeler? = null

  init {
    // Need to wait until after onCreate so that the ObjectGraph.ActivityScope is
    // correctly set up otherwise we crash.
    activity.doOnCreate {
      ObjectGraph.ActivityScope.retrieve(activity).injector().plusChangeLog().create().inject(this)

      viewModel
          .requireNotNull()
          .bind(
              scope = activity.lifecycleScope,
          )
    }

    activity.doOnDestroy { viewModel = null }
  }

  @Composable
  private fun RenderContent(
      modifier: Modifier,
      activityState: PYDroidActivityState,
      state: ChangeLogViewState,
      onDismissDialog: () -> Unit,
      content: @Composable () -> Unit,
  ) {
    val showDialog by state.isShowingDialog.collectAsStateWithLifecycle()

    content()

    if (showDialog) {
      ChangeLogDialog(
          modifier = modifier,
          activityState = activityState,
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
  private fun Render(
      modifier: Modifier = Modifier,
      content: ShowUpdateChangeLogWidget,
  ) {
    val delegate = rememberPYDroidDelegate()
    val activityState = remember(delegate) { delegate.activityState() }

    val vm = rememberNotNull(viewModel)
    SaveStateDisposableEffect(vm)

    RenderContent(
        modifier = modifier,
        state = vm,
        activityState = activityState,
        onDismissDialog = { vm.handleCloseDialog() },
    ) {
      content(
          vm,
          {
            vm.handleDismissUpsell()
            vm.handleShowDialog()
          },
          { vm.handleDismissUpsell() },
      )
    }
  }

  /** Render into a composable the default version check screen upsell */
  @Composable
  fun RenderChangeLogWidget(
      modifier: Modifier = Modifier,
      dialogModifier: Modifier = Modifier,
  ) {
    Render(
        modifier = dialogModifier,
    ) { state, onShowChangeLog, onDismiss ->
      ShowChangeLogScreen(
          modifier = modifier,
          state = state,
          onShowChangeLog = onShowChangeLog,
          onDismiss = onDismiss,
      )
    }
  }
}
