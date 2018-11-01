/*
 * Copyright (C) 2018 Peter Kenji Yamanaka
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pyamsoft.pydroid.ui.app.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.annotation.CheckResult
import androidx.annotation.IdRes
import androidx.annotation.XmlRes
import androidx.core.content.ContextCompat
import androidx.preference.Preference
import com.pyamsoft.pydroid.bootstrap.rating.RatingViewModel
import com.pyamsoft.pydroid.bootstrap.version.VersionCheckProvider
import com.pyamsoft.pydroid.bootstrap.version.VersionCheckViewModel
import com.pyamsoft.pydroid.ui.PYDroid
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.about.AboutLibrariesFragment
import com.pyamsoft.pydroid.ui.util.MarketLinker
import com.pyamsoft.pydroid.ui.util.navigate
import com.pyamsoft.pydroid.ui.version.VersionCheckActivity
import com.pyamsoft.pydroid.util.hyperlink
import com.pyamsoft.pydroid.util.tintWith
import timber.log.Timber

abstract class SettingsPreferenceFragment : ToolbarPreferenceFragment() {

  internal lateinit var versionViewModel: VersionCheckViewModel
  internal lateinit var ratingViewModel: RatingViewModel

  private lateinit var applicationSettings: Preference
  private lateinit var upgradeInfo: Preference
  private lateinit var clearAll: Preference
  private lateinit var checkVersion: Preference
  private lateinit var showAboutLicenses: Preference
  private lateinit var rateApplication: Preference
  private lateinit var moreApps: Preference
  private lateinit var followSocialMedia: Preference
  private lateinit var followBlog: Preference

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
    PYDroid.obtain(requireContext())
        .plusAppComponent(viewLifecycleOwner, versionedActivity.currentApplicationVersion)
        .inject(this)

    val view = requireNotNull(super.onCreateView(inflater, container, savedInstanceState))
    applicationSettings = findPreference("application_settings")
    upgradeInfo = findPreference(getString(R.string.upgrade_info_key))
    clearAll = findPreference(getString(R.string.clear_all_key))
    checkVersion = findPreference(getString(R.string.check_version_key))
    showAboutLicenses = findPreference(getString(R.string.about_license_key))
    rateApplication = findPreference(getString(R.string.rating_key))
    moreApps = findPreference(getString(R.string.more_apps_key))
    followSocialMedia = findPreference(getString(R.string.social_media_f_key))
    followBlog = findPreference(getString(R.string.social_media_b_key))

    setupApplicationTitle()
    setupUpgradeInfo()
    setupClearAll()
    setupCheckVersion()
    setupAboutLicenses()
    setupRateApp(view)
    setupMoreApps(view)
    setupFollows(view)

    adjustIconTint(isDarkTheme)

    return view
  }

  private fun adjustIconTint(darkTheme: Boolean) {
    applicationSettings.adjustTint(darkTheme)
    upgradeInfo.adjustTint(darkTheme)
    clearAll.adjustTint(darkTheme)
    checkVersion.adjustTint(darkTheme)
    showAboutLicenses.adjustTint(darkTheme)
    rateApplication.adjustTint(darkTheme)
    moreApps.adjustTint(darkTheme)
    followSocialMedia.adjustTint(darkTheme)
    followBlog.adjustTint(darkTheme)
  }

  private fun Preference.adjustTint(darkTheme: Boolean) {
    val icon = this.icon
    if (icon != null) {
      this.icon = icon.tintWith(
          ContextCompat.getColor(
              context,
              if (darkTheme) R.color.white else R.color.black
          )
      )
    }
  }

  private fun setupMoreApps(view: View) {
    moreApps.setOnPreferenceClickListener {
      MarketLinker.linkToDeveloperPage(view)
      return@setOnPreferenceClickListener true
    }
  }

  private fun setupFollows(view: View) {
    followBlog.setOnPreferenceClickListener {
      BLOG.hyperlink(view.context)
          .navigate(view)
      return@setOnPreferenceClickListener true
    }

    followSocialMedia.setOnPreferenceClickListener {
      FACEBOOK.hyperlink(view.context)
          .navigate(view)
      return@setOnPreferenceClickListener true
    }
  }

  private fun setupRateApp(view: View) {
    rateApplication.setOnPreferenceClickListener { _ ->
      MarketLinker.linkToMarketPage(view.context.packageName, view)
      return@setOnPreferenceClickListener true
    }
  }

  private fun setupAboutLicenses() {
    showAboutLicenses.setOnPreferenceClickListener {
      onLicenseItemClicked()
      return@setOnPreferenceClickListener true
    }
  }

  private fun setupCheckVersion() {
    checkVersion.setOnPreferenceClickListener {
      onCheckForUpdatesClicked(versionViewModel)
      return@setOnPreferenceClickListener true
    }
  }

  private fun setupClearAll() {
    if (hideClearAll) {
      clearAll.isVisible = false
    } else {
      clearAll.setOnPreferenceClickListener {
        onClearAllClicked()
        return@setOnPreferenceClickListener true
      }
    }
  }

  private fun setupUpgradeInfo() {
    if (hideUpgradeInformation) {
      upgradeInfo.isVisible = false
    } else {
      upgradeInfo.setOnPreferenceClickListener {
        onShowChangelogClicked()
        return@setOnPreferenceClickListener true
      }
    }
  }

  private fun setupApplicationTitle() {
    applicationSettings.title = "$applicationName Settings"
  }

  /**
   * Logs when the Clear All option is clicked, override to use unique implementation
   */
  protected open fun onClearAllClicked() {
    Timber.d("Clear all preferences clicked")
  }

  /**
   * Shows a page for Open Source licenses, override or extend to use unique implementation
   */
  @CallSuper
  protected open fun onLicenseItemClicked() {
    activity?.also {
      Timber.d("Show about licenses fragment")
      AboutLibrariesFragment.show(it, rootViewContainer)
    }
  }

  /**
   * Shows the changelog, override or extend to use unique implementation
   */
  protected open fun onShowChangelogClicked() {
    ratingViewModel.loadRatingDialog(true)
  }

  /**
   * Checks the server for updates, override to use a custom behavior
   */
  protected open fun onCheckForUpdatesClicked(viewModel: VersionCheckViewModel) {
    viewModel.checkForUpdates(true)
  }

  private val versionedActivity: VersionCheckProvider
    @CheckResult get() {
      val activity = activity
      if (activity is VersionCheckActivity) {
        return activity
      } else {
        throw IllegalStateException("Activity is not VersionCheckActivity")
      }
    }

  protected open val preferenceXmlResId: Int = 0

  protected open val hideUpgradeInformation: Boolean = false

  protected open val hideClearAll: Boolean = false

  @get:CheckResult
  protected abstract val isDarkTheme: Boolean

  @get:[CheckResult IdRes]
  protected abstract val rootViewContainer: Int

  @get:CheckResult
  protected abstract val applicationName: String

  companion object {

    private const val FACEBOOK = "https://www.facebook.com/pyamsoftware"
    private const val BLOG = "https://pyamsoft.blogspot.com/"
  }
}
