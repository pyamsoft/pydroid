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

package com.pyamsoft.pydroid.ui.version

import androidx.activity.ComponentActivity
import androidx.annotation.CheckResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.pyamsoft.pydroid.core.Logger
import com.pyamsoft.pydroid.ui.internal.pydroid.ObjectGraph
import com.pyamsoft.pydroid.ui.internal.version.VersionCheckViewModeler
import com.pyamsoft.pydroid.ui.internal.version.VersionUpdateProgressScreen
import com.pyamsoft.pydroid.ui.util.rememberNotNull
import com.pyamsoft.pydroid.util.doOnCreate
import com.pyamsoft.pydroid.util.doOnDestroy

/** A Composable that can display version update progress */
public typealias VersionUpdateProgressWidget =
    (
        state: VersionCheckViewState,
    ) -> Unit

/**
 * A self contained class which is able to display the current progress of an in-app update Adopts
 * the theme from whichever composable it is rendered into
 */
public class VersionUpdateProgress
internal constructor(
    activity: ComponentActivity,
    private val disabled: Boolean,
) {

  internal var viewModel: VersionCheckViewModeler? = null

  init {
    if (disabled) {
      Logger.w { "Application has disabled the VersionCheck component" }
    } else {
      // Need to wait until after onCreate so that the ObjectGraph.ActivityScope is
      // correctly set up otherwise we crash.
      activity.doOnCreate {
        ObjectGraph.ActivityScope.retrieve(activity)
            .injector()
            .plusVersionCheck()
            .create()
            .inject(this)
      }
    }

    activity.doOnDestroy { viewModel = null }
  }

  /**
   * Render into a composable the version check screen upsell
   *
   * Using custom UI
   */
  @Composable
  public fun Render(content: @Composable VersionUpdateProgressWidget) {
    if (disabled) {
      // Log in a LE so that we only log once per lifecycle instead of per-render
      LaunchedEffect(Unit) { Logger.w { "Application has disabled the VersionCheck component" } }
      return
    }

    val viewModel = rememberNotNull(viewModel)
    content(
        state = viewModel,
    )
  }

  /** Render into a composable the default version check screen upsell */
  @Composable
  public fun RenderVersionCheckWidget(
      modifier: Modifier = Modifier,
  ) {
    Render { state ->
      VersionUpdateProgressScreen(
          modifier = modifier,
          state = state,
      )
    }
  }

  public companion object {

    /** Create a new version update progress UI component */
    @JvmStatic
    @CheckResult
    public fun create(
        activity: ComponentActivity,
        disabled: Boolean = false,
    ): VersionUpdateProgress {
      return VersionUpdateProgress(activity, disabled)
    }
  }
}
