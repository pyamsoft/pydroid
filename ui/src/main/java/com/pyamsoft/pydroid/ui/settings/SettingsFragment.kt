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

package com.pyamsoft.pydroid.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.pyamsoft.pydroid.bootstrap.otherapps.api.OtherApp
import com.pyamsoft.pydroid.core.Logger
import com.pyamsoft.pydroid.core.ResultWrapper
import com.pyamsoft.pydroid.core.requireNotNull
import com.pyamsoft.pydroid.inject.Injector
import com.pyamsoft.pydroid.ui.PYDroidComponent
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.app.ComposeTheme
import com.pyamsoft.pydroid.ui.internal.app.NoopTheme
import com.pyamsoft.pydroid.ui.internal.changelog.ChangeLogViewModel
import com.pyamsoft.pydroid.ui.internal.otherapps.OtherAppsDialog
import com.pyamsoft.pydroid.ui.internal.rating.RatingViewModel
import com.pyamsoft.pydroid.ui.internal.settings.SettingsControllerEvent
import com.pyamsoft.pydroid.ui.internal.settings.SettingsScreen
import com.pyamsoft.pydroid.ui.internal.settings.SettingsViewModel
import com.pyamsoft.pydroid.ui.internal.version.VersionCheckViewModel
import com.pyamsoft.pydroid.ui.util.getDefaultSharedPreferencesName
import com.pyamsoft.pydroid.util.MarketLinker

/** Fragment for displaying a settings page */
public abstract class SettingsFragment : Fragment() {

  /** Hide upgrade */
  protected open val hideUpgradeInformation: Boolean = false

  /** Hide clear button */
  protected open val hideClearAll: Boolean = false

  /** May be provided by PYDroid, otherwise this is just a noop */
  internal var composeTheme: ComposeTheme = NoopTheme

  internal var factory: ViewModelProvider.Factory? = null
  private val viewModel by activityViewModels<SettingsViewModel> { factory.requireNotNull() }

  // Don't need to create a component or bind this to the controller, since RatingActivity should
  // be bound for us.
  internal var ratingFactory: ViewModelProvider.Factory? = null
  private val ratingViewModel by
      activityViewModels<RatingViewModel> { ratingFactory.requireNotNull() }

  // Don't need to create a component or bind this to the controller, since VersionCheckActivity
  // should
  // be bound for us.
  internal var versionFactory: ViewModelProvider.Factory? = null
  private val versionViewModel by
      activityViewModels<VersionCheckViewModel> { versionFactory.requireNotNull() }

  // Don't need to create a component or bind this to the controller, since ChangeLogActivity should
  // be bound for us.
  internal var changeLogFactory: ViewModelProvider.Factory? = null
  private val changeLogViewModel by
      activityViewModels<ChangeLogViewModel> { changeLogFactory.requireNotNull() }

  /** Provide a settings data store */
  protected open val settingsDataStore: DataStore<Preferences> by
      lazy(LazyThreadSafetyMode.NONE) {
        val act = requireActivity()
        return@lazy PreferenceDataStoreFactory.create {
          act.preferencesDataStoreFile(act.getDefaultSharedPreferencesName())
        }
      }

  /** Any user provided compose content */
  @Composable protected open fun Content() {}

  final override fun onCreateView(
      inflater: LayoutInflater,
      container: ViewGroup?,
      savedInstanceState: Bundle?
  ): View {
    val act = requireActivity()

    Injector.obtainFromApplication<PYDroidComponent>(act)
        .plusSettings()
        .create(
            hideClearAll = hideClearAll,
            hideUpgradeInformation = hideUpgradeInformation,
        )
        .inject(this)

    return ComposeView(act).apply {
      id = R.id.fragment_settings

      setContent {
        val state by viewModel.compose()

        composeTheme {
          Content()

          SettingsScreen(
              dataStore = settingsDataStore,
              state = state,
              onDarkModeChanged = {
                viewModel.handleChangeDarkMode(viewLifecycleOwner.lifecycleScope, it)
              },
          )
        }
      }
    }
  }

  @CallSuper
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    viewModel.bindController(viewLifecycleOwner) { event ->
      return@bindController when (event) {
        is SettingsControllerEvent.NavigateDeveloperPage -> openDeveloperPage()
        is SettingsControllerEvent.OpenOtherAppsScreen -> openOtherAppsPage(event.others)
      }
    }
  }

  private fun openDeveloperPage() {
    MarketLinker.linkToDeveloperPage(requireContext()).handleNavigation()
  }

  private fun openOtherAppsPage(apps: List<OtherApp>) {
    Logger.d("Show other apps fragment: $apps")
    OtherAppsDialog.show(requireActivity())
  }

  private fun ResultWrapper<Unit>.handleNavigation() {
    this.onSuccess { viewModel.handleNavigationSuccess() }
        .onFailure { Logger.e(it, "Failed to navigate hyperlink") }
        .onFailure { viewModel.handleNavigationFailed(it) }
  }
}
