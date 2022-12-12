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

package com.pyamsoft.pydroid.ui.changelog

import androidx.annotation.CheckResult
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
import com.pyamsoft.pydroid.core.requireNotNull
import com.pyamsoft.pydroid.ui.internal.changelog.ChangeLogViewModeler
import com.pyamsoft.pydroid.ui.internal.changelog.ChangeLogViewState
import com.pyamsoft.pydroid.ui.internal.changelog.ShowChangeLogScreen
import com.pyamsoft.pydroid.ui.internal.changelog.dialog.ChangeLogDialog
import com.pyamsoft.pydroid.ui.internal.pydroid.ObjectGraph
import com.pyamsoft.pydroid.util.doOnCreate
import com.pyamsoft.pydroid.util.doOnDestroy

/** Handles Change Log display in app */
public typealias OnShowChangeLog = () -> Unit

/** Handles Change Log display in app */
public typealias ShowUpdateChangeLogWidget =
    (
        ChangeLogViewState,
        OnShowChangeLog,
    ) -> Unit

/**
 * A self contained class which is able to check for updates and prompt the user to install them
 * in-app. Adopts the theme from whichever composable it is rendered into
 */
public class ShowUpdateChangeLog
internal constructor(
    activity: FragmentActivity,
) {
  private var hostingActivity: FragmentActivity? = activity

  internal var viewModel: ChangeLogViewModeler? = null

  init {
    // Need to wait until after onCreate so that the ObjectGraph.ActivityScope is
    // correctly set up otherwise we crash.
    activity.doOnCreate {
      ObjectGraph.ActivityScope.retrieve(activity).injector().plusChangeLog().create().inject(this)
    }

    activity.doOnDestroy {
      hostingActivity = null
      viewModel = null
    }
  }

  private fun handleChangeLog() {
    ChangeLogDialog.show(activity = hostingActivity.requireNotNull())
  }

  /**
   * Render into a composable the version check screen upsell
   *
   * Using custom UI
   */
  @Composable
  public fun Render(content: @Composable ShowUpdateChangeLogWidget) {
    val state = viewModel.requireNotNull().state()
    content(state) { handleChangeLog() }
  }

  /** Render into a composable the default version check screen upsell */
  @Composable
  public fun RenderChangeLogWidget(
      modifier: Modifier = Modifier,
  ) {
    Render { state, onShowChangeLog ->
      ShowChangeLogScreen(
          modifier = modifier,
          state = state,
          onShowChangeLog = onShowChangeLog,
          onDismiss = {
            // TODO
          },
      )
    }
  }

  public companion object {

    /** Create a new show update changelog UI component */
    @JvmStatic
    @CheckResult
    public fun create(
        activity: FragmentActivity,
    ): ShowUpdateChangeLog {
      return ShowUpdateChangeLog(activity)
    }
  }
}
