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
import androidx.core.content.ContextCompat
import androidx.preference.Preference
import androidx.preference.PreferenceGroup
import androidx.preference.PreferenceScreen
import com.pyamsoft.pydroid.core.bus.Publisher
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.arch.InvalidUiComponentIdException
import com.pyamsoft.pydroid.ui.arch.UiView
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
import com.pyamsoft.pydroid.util.hyperlink
import com.pyamsoft.pydroid.util.tintWith

internal class AppSettingsView internal constructor(
  private val theming: Theming,
  private val preferenceScreen: PreferenceScreen,
  private val applicationName: String,
  private val bugreportUrl: String,
  private val hideClearAll: Boolean,
  private val hideUpgradeInformation: Boolean,
  uiBus: Publisher<AppSettingsViewEvent>
) : UiView<AppSettingsViewEvent>(uiBus) {

  private val context = preferenceScreen.context

  private lateinit var moreApps: Preference
  private lateinit var social: Preference
  private lateinit var followBlog: Preference
  private lateinit var rate: Preference
  private lateinit var bugReport: Preference
  private lateinit var licenses: Preference
  private lateinit var version: Preference
  private lateinit var clearAll: Preference
  private lateinit var upgradeInfo: Preference
  private lateinit var theme: Preference
  private lateinit var applicationSettings: Preference

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

  private fun setupMoreApps() {
    moreApps = preferenceScreen.findPreference(context.getString(R.string.more_apps_key))
    moreApps.setOnPreferenceClickListener {
      publish(MoreAppsClicked)
      return@setOnPreferenceClickListener true
    }
  }

  private fun setupSocial() {
    social = preferenceScreen.findPreference(context.getString(R.string.social_media_f_key))
    val socialLink = FACEBOOK.hyperlink(context)
    social.setOnPreferenceClickListener {
      publish(FollowSocialClicked(socialLink))
      return@setOnPreferenceClickListener true
    }
  }

  private fun setupBlog() {
    followBlog = preferenceScreen.findPreference(context.getString(R.string.social_media_b_key))
    val blogLink = BLOG.hyperlink(context)
    followBlog.setOnPreferenceClickListener {
      publish(FollowBlogClicked(blogLink))
      return@setOnPreferenceClickListener true
    }
  }

  private fun setupRateApp() {
    rate = preferenceScreen.findPreference(context.getString(R.string.rating_key))
    rate.setOnPreferenceClickListener {
      publish(RateAppClicked)
      return@setOnPreferenceClickListener true
    }
  }

  private fun setupBugReport() {
    bugReport = preferenceScreen.findPreference(context.getString(R.string.bugreport_key))
    val reportLink = bugreportUrl.hyperlink(context)
    bugReport.setOnPreferenceClickListener {
      publish(BugReportClicked(reportLink))
      return@setOnPreferenceClickListener true
    }
  }

  private fun setupLicenses() {
    licenses = preferenceScreen.findPreference(context.getString(R.string.about_license_key))
    licenses.setOnPreferenceClickListener {
      publish(LicenseClicked)
      return@setOnPreferenceClickListener true
    }
  }

  private fun setupCheckUpgrade() {
    version = preferenceScreen.findPreference(context.getString(R.string.check_version_key))
    version.setOnPreferenceClickListener {
      publish(CheckUpgrade)
      return@setOnPreferenceClickListener true
    }
  }

  private fun setupClearAppData() {
    clearAll = preferenceScreen.findPreference(context.getString(R.string.clear_all_key))
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
    upgradeInfo = preferenceScreen.findPreference(context.getString(R.string.upgrade_info_key))
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
    theme = preferenceScreen.findPreference(context.getString(R.string.dark_mode_key))
    theme.setOnPreferenceChangeListener { _, newValue ->
      if (newValue is Boolean) {
        publish(DarkTheme(newValue))
        return@setOnPreferenceChangeListener true
      }
      return@setOnPreferenceChangeListener false
    }
  }

  private fun setupApplicationTitle() {
    applicationSettings = preferenceScreen.findPreference("application_settings")
    applicationSettings.title = "$applicationName Settings"
  }

  companion object {

    private const val FACEBOOK = "https://www.facebook.com/pyamsoftware"
    private const val BLOG = "https://pyamsoft.blogspot.com/"
  }

}