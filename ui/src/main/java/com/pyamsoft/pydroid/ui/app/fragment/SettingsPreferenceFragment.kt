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
import androidx.preference.PreferenceGroup
import com.pyamsoft.pydroid.bootstrap.rating.RatingViewModel
import com.pyamsoft.pydroid.bootstrap.version.VersionCheckProvider
import com.pyamsoft.pydroid.bootstrap.version.VersionCheckViewModel
import com.pyamsoft.pydroid.ui.PYDroid
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.about.AboutLibrariesFragment
import com.pyamsoft.pydroid.ui.theme.Theming
import com.pyamsoft.pydroid.ui.util.MarketLinker
import com.pyamsoft.pydroid.ui.util.navigate
import com.pyamsoft.pydroid.ui.version.VersionCheckActivity
import com.pyamsoft.pydroid.util.hyperlink
import com.pyamsoft.pydroid.util.tintWith
import timber.log.Timber

abstract class SettingsPreferenceFragment : ToolbarPreferenceFragment() {

  internal lateinit var versionViewModel: VersionCheckViewModel
  internal lateinit var ratingViewModel: RatingViewModel
  internal lateinit var theming: Theming

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
    return requireNotNull(super.onCreateView(inflater, container, savedInstanceState))
  }

  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    super.onViewCreated(view, savedInstanceState)
    adjustIconTint()
    setupApplicationTitle()
    setupUpgradeInfo()
    setupClearAll()
    setupCheckVersion()
    setupAboutLicenses()
    setupRateApp(view)
    setupBugreport(view)
    setupMoreApps(view)
    setupFollows(view)
  }

  private fun adjustIconTint() {
    val darkTheme = theming.isDarkTheme()
    preferenceScreen.adjustTint(darkTheme)
  }

  private fun PreferenceGroup.adjustTint(darkTheme: Boolean) {
    val size = preferenceCount
    for (i in 0 until size) {
      val pref = getPreference(i)
      if (pref is PreferenceGroup) {
        pref.adjustTint(darkTheme)
      } else {
        pref.adjustTint(darkTheme)
      }
    }
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
    val moreApps = findPreference(getString(R.string.more_apps_key))
    moreApps.setOnPreferenceClickListener {
      MarketLinker.linkToDeveloperPage(view)
      return@setOnPreferenceClickListener true
    }
  }

  private fun setupFollows(view: View) {
    val followSocialMedia = findPreference(getString(R.string.social_media_f_key))
    val followBlog = findPreference(getString(R.string.social_media_b_key))
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
    val rateApplication = findPreference(getString(R.string.rating_key))
    rateApplication.setOnPreferenceClickListener {
      MarketLinker.linkToMarketPage(view.context.packageName, view)
      return@setOnPreferenceClickListener true
    }
  }

  private fun setupBugreport(view: View) {
    val reportUrl = bugreportUrl.hyperlink(view.context)
    val bugreport = findPreference(getString(R.string.bugreport_key))
    bugreport.setOnPreferenceClickListener {
      reportUrl.navigate(view)
      return@setOnPreferenceClickListener true
    }
  }

  private fun setupAboutLicenses() {
    val showAboutLicenses = findPreference(getString(R.string.about_license_key))
    showAboutLicenses.setOnPreferenceClickListener {
      onLicenseItemClicked()
      return@setOnPreferenceClickListener true
    }
  }

  private fun setupCheckVersion() {
    val checkVersion = findPreference(getString(R.string.check_version_key))
    checkVersion.setOnPreferenceClickListener {
      onCheckForUpdatesClicked(versionViewModel)
      return@setOnPreferenceClickListener true
    }
  }

  private fun setupClearAll() {
    val clearAll = findPreference(getString(R.string.clear_all_key))
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
    val upgradeInfo = findPreference(getString(R.string.upgrade_info_key))
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
    val applicationSettings = findPreference("application_settings")
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

  @get:[CheckResult IdRes]
  protected abstract val rootViewContainer: Int

  @get:CheckResult
  protected abstract val applicationName: String

  @get:CheckResult
  protected abstract val bugreportUrl: String

  companion object {

    private const val FACEBOOK = "https://www.facebook.com/pyamsoftware"
    private const val BLOG = "https://pyamsoft.blogspot.com/"
  }
}
