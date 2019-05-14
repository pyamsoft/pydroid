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
import androidx.preference.PreferenceFragmentCompat
import com.pyamsoft.pydroid.arch.impl.createComponent
import com.pyamsoft.pydroid.arch.impl.doOnDestroy
import com.pyamsoft.pydroid.core.tryDispose
import com.pyamsoft.pydroid.ui.Injector
import com.pyamsoft.pydroid.ui.PYDroidComponent
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.about.AboutFragment
import com.pyamsoft.pydroid.ui.app.ActivityBase
import com.pyamsoft.pydroid.ui.rating.ChangeLogProvider
import com.pyamsoft.pydroid.ui.rating.RatingControllerEvent.ShowDialog
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
import com.pyamsoft.pydroid.ui.util.MarketLinker
import com.pyamsoft.pydroid.ui.util.show
import com.pyamsoft.pydroid.ui.version.VersionCheckViewModel
import com.pyamsoft.pydroid.ui.version.VersionControllerEvent.ShowUpgrade
import com.pyamsoft.pydroid.ui.version.VersionView
import com.pyamsoft.pydroid.ui.version.upgrade.VersionUpgradeDialog
import com.pyamsoft.pydroid.util.HyperlinkIntent
import timber.log.Timber

abstract class AppSettingsPreferenceFragment : PreferenceFragmentCompat() {

  protected open val preferenceXmlResId: Int = 0

  protected open val hideUpgradeInformation: Boolean = false

  protected open val hideClearAll: Boolean = false

  internal var appSettingsViewModel: AppSettingsViewModel? = null
  internal var appSettingsView: AppSettingsView? = null

  internal var ratingViewModel: RatingViewModel? = null

  internal var versionView: VersionView? = null
  internal var versionViewModel: VersionCheckViewModel? = null

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
            listView, viewLifecycleOwner, preferenceScreen,
            hideClearAll, hideUpgradeInformation
        )
        .inject(this)

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
        is OpenShowUpgrade -> forceInfoShow()
        is ChangeDarkTheme -> darkThemeChanged(it.isDark)
      }
    }

    createComponent(
        savedInstanceState, this,
        requireNotNull(versionViewModel),
        requireNotNull(versionView)
    ) {
      return@createComponent when (it) {
        is ShowUpgrade -> showVersionUpgrade(it.payload.newVersion)
      }
    }

    val disposable = requireNotNull(ratingViewModel).render {
      return@render when (it) {
        is ShowDialog -> openUpdateInfo()
      }
    }

    viewLifecycleOwner.doOnDestroy {
      disposable.tryDispose()
    }
  }

  private fun forceUpgradeCheck() {
    requireNotNull(versionViewModel).checkForUpdates(true)
  }

  private fun forceInfoShow() {
    requireNotNull(ratingViewModel).load(true)
  }

  private fun showVersionUpgrade(newVersion: Int) {
    VersionUpgradeDialog.newInstance(newVersion)
        .show(requireActivity(), VersionUpgradeDialog.TAG)
  }

  private fun openUpdateInfo() {
    RatingDialog.newInstance(requireActivity() as ChangeLogProvider)
        .show(requireActivity(), RatingDialog.TAG)
  }

  override fun onDestroyView() {
    super.onDestroyView()

    appSettingsViewModel = null
    appSettingsView = null
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

  private fun darkThemeChanged(dark: Boolean) {
    onDarkThemeClicked(dark)
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
  protected open fun onDarkThemeClicked(dark: Boolean) {
    Timber.d("Dark theme set: $dark")
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
