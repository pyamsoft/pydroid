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
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.preference.Preference
import androidx.preference.PreferenceGroup
import androidx.preference.PreferenceScreen
import com.pyamsoft.pydroid.core.bus.Publisher
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.arch.InvalidUiComponentIdException
import com.pyamsoft.pydroid.ui.arch.PrefUiView
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
import com.pyamsoft.pydroid.ui.util.Snackbreak
import com.pyamsoft.pydroid.util.hyperlink
import com.pyamsoft.pydroid.util.tintWith

internal class AppSettingsView internal constructor(
  private val view: View,
  private val theming: Theming,
  private val applicationName: String,
  private val bugreportUrl: String,
  private val hideClearAll: Boolean,
  private val hideUpgradeInformation: Boolean,
  private val owner: LifecycleOwner,
  preferenceScreen: PreferenceScreen,
  uiBus: Publisher<AppSettingsViewEvent>
) : PrefUiView<AppSettingsViewEvent>(preferenceScreen, uiBus) {

  private val context = preferenceScreen.context

  private val moreApps by lazyPref<Preference>(R.string.more_apps_key)
  private val social by lazyPref<Preference>(R.string.social_media_f_key)
  private val followBlog by lazyPref<Preference>(R.string.social_media_b_key)
  private val rate by lazyPref<Preference>(R.string.rating_key)
  private val bugReport by lazyPref<Preference>(R.string.bugreport_key)
  private val licenses by lazyPref<Preference>(R.string.about_license_key)
  private val version by lazyPref<Preference>(R.string.check_version_key)
  private val clearAll by lazyPref<Preference>(R.string.clear_all_key)
  private val upgradeInfo by lazyPref<Preference>(R.string.upgrade_info_key)
  private val theme by lazyPref<Preference>(R.string.dark_mode_key)
  private val applicationSettings by lazyPref<Preference>("application_settings")

  override fun id(): Int {
    throw InvalidUiComponentIdException
  }

  override fun inflate(savedInstanceState: Bundle?) {
    adjustIconTint()

    setupApplicationTitle()
    setupMoreApps()
    setupBlog()
    setupBugReport()
    setupCheckUpgrade()
    setupClearAppData()
    setupDarkTheme()
    setupLicenses()
    setupRateApp()
    setupShowUpgradeInfo()
    setupSocial()
  }

  override fun saveState(outState: Bundle) {
  }

  override fun teardown() {
    moreApps.onPreferenceClickListener = null
    social.onPreferenceClickListener = null
    followBlog.onPreferenceClickListener = null
    rate.onPreferenceClickListener = null
    bugReport.onPreferenceClickListener = null
    licenses.onPreferenceClickListener = null
    version.onPreferenceClickListener = null
    clearAll.onPreferenceClickListener = null
    upgradeInfo.onPreferenceClickListener = null
    theme.onPreferenceClickListener = null
    applicationSettings.title = ""
  }

  private fun adjustIconTint() {
    val darkTheme = theming.isDarkTheme()
    parent.adjustTint(darkTheme)
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

  private fun setupMoreApps() {
    moreApps.setOnPreferenceClickListener {
      publish(MoreAppsClicked)
      return@setOnPreferenceClickListener true
    }
  }

  private fun setupSocial() {
    val socialLink = FACEBOOK.hyperlink(context)
    social.setOnPreferenceClickListener {
      publish(FollowSocialClicked(socialLink))
      return@setOnPreferenceClickListener true
    }
  }

  private fun setupBlog() {
    val blogLink = BLOG.hyperlink(context)
    followBlog.setOnPreferenceClickListener {
      publish(FollowBlogClicked(blogLink))
      return@setOnPreferenceClickListener true
    }
  }

  private fun setupRateApp() {
    rate.setOnPreferenceClickListener {
      publish(RateAppClicked)
      return@setOnPreferenceClickListener true
    }
  }

  private fun setupBugReport() {
    val reportLink = bugreportUrl.hyperlink(context)
    bugReport.setOnPreferenceClickListener {
      publish(BugReportClicked(reportLink))
      return@setOnPreferenceClickListener true
    }
  }

  private fun setupLicenses() {
    licenses.setOnPreferenceClickListener {
      publish(LicenseClicked)
      return@setOnPreferenceClickListener true
    }
  }

  private fun setupCheckUpgrade() {
    version.setOnPreferenceClickListener {
      publish(CheckUpgrade)
      return@setOnPreferenceClickListener true
    }
  }

  private fun setupClearAppData() {
    if (hideClearAll) {
      clearAll.isVisible = false
    } else {
      clearAll.setOnPreferenceClickListener {
        publish(ClearAppData)
        return@setOnPreferenceClickListener true
      }
    }
  }

  private fun setupShowUpgradeInfo() {
    if (hideUpgradeInformation) {
      upgradeInfo.isVisible = false
    } else {
      upgradeInfo.setOnPreferenceClickListener {
        publish(ShowUpgradeInfo)
        return@setOnPreferenceClickListener true
      }
    }
  }

  private fun setupDarkTheme() {
    theme.setOnPreferenceChangeListener { _, newValue ->
      if (newValue is Boolean) {
        publish(DarkTheme(newValue))
        return@setOnPreferenceChangeListener true
      }
      return@setOnPreferenceChangeListener false
    }
  }

  private fun setupApplicationTitle() {
    applicationSettings.title = "$applicationName Settings"
  }

  fun showError(error: ActivityNotFoundException) {
    Snackbreak.bindTo(owner)
        .short(view, error.message ?: "No activity can handle this URL")
        .show()

  }

  companion object {

    private const val FACEBOOK = "https://www.facebook.com/pyamsoftware"
    private const val BLOG = "https://pyamsoft.blogspot.com/"
  }

}