/*
 * Copyright 2019 Peter Kenji Yamanaka
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
 *
 */

package com.pyamsoft.pydroid.ui.settings

import android.content.ActivityNotFoundException
import android.os.Bundle
import android.view.View
import androidx.annotation.CallSuper
import androidx.annotation.XmlRes
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.preference.PreferenceFragmentCompat
import com.pyamsoft.pydroid.arch.createComponent
import com.pyamsoft.pydroid.ui.Injector
import com.pyamsoft.pydroid.ui.PYDroidComponent
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.about.AboutFragment
import com.pyamsoft.pydroid.ui.app.ActivityBase
import com.pyamsoft.pydroid.ui.rating.ChangeLogProvider
import com.pyamsoft.pydroid.ui.rating.RatingControllerEvent.LoadRating
import com.pyamsoft.pydroid.ui.rating.RatingViewModel
import com.pyamsoft.pydroid.ui.rating.dialog.RatingDialog
import com.pyamsoft.pydroid.ui.settings.AppSettingsControllerEvent.AttemptCheckUpgrade
import com.pyamsoft.pydroid.ui.settings.AppSettingsControllerEvent.AttemptClearData
import com.pyamsoft.pydroid.ui.settings.AppSettingsControllerEvent.ChangeDarkTheme
import com.pyamsoft.pydroid.ui.settings.AppSettingsControllerEvent.NavigateHyperlink
import com.pyamsoft.pydroid.ui.settings.AppSettingsControllerEvent.NavigateMoreApps
import com.pyamsoft.pydroid.ui.settings.AppSettingsControllerEvent.NavigateRateApp
import com.pyamsoft.pydroid.ui.settings.AppSettingsControllerEvent.OpenShowUpgrade
import com.pyamsoft.pydroid.ui.settings.AppSettingsControllerEvent.ShowLicense
import com.pyamsoft.pydroid.ui.theme.Theming
import com.pyamsoft.pydroid.ui.util.MarketLinker
import com.pyamsoft.pydroid.ui.util.show
import com.pyamsoft.pydroid.ui.version.VersionCheckViewModel
import com.pyamsoft.pydroid.ui.version.VersionControllerEvent.ShowUpgrade
import com.pyamsoft.pydroid.ui.version.VersionView
import com.pyamsoft.pydroid.ui.version.upgrade.VersionUpgradeDialog
import com.pyamsoft.pydroid.util.HyperlinkIntent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import timber.log.Timber

abstract class AppSettingsPreferenceFragment : PreferenceFragmentCompat() {

  protected open val preferenceXmlResId: Int = 0

  protected open val hideUpgradeInformation: Boolean = false

  protected open val hideClearAll: Boolean = false

  internal var appSettingsViewModelFactory: ViewModelProvider.Factory? = null
  internal var appSettingsView: AppSettingsView? = null
  private var appSettingsViewModel: AppSettingsViewModel? = null

  internal var ratingViewModel: RatingViewModel? = null

  internal var versionView: VersionView? = null
  private var versionViewModel: VersionCheckViewModel? = null

  @CallSuper
  override fun onCreatePreferences(
    savedInstanceState: Bundle?,
    rootKey: String?
  ) {
    @XmlRes val xmlResId: Int = preferenceXmlResId
    if (xmlResId != 0) {
      addPreferencesFromResource(xmlResId)
    }
    addPreferencesFromResource(R.xml.pydroid)
  }

  @CallSuper
  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    super.onViewCreated(view, savedInstanceState)

    Injector.obtain<PYDroidComponent>(view.context.applicationContext)
        .plusSettingsComponent()
        .create(
            requireActivity(), listView, viewLifecycleOwner, preferenceScreen,
            hideClearAll, hideUpgradeInformation
        )
        .inject(this)

    ViewModelProviders.of(this, appSettingsViewModelFactory)
        .let { factory ->
          appSettingsViewModel = factory.get(AppSettingsViewModel::class.java)
          versionViewModel = factory.get(VersionCheckViewModel::class.java)
        }

    createComponent(
        savedInstanceState, viewLifecycleOwner,
        requireNotNull(appSettingsViewModel),
        requireNotNull(appSettingsView)
    ) {
      return@createComponent when (it) {
        is NavigateMoreApps -> viewMorePyamsoftApps()
        is NavigateHyperlink -> navigateHyperlink(it.hyperlinkIntent)
        is NavigateRateApp -> openMarkForRating()
        is ShowLicense -> openLicensesPage()
        is AttemptCheckUpgrade -> forceUpgradeCheck()
        is AttemptClearData -> openClearDataDialog()
        is OpenShowUpgrade -> requireNotNull(ratingViewModel).load(true)
        is ChangeDarkTheme -> darkThemeChanged(it.newMode)
      }
    }

    createComponent(
        savedInstanceState, viewLifecycleOwner,
        requireNotNull(versionViewModel),
        requireNotNull(versionView)
    ) {
      return@createComponent when (it) {
        is ShowUpgrade -> showVersionUpgrade(it.payload.newVersion)
      }
    }

    createComponent(
        savedInstanceState, viewLifecycleOwner,
        requireNotNull(ratingViewModel)
    ) {
      return@createComponent when (it) {
        is LoadRating -> openUpdateInfo()
      }
    }

    requireNotNull(appSettingsViewModel).initDarkThemeState(requireActivity())
  }

  private fun forceUpgradeCheck() {
    requireNotNull(versionViewModel).checkForUpdates(true)
  }

  private fun openUpdateInfo() {
    RatingDialog.newInstance(requireActivity() as ChangeLogProvider)
        .show(requireActivity(), RatingDialog.TAG)
  }

  private fun showVersionUpgrade(newVersion: Int) {
    VersionUpgradeDialog.newInstance(newVersion)
        .show(requireActivity(), VersionUpgradeDialog.TAG)
  }

  override fun onDestroyView() {
    super.onDestroyView()

    appSettingsViewModel = null
    appSettingsView = null
    appSettingsViewModelFactory = null
  }

  private fun failedNavigation(error: ActivityNotFoundException?) {
    if (error != null) {
      requireNotNull(appSettingsViewModel).navigationFailed(error)
    }
  }

  private fun viewMorePyamsoftApps() {
    val error = MarketLinker.linkToDeveloperPage(requireContext())
    failedNavigation(error)
  }

  private fun darkThemeChanged(mode: Theming.Mode) {
    onDarkThemeClicked(mode)
  }

  private fun openClearDataDialog() {
    onClearAllClicked()
  }

  private fun openLicensesPage() {
    onLicenseItemClicked()
  }

  private fun openMarkForRating() {
    requireContext().also { c ->
      val link = c.packageName
      val error = MarketLinker.linkToMarketPage(c, link)
      failedNavigation(error)
    }
  }

  private fun navigateHyperlink(link: HyperlinkIntent) {
    val error = link.navigate()
    failedNavigation(error)
  }

  @CallSuper
  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    appSettingsView?.saveState(outState)
  }

  /**
   * Logs when the Clear All option is clicked, override to use unique implementation
   */
  @CallSuper
  protected open fun onClearAllClicked() {
    Timber.d("Clear all preferences clicked")
  }

  /**
   * Toggles dark theme, override or extend to use unique implementation
   */
  @CallSuper
  protected open fun onDarkThemeClicked(mode: Theming.Mode) {
    Timber.d("Dark theme set: $mode")
    requireActivity().recreate()
  }

  /**
   * Shows a page for Open Source licenses, override or extend to use unique implementation
   */
  @CallSuper
  protected open fun onLicenseItemClicked() {
    val a = requireActivity()
    if (a is ActivityBase) {
      Timber.d("Show about licenses fragment")
      AboutFragment.show(a, a.fragmentContainerId)
    }
  }
}
