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
  private val uiBus: Publisher<AppSettingsViewEvent>,
  private val preferenceScreen: PreferenceScreen,
  private val applicationName: String,
  private val bugreportUrl: String,
  private val hideClearAll: Boolean,
  private val hideUpgradeInformation: Boolean
) : UiView<AppSettingsViewEvent> {

  private val context = preferenceScreen.context

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
    val moreApps = preferenceScreen.findPreference(context.getString(R.string.more_apps_key))
    moreApps.setOnPreferenceClickListener {
      uiBus.publish(MoreAppsClicked)
      return@setOnPreferenceClickListener true
    }
  }

  private fun setupSocial() {
    val social = preferenceScreen.findPreference(context.getString(R.string.social_media_f_key))
    val socialLink = FACEBOOK.hyperlink(context)
    social.setOnPreferenceClickListener {
      uiBus.publish(FollowSocialClicked(socialLink))
      return@setOnPreferenceClickListener true
    }
  }

  private fun setupBlog() {
    val followBlog = preferenceScreen.findPreference(context.getString(R.string.social_media_b_key))
    val blogLink = BLOG.hyperlink(context)
    followBlog.setOnPreferenceClickListener {
      uiBus.publish(FollowBlogClicked(blogLink))
      return@setOnPreferenceClickListener true
    }
  }

  private fun setupRateApp() {
    val rate = preferenceScreen.findPreference(context.getString(R.string.rating_key))
    rate.setOnPreferenceClickListener {
      uiBus.publish(RateAppClicked)
      return@setOnPreferenceClickListener true
    }
  }

  private fun setupBugReport() {
    val bugreport = preferenceScreen.findPreference(context.getString(R.string.bugreport_key))
    val reportLink = bugreportUrl.hyperlink(context)
    bugreport.setOnPreferenceClickListener {
      uiBus.publish(BugReportClicked(reportLink))
      return@setOnPreferenceClickListener true
    }
  }

  private fun setupLicenses() {
    val licenses = preferenceScreen.findPreference(context.getString(R.string.about_license_key))
    licenses.setOnPreferenceClickListener {
      uiBus.publish(LicenseClicked)
      return@setOnPreferenceClickListener true
    }
  }

  private fun setupCheckUpgrade() {
    val version = preferenceScreen.findPreference(context.getString(R.string.check_version_key))
    version.setOnPreferenceClickListener {
      uiBus.publish(CheckUpgrade)
      return@setOnPreferenceClickListener true
    }
  }

  private fun setupClearAppData() {
    val clearAll = preferenceScreen.findPreference(context.getString(R.string.clear_all_key))
    if (hideClearAll) {
      clearAll.isVisible = false
    } else {
      clearAll.setOnPreferenceClickListener {
        uiBus.publish(ClearAppData)
        return@setOnPreferenceClickListener true
      }
    }
  }

  private fun setupShowUpgradeInfo() {
    val upgradeInfo = preferenceScreen.findPreference(context.getString(R.string.upgrade_info_key))
    if (hideUpgradeInformation) {
      upgradeInfo.isVisible = false
    } else {
      upgradeInfo.setOnPreferenceClickListener {
        uiBus.publish(ShowUpgradeInfo)
        return@setOnPreferenceClickListener true
      }
    }
  }

  private fun setupDarkTheme() {
    val theme = preferenceScreen.findPreference(context.getString(R.string.dark_mode_key))
    theme.setOnPreferenceChangeListener { _, newValue ->
      if (newValue is Boolean) {
        uiBus.publish(DarkTheme(newValue))
        return@setOnPreferenceChangeListener true
      }
      return@setOnPreferenceChangeListener false
    }
  }

  private fun setupApplicationTitle() {
    val applicationSettings = preferenceScreen.findPreference("application_settings")
    applicationSettings.title = "$applicationName Settings"
  }

  companion object {

    private const val FACEBOOK = "https://www.facebook.com/pyamsoftware"
    private const val BLOG = "https://pyamsoft.blogspot.com/"
  }

}