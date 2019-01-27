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
import com.pyamsoft.pydroid.core.singleDisposable
import com.pyamsoft.pydroid.core.tryDispose
import com.pyamsoft.pydroid.ui.PYDroid
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.about.AboutFragment
import com.pyamsoft.pydroid.ui.app.activity.ActivityBase
import com.pyamsoft.pydroid.ui.app.fragment.ToolbarPreferenceFragment
import com.pyamsoft.pydroid.ui.arch.destroy
import com.pyamsoft.pydroid.ui.rating.RatingWorker
import com.pyamsoft.pydroid.ui.settings.AppSettingsViewEvent.BugReportClicked
import com.pyamsoft.pydroid.ui.settings.AppSettingsViewEvent.CheckUpgrade
import com.pyamsoft.pydroid.ui.settings.AppSettingsViewEvent.ClearAppData
import com.pyamsoft.pydroid.ui.settings.AppSettingsViewEvent.DarkTheme
import com.pyamsoft.pydroid.ui.settings.AppSettingsViewEvent.FollowBlogClicked
import com.pyamsoft.pydroid.ui.settings.AppSettingsViewEvent.FollowSocialClicked
import com.pyamsoft.pydroid.ui.settings.AppSettingsViewEvent.LicenseClicked
import com.pyamsoft.pydroid.ui.settings.AppSettingsViewEvent.MoreAppsClicked
import com.pyamsoft.pydroid.ui.settings.AppSettingsViewEvent.RateAppClicked
import com.pyamsoft.pydroid.ui.settings.AppSettingsViewEvent.ShowUpgradeInfo
import com.pyamsoft.pydroid.ui.theme.Theming
import com.pyamsoft.pydroid.ui.util.MarketLinker
import com.pyamsoft.pydroid.ui.version.VersionCheckWorker
import com.pyamsoft.pydroid.util.HyperlinkIntent
import timber.log.Timber

abstract class AppSettingsPreferenceFragment : ToolbarPreferenceFragment() {

  internal lateinit var theming: Theming
  internal lateinit var versionWorker: VersionCheckWorker
  internal lateinit var ratingWorker: RatingWorker
  internal lateinit var settingsWorker: AppSettingsWorker
  internal lateinit var settingsComponent: AppSettingsUiComponent

  private var ratingDisposable by singleDisposable()
  private var checkUpdatesDisposable by singleDisposable()

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
        .plusSettingsComponent(
            view, viewLifecycleOwner, preferenceScreen,
            hideClearAll, hideUpgradeInformation
        )
        .inject(this)
    return view
  }

  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    super.onViewCreated(view, savedInstanceState)
    settingsComponent.onUiEvent {
      return@onUiEvent when (it) {
        is ShowUpgradeInfo -> onShowChangelogClicked()
        is RateAppClicked -> rateAppClicked()
        is MoreAppsClicked -> moreAppsClicked()
        is FollowSocialClicked -> navigateToUrl(it.link)
        is FollowBlogClicked -> navigateToUrl(it.link)
        is BugReportClicked -> navigateToUrl(it.link)
        is LicenseClicked -> onLicenseItemClicked()
        is CheckUpgrade -> onCheckForUpdatesClicked()
        is ClearAppData -> onClearAllClicked()
        is DarkTheme -> onDarkThemeClicked(it.dark)
      }
    }
        .destroy(viewLifecycleOwner)

    settingsComponent.create(savedInstanceState)
  }

  private fun navigateToUrl(link: HyperlinkIntent) {
    link.navigate { settingsWorker.failedLinkUrl(it) }
  }

  private fun moreAppsClicked() {
    MarketLinker.linkToDeveloperPage(requireContext()) { settingsWorker.failedLinkUrl(it) }
  }

  private fun rateAppClicked() {
    val link = requireContext().packageName
    MarketLinker.linkToMarketPage(requireContext(), link) { settingsWorker.failedLinkUrl(it) }
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    settingsComponent.saveState(outState)
  }

  override fun onDestroyView() {
    super.onDestroyView()
    ratingDisposable.tryDispose()
    checkUpdatesDisposable.tryDispose()
  }

  /**
   * Logs when the Clear All option is clicked, override to use unique implementation
   */
  protected open fun onClearAllClicked() {
    Timber.d("Clear all preferences clicked")
  }

  /**
   * Toggles dark theme, override or extend to use unique implementation
   */
  @CallSuper
  protected open fun onDarkThemeClicked(dark: Boolean) {
    Timber.d("Dark theme set: $dark")
    theming.setDarkTheme(dark)
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
    ratingDisposable = ratingWorker.loadRatingDialog(true)
  }

  /**
   * Checks the server for updates, override or extend to use unique implementation
   */
  @CallSuper
  protected open fun onCheckForUpdatesClicked() {
    checkUpdatesDisposable = versionWorker.checkForUpdates(true)
  }

  protected open val preferenceXmlResId: Int = 0

  protected open val hideUpgradeInformation: Boolean = false

  protected open val hideClearAll: Boolean = false
}
