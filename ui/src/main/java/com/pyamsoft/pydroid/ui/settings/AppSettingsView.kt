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

import androidx.core.content.ContextCompat
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceGroup
import androidx.preference.PreferenceScreen
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.arch.PrefUiView
import com.pyamsoft.pydroid.ui.privacy.PrivacyEventBus
import com.pyamsoft.pydroid.ui.privacy.PrivacyEvents.ViewPrivacyPolicy
import com.pyamsoft.pydroid.ui.privacy.PrivacyEvents.ViewTermsAndConditions
import com.pyamsoft.pydroid.ui.settings.AppSettingsViewEvent.CheckUpgrade
import com.pyamsoft.pydroid.ui.settings.AppSettingsViewEvent.ClearData
import com.pyamsoft.pydroid.ui.settings.AppSettingsViewEvent.Hyperlink
import com.pyamsoft.pydroid.ui.settings.AppSettingsViewEvent.MoreApps
import com.pyamsoft.pydroid.ui.settings.AppSettingsViewEvent.RateApp
import com.pyamsoft.pydroid.ui.settings.AppSettingsViewEvent.ShowUpgrade
import com.pyamsoft.pydroid.ui.settings.AppSettingsViewEvent.ToggleDarkTheme
import com.pyamsoft.pydroid.ui.settings.AppSettingsViewEvent.ViewLicense
import com.pyamsoft.pydroid.util.hyperlink
import com.pyamsoft.pydroid.util.tintWith
import kotlinx.coroutines.launch

internal class AppSettingsView internal constructor(
    private val applicationName: CharSequence,
    private val bugReportUrl: String,
    private val viewSourceUrl: String,
    private val privacyPolicyUrl: String,
    private val termsConditionsUrl: String,
    private val hideClearAll: Boolean,
    private val hideUpgradeInformation: Boolean,
    preferenceScreen: PreferenceScreen
) : PrefUiView<AppSettingsViewState, AppSettingsViewEvent>(preferenceScreen) {

    private var preferenceScreen: PreferenceScreen? = preferenceScreen

    private val moreApps by boundPref<Preference>(R.string.more_apps_key)
    private val social by boundPref<Preference>(R.string.social_media_f_key)
    private val followBlog by boundPref<Preference>(R.string.social_media_b_key)
    private val rate by boundPref<Preference>(R.string.rating_key)
    private val bugReport by boundPref<Preference>(R.string.bugreport_key)
    private val viewSource by boundPref<Preference>(R.string.view_source_key)
    private val licenses by boundPref<Preference>(R.string.about_license_key)
    private val version by boundPref<Preference>(R.string.check_version_key)
    private val clearAll by boundPref<Preference>(R.string.clear_all_key)
    private val upgradeInfo by boundPref<Preference>(R.string.upgrade_info_key)
    private val theme by boundPref<ListPreference>(R.string.dark_mode_key)
    private val privacyPolicy by boundPref<Preference>(R.string.view_privacy_key)
    private val termsConditions by boundPref<Preference>(R.string.view_terms_key)
    private val applicationGroup by boundPref<Preference>("application_settings")

    init {
        val self = this
        doOnInflate {
            setupApplicationTitle()
            setupBugReport()
            setupViewSource()
            setupClearAppData()
            setupShowUpgradeInfo()
            setupMoreApps()
            setupBlog()
            setupCheckUpgrade()
            setupDarkTheme()
            setupLicenses()
            setupRateApp()
            setupSocial()
            setupPrivacyPolicy()
            setupTermsConditions()
        }

        doOnTeardown {
            moreApps.onPreferenceClickListener = null
            social.onPreferenceClickListener = null
            followBlog.onPreferenceClickListener = null
            rate.onPreferenceClickListener = null
            bugReport.onPreferenceClickListener = null
            viewSource.onPreferenceClickListener = null
            licenses.onPreferenceClickListener = null
            version.onPreferenceClickListener = null
            clearAll.onPreferenceClickListener = null
            upgradeInfo.onPreferenceClickListener = null
            theme.onPreferenceClickListener = null
            privacyPolicy.onPreferenceClickListener = null
            termsConditions.onPreferenceClickListener = null

            self.preferenceScreen = null
        }
    }

    override fun onRender(state: AppSettingsViewState) {
        state.isDarkTheme?.let { darkTheme ->
            requireNotNull(preferenceScreen).adjustTint(darkTheme.dark)
        }
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

    private fun setupPrivacyPolicy() {
        privacyPolicy.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            viewScope.launch {
                PrivacyEventBus.publish(ViewPrivacyPolicy(privacyPolicyUrl))
            }
            return@OnPreferenceClickListener true
        }
    }

    private fun setupTermsConditions() {
        termsConditions.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            viewScope.launch {
                PrivacyEventBus.publish(ViewTermsAndConditions(termsConditionsUrl))
            }
            return@OnPreferenceClickListener true
        }
    }

    private fun setupViewSource() {
        val sourceLink = viewSourceUrl.hyperlink(viewSource.context)
        viewSource.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            publish(Hyperlink(sourceLink))
            return@OnPreferenceClickListener true
        }
    }

    private fun setupMoreApps() {
        moreApps.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            publish(MoreApps)
            return@OnPreferenceClickListener true
        }
    }

    private fun setupSocial() {
        val socialLink = FACEBOOK.hyperlink(social.context)
        social.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            publish(Hyperlink(socialLink))
            return@OnPreferenceClickListener true
        }
    }

    private fun setupBlog() {
        val blogLink = BLOG.hyperlink(followBlog.context)
        followBlog.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            publish(Hyperlink(blogLink))
            return@OnPreferenceClickListener true
        }
    }

    private fun setupRateApp() {
        rate.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            publish(RateApp)
            return@OnPreferenceClickListener true
        }
    }

    private fun setupBugReport() {
        val reportLink = bugReportUrl.hyperlink(bugReport.context)
        bugReport.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            publish(Hyperlink(reportLink))
            return@OnPreferenceClickListener true
        }
    }

    private fun setupLicenses() {
        licenses.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            publish(ViewLicense)
            return@OnPreferenceClickListener true
        }
    }

    private fun setupCheckUpgrade() {
        version.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            publish(CheckUpgrade)
            return@OnPreferenceClickListener true
        }
    }

    private fun setupClearAppData() {
        if (hideClearAll) {
            clearAll.isVisible = false
        } else {
            clearAll.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                publish(ClearData)
                return@OnPreferenceClickListener true
            }
        }
    }

    private fun setupShowUpgradeInfo() {
        if (hideUpgradeInformation) {
            upgradeInfo.isVisible = false
        } else {
            upgradeInfo.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                publish(ShowUpgrade)
                return@OnPreferenceClickListener true
            }
        }
    }

    private fun setupDarkTheme() {
        theme.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, newValue ->
            if (newValue is String) {
                publish(ToggleDarkTheme(newValue))
                return@OnPreferenceChangeListener true
            }
            return@OnPreferenceChangeListener false
        }
    }

    private fun setupApplicationTitle() {
        applicationGroup.title = "$applicationName Settings"
    }

    companion object {

        private const val FACEBOOK = "https://www.facebook.com/pyamsoftware"
        private const val BLOG = "https://pyamsoft.blogspot.com/"
    }
}
