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
import android.view.View
import androidx.annotation.CallSuper
import androidx.annotation.XmlRes
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceFragmentCompat
import com.pyamsoft.pydroid.arch.StateSaver
import com.pyamsoft.pydroid.arch.createComponent
import com.pyamsoft.pydroid.arch.newUiController
import com.pyamsoft.pydroid.bootstrap.otherapps.api.OtherApp
import com.pyamsoft.pydroid.core.ResultWrapper
import com.pyamsoft.pydroid.inject.Injector
import com.pyamsoft.pydroid.ui.PYDroidComponent
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.arch.fromViewModelFactory
import com.pyamsoft.pydroid.ui.internal.about.AboutDialog
import com.pyamsoft.pydroid.ui.internal.billing.BillingDialog
import com.pyamsoft.pydroid.ui.internal.changelog.ChangeLogViewModel
import com.pyamsoft.pydroid.ui.internal.otherapps.OtherAppsDialog
import com.pyamsoft.pydroid.ui.internal.rating.RatingViewModel
import com.pyamsoft.pydroid.ui.internal.settings.AppSettingsControllerEvent
import com.pyamsoft.pydroid.ui.internal.settings.AppSettingsView
import com.pyamsoft.pydroid.ui.internal.settings.AppSettingsViewEvent
import com.pyamsoft.pydroid.ui.internal.settings.AppSettingsViewModel
import com.pyamsoft.pydroid.ui.internal.settings.clear.SettingsClearConfigDialog
import com.pyamsoft.pydroid.ui.internal.version.VersionCheckView
import com.pyamsoft.pydroid.ui.internal.version.VersionCheckViewModel
import com.pyamsoft.pydroid.ui.theme.Theming
import com.pyamsoft.pydroid.ui.util.removeAllItemDecorations
import com.pyamsoft.pydroid.ui.util.show
import com.pyamsoft.pydroid.util.HyperlinkIntent
import com.pyamsoft.pydroid.util.MarketLinker
import timber.log.Timber

/** Preference fragment level for displaying a preference screen */
public abstract class AppSettingsPreferenceFragment : PreferenceFragmentCompat() {

  /** XML resource id */
  protected open val preferenceXmlResId: Int = 0

  /** Hide upgrade */
  protected open val hideUpgradeInformation: Boolean = false

  /** Hide clear button */
  protected open val hideClearAll: Boolean = false

  private var settingsStateSaver: StateSaver? = null
  private var ratingStateSaver: StateSaver? = null
  private var versionStateSaver: StateSaver? = null

  internal var settingsView: AppSettingsView? = null

  internal var versionCheckView: VersionCheckView? = null

  internal var factory: ViewModelProvider.Factory? = null
  private val settingsViewModel by fromViewModelFactory<AppSettingsViewModel>(activity = true) {
    factory
  }
  // Don't need to create a component or bind this to the controller, since RatingActivity should
  // be bound for us.
  private val ratingViewModel by fromViewModelFactory<RatingViewModel>(activity = true) { factory }

  // Don't need to create a component or bind this to the controller, since RatingActivity should
  // be bound for us.
  private val versionViewModel by fromViewModelFactory<VersionCheckViewModel>(activity = true) {
    factory
  }

  // Don't need to create a component or bind this to the controller, since RatingActivity should
  // be bound for us.
  private val changeLogViewModel by fromViewModelFactory<ChangeLogViewModel>(activity = true) {
    factory
  }

  /** On inflate preferences */
  @CallSuper
  override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
    @XmlRes val xmlResId: Int = preferenceXmlResId
    if (xmlResId != 0) {
      addPreferencesFromResource(xmlResId)
    }

    addPreferencesFromResource(R.xml.settings)
    addPreferencesFromResource(R.xml.support)
    addPreferencesFromResource(R.xml.ad)
    addPreferencesFromResource(R.xml.social)
  }

  /** On create */
  @CallSuper
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    Injector.obtainFromApplication<PYDroidComponent>(view.context)
        .plusSettings()
        .create(viewLifecycleOwner, preferenceScreen, hideClearAll, hideUpgradeInformation) {
          listView
        }
        .inject(this)

    settingsStateSaver =
        createComponent(
            savedInstanceState,
            viewLifecycleOwner,
            settingsViewModel,
            controller =
                newUiController {
                  return@newUiController when (it) {
                    is AppSettingsControllerEvent.DarkModeChanged -> darkThemeChanged(it.newMode)
                    is AppSettingsControllerEvent.NavigateDeveloperPage -> openDeveloperPage()
                    is AppSettingsControllerEvent.OpenOtherAppsScreen ->
                        openOtherAppsPage(it.others)
                  }
                },
            requireNotNull(settingsView)) {
          return@createComponent when (it) {
            is AppSettingsViewEvent.CheckUpgrade -> versionViewModel.handleCheckForUpdates(true)
            is AppSettingsViewEvent.ClearData -> openClearDataDialog()
            is AppSettingsViewEvent.Hyperlink -> navigateHyperlink(it.hyperlinkIntent)
            is AppSettingsViewEvent.MoreApps -> settingsViewModel.handleSeeMoreApps()
            is AppSettingsViewEvent.RateApp -> ratingViewModel.loadMarketPage()
            is AppSettingsViewEvent.ShowDonate -> openDonationDialog()
            is AppSettingsViewEvent.ShowUpgrade -> changeLogViewModel.show(true)
            is AppSettingsViewEvent.ToggleDarkTheme ->
                settingsViewModel.handleChangeDarkMode(it.mode)
            is AppSettingsViewEvent.ViewLicense -> openLicensesPage()
          }
        }

    settingsViewModel.handleSyncDarkThemeState(viewLifecycleOwner.lifecycleScope, requireActivity())
  }

  private fun openDonationDialog() {
    Timber.d("Launch donation dialog")
    BillingDialog.open(requireActivity())
  }

  private fun openOtherAppsPage(apps: List<OtherApp>) {
    onViewMorePyamsoftAppsClicked(false)
    Timber.d("Show other apps fragment: $apps")
    OtherAppsDialog().show(requireActivity(), OtherAppsDialog.TAG)
  }

  /** On destroy */
  override fun onDestroyView() {
    super.onDestroyView()
    settingsView = null
    factory = null
    settingsStateSaver = null
    ratingStateSaver = null
    versionStateSaver = null

    // Clear list view
    listView?.removeAllItemDecorations()
  }

  private fun ResultWrapper<Unit>.handleNavigation() {
    this.onSuccess { settingsViewModel.handleNavigationSuccess() }.onFailure {
      settingsViewModel.handleNavigationFailed(it)
    }
  }

  private fun openDeveloperPage() {
    onViewMorePyamsoftAppsClicked(true)
    MarketLinker.linkToDeveloperPage(requireContext()).handleNavigation()
  }

  private fun darkThemeChanged(mode: Theming.Mode) {
    onDarkThemeClicked(mode)
  }

  private fun openClearDataDialog() {
    onClearAllPrompt()
  }

  /**
   * Logs when the Clear All option is clicked, override to use unique implementation
   *
   * NOTE: In the future this method will be going away as the clear all flow will be handled by the
   * library. Custom clear logic in the middle will be supported but the UI for the prompt, and the
   * end result of clearing user application data will be enforced by the library.
   */
  protected open fun onClearAllPrompt() {
    SettingsClearConfigDialog.newInstance().show(requireActivity(), SettingsClearConfigDialog.TAG)
  }

  private fun openLicensesPage() {
    onLicenseItemClicked()
  }

  private fun navigateHyperlink(link: HyperlinkIntent) {
    link.navigate().handleNavigation()
  }

  /** On save instance state */
  @CallSuper
  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    settingsStateSaver?.saveState(outState)
    ratingStateSaver?.saveState(outState)
    versionStateSaver?.saveState(outState)
  }

  /** Toggles dark theme, override or extend to use unique implementation */
  @CallSuper
  protected open fun onDarkThemeClicked(mode: Theming.Mode) {
    Timber.d("Dark theme set: $mode")
  }

  /** Shows a page for Open Source licenses, override or extend to use unique implementation */
  @CallSuper
  protected open fun onLicenseItemClicked() {
    Timber.d("Show about licenses fragment")
    AboutDialog().show(requireActivity(), AboutDialog.TAG)
  }

  /** Shows a page for Source licenses, override or extend to use unique implementation */
  protected open fun onViewMorePyamsoftAppsClicked(navigatingAway: Boolean) {}
}
