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

package com.pyamsoft.pydroid.ui.settings

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.annotation.CheckResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.Dp
import androidx.fragment.app.Fragment
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.internal.app.ComposeTheme
import com.pyamsoft.pydroid.ui.internal.app.NoopTheme
import com.pyamsoft.pydroid.ui.internal.app.invoke
import com.pyamsoft.pydroid.ui.internal.pydroid.ObjectGraph
import com.pyamsoft.pydroid.ui.preference.Preferences
import com.pyamsoft.pydroid.ui.theme.ZeroElevation
import com.pyamsoft.pydroid.ui.util.dispose
import com.pyamsoft.pydroid.ui.util.recompose
import com.pyamsoft.pydroid.ui.util.rememberAsStateList

/** Fragment for displaying a settings page */
@Deprecated("Start migrating to Compose and use SettingsPage")
public abstract class SettingsFragment : Fragment() {

  /** May be provided by PYDroid, otherwise this is just a noop */
  internal var composeTheme: ComposeTheme = NoopTheme

  /** Hide upgrade */
  protected abstract val hideUpgradeInformation: Boolean

  /** Hide clear button */
  protected abstract val hideClearAll: Boolean

  /** Override this method to implement any custom preferences in your app */
  @Composable @CheckResult protected abstract fun customPrePreferences(): List<Preferences>

  /** Override this method to implement any custom preferences in your app */
  @Composable @CheckResult protected abstract fun customPostPreferences(): List<Preferences>

  /** Override this method to add additional margin to the top settings item */
  @Composable @CheckResult protected abstract fun customTopItemMargin(): Dp

  /** Override this method to add additional margin to the top settings item */
  @Composable @CheckResult protected abstract fun customBottomItemMargin(): Dp

  /** Override this method to add additional margin to the top settings item */
  @Composable
  @CheckResult
  protected open fun customElevation(): Dp {
    return ZeroElevation
  }

  final override fun onCreateView(
      inflater: LayoutInflater,
      container: ViewGroup?,
      savedInstanceState: Bundle?
  ): View {
    val act = requireActivity()

    ObjectGraph.ActivityScope.retrieve(act).injector().plusSettings().create().inject(this)

    return ComposeView(act).apply {
      id = R.id.fragment_settings

      setContent {
        composeTheme(act) {
          SettingsPage(
              hideClearAll = hideClearAll,
              hideUpgradeInformation = hideUpgradeInformation,
              customPrePreferences = customPrePreferences().rememberAsStateList(),
              customPostPreferences = customPostPreferences().rememberAsStateList(),
              customTopItemMargin = customTopItemMargin(),
              customBottomItemMargin = customBottomItemMargin(),
              customElevation = customElevation(),
          )
        }
      }
    }
  }

  @CallSuper
  override fun onConfigurationChanged(newConfig: Configuration) {
    super.onConfigurationChanged(newConfig)
    recompose()
  }

  @CallSuper
  override fun onDestroyView() {
    super.onDestroyView()
    dispose()
  }
}
