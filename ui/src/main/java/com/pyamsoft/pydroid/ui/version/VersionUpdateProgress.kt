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

package com.pyamsoft.pydroid.ui.version

import androidx.annotation.CheckResult
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
import com.pyamsoft.pydroid.core.requireNotNull
import com.pyamsoft.pydroid.ui.internal.pydroid.ObjectGraph
import com.pyamsoft.pydroid.ui.internal.version.VersionCheckViewModeler
import com.pyamsoft.pydroid.ui.internal.version.VersionUpdateProgressScreen
import com.pyamsoft.pydroid.util.doOnCreate
import com.pyamsoft.pydroid.util.doOnDestroy

/** A Composable that can display version update progress */
public typealias VersionUpdateProgressWidget =
    (
        VersionCheckViewState,
    ) -> Unit

/**
 * A self contained class which is able to display the current progress of an in-app update Adopts
 * the theme from whichever composable it is rendered into
 */
public class VersionUpdateProgress
internal constructor(
    activity: FragmentActivity,
) {
  private var hostingActivity: FragmentActivity? = activity

  internal var viewModel: VersionCheckViewModeler? = null

  init {
    // Need to wait until after onCreate so that the ObjectGraph.ActivityScope is
    // correctly set up otherwise we crash.
    activity.doOnCreate {
      ObjectGraph.ActivityScope.retrieve(activity)
          .injector()
          .plusVersionCheck()
          .create()
          .inject(this)
    }

    activity.doOnDestroy {
      hostingActivity = null
      viewModel = null
    }
  }

  /**
   * Render into a composable the version check screen upsell
   *
   * Using custom UI
   */
  @Composable
  public fun Render(content: @Composable VersionUpdateProgressWidget) {
    val state = viewModel.requireNotNull().state()
    content(state)
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
        activity: FragmentActivity,
    ): VersionUpdateProgress {
      return VersionUpdateProgress(activity)
    }
  }
}
