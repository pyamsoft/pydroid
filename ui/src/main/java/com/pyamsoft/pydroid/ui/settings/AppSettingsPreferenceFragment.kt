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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.annotation.XmlRes
import androidx.preference.PreferenceFragmentCompat
import com.pyamsoft.pydroid.ui.PYDroid
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.about.AboutFragment
import com.pyamsoft.pydroid.ui.app.ActivityBase
import com.pyamsoft.pydroid.ui.navigation.FailedNavigationPresenter
import com.pyamsoft.pydroid.ui.rating.RatingPresenter
import com.pyamsoft.pydroid.ui.settings.AppSettingsPresenter.Callback
import com.pyamsoft.pydroid.ui.util.MarketLinker
import com.pyamsoft.pydroid.ui.version.VersionCheckPresenter
import com.pyamsoft.pydroid.util.HyperlinkIntent
import timber.log.Timber

abstract class AppSettingsPreferenceFragment : PreferenceFragmentCompat(), Callback,
    VersionCheckPresenter.Callback {

  protected open val preferenceXmlResId: Int = 0

  protected open val hideUpgradeInformation: Boolean = false

  protected open val hideClearAll: Boolean = false

  internal lateinit var settingsView: AppSettingsView
  internal lateinit var versionPresenter: VersionCheckPresenter
  internal lateinit var ratingPresenter: RatingPresenter
  internal lateinit var settingsPresenter: AppSettingsPresenter
  internal lateinit var failedNavPresenter: FailedNavigationPresenter

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
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    val view = requireNotNull(super.onCreateView(inflater, container, savedInstanceState))

    PYDroid.obtain(requireContext())
        .plusSettingsComponent(preferenceScreen, hideClearAll, hideUpgradeInformation)
        .inject(this)

    return view
  }

  @CallSuper
  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    super.onViewCreated(view, savedInstanceState)
    settingsView.inflate(savedInstanceState)
    versionPresenter.bind(viewLifecycleOwner, this)
    settingsPresenter.bind(viewLifecycleOwner, this)
  }

  final override fun onVersionCheckBegin(forced: Boolean) {
    Timber.d("onVersionCheckBegin handled by VersionActivity")
  }

  final override fun onVersionCheckFound(
    currentVersion: Int,
    newVersion: Int
  ) {
    Timber.d("onVersionCheckFound handled by VersionActivity")
  }

  final override fun onVersionCheckError(throwable: Throwable) {
    Timber.d("onVersionCheckError handled by VersionActivity")
  }

  final override fun onVersionCheckComplete() {
    Timber.d("onVersionCheckComplete handled by VersionActivity")
  }

  final override fun onViewMorePyamsoftApps() {
    MarketLinker.linkToDeveloperPage(requireContext()) { failedNavPresenter.failedNavigation(it) }
  }

  final override fun onShowUpgradeInfo() {
    onShowChangelogClicked()
  }

  final override fun onDarkThemeChanged(dark: Boolean) {
    onDarkThemeClicked(dark)
  }

  final override fun onShowSocialMedia(link: HyperlinkIntent) {
    navigateToUrl(link)
  }

  final override fun onClearAppData() {
    onClearAllClicked()
  }

  final override fun onCheckUpgrade() {
    onCheckForUpdatesClicked()
  }

  final override fun onViewLicenses() {
    onLicenseItemClicked()
  }

  final override fun onOpenBugReport(link: HyperlinkIntent) {
    navigateToUrl(link)
  }

  final override fun onRateApp() {
    requireContext().also { c ->
      val link = c.packageName
      MarketLinker.linkToMarketPage(c, link) { failedNavPresenter.failedNavigation(it) }
    }
  }

  final override fun onShowBlog(link: HyperlinkIntent) {
    navigateToUrl(link)
  }

  private fun navigateToUrl(link: HyperlinkIntent) {
    link.navigate { failedNavPresenter.failedNavigation(it) }
  }

  @CallSuper
  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    settingsView.saveState(outState)
  }

  @CallSuper
  override fun onDestroyView() {
    super.onDestroyView()
    settingsView.teardown()
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

  /**
   * Shows the changelog, override or extend to use unique implementation
   */
  @CallSuper
  protected open fun onShowChangelogClicked() {
    ratingPresenter.load(true)
  }

  /**
   * Checks the server for updates, override or extend to use unique implementation
   */
  @CallSuper
  protected open fun onCheckForUpdatesClicked() {
    versionPresenter.checkForUpdates(true)
  }
}
