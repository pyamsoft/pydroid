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
import androidx.lifecycle.lifecycleScope
import com.pyamsoft.pydroid.core.Logger
import com.pyamsoft.pydroid.core.requireNotNull
import com.pyamsoft.pydroid.inject.Injector
import com.pyamsoft.pydroid.ui.app.ComposeTheme
import com.pyamsoft.pydroid.ui.internal.app.AppComponent
import com.pyamsoft.pydroid.ui.internal.app.NoopTheme
import com.pyamsoft.pydroid.ui.internal.version.VersionCheckScreen
import com.pyamsoft.pydroid.ui.internal.version.VersionCheckViewModeler
import com.pyamsoft.pydroid.ui.internal.version.upgrade.VersionUpgradeViewModeler
import com.pyamsoft.pydroid.util.doOnDestroy

public class VersionUpgradeAvailable
internal constructor(
    activity: FragmentActivity,
    private val appName: String,
) {

  private var activity: FragmentActivity? = null

  /** May be provided by PYDroid, otherwise this is just a noop */
  internal var composeTheme: ComposeTheme = NoopTheme

  internal var checkViewModel: VersionCheckViewModeler? = null
  internal var upgradeViewModel: VersionUpgradeViewModeler? = null

  init {
    inject(activity)
    unbindOnDestroy(activity)
  }

  private fun unbindOnDestroy(activity: FragmentActivity) {
    activity.doOnDestroy {
      this.activity = null
      checkViewModel = null
    }
  }

  private fun inject(activity: FragmentActivity) {
    Injector.obtainFromActivity<AppComponent>(activity).plusVersionCheck().create().inject(this)
  }

  private fun handleUpgrade() {
    val act = activity.requireNotNull()
    upgradeViewModel
        .requireNotNull()
        .completeUpgrade(
            scope = act.lifecycleScope,
            onUpgradeComplete = {
              Logger.d("Upgrade complete, dismiss")
              act.finish()
            },
        )
  }

  /** Render into a composable the version check screen upsell */
  @Composable
  public fun Render(
      modifier: Modifier = Modifier,
  ) {
    upgradeViewModel.requireNotNull().Render { upgradeState ->
        checkViewModel.requireNotNull().Render { checkState ->
            composeTheme(activity.requireNotNull()) {
                VersionCheckScreen(
                    modifier = modifier,
                    versionCheckState = checkState,
                    versionUpgradeState = upgradeState,
                    appName = appName,
                    onUpgrade = { handleUpgrade() },
                )
            }
        }
    }
  }

  public companion object {

    /** Create a new version upgrade available UI component */
    @JvmStatic
    @CheckResult
    public fun create(
        activity: FragmentActivity,
        appName: String,
    ): VersionUpgradeAvailable {
      return VersionUpgradeAvailable(
          activity,
          appName,
      )
    }
  }
}
