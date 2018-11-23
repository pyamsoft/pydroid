package com.pyamsoft.pydroid.ui.app.fragment

import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle.Event.ON_DESTROY
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import androidx.preference.Preference
import androidx.preference.PreferenceGroup
import androidx.preference.PreferenceScreen
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.theme.Theming
import com.pyamsoft.pydroid.util.HyperlinkIntent
import com.pyamsoft.pydroid.util.hyperlink
import com.pyamsoft.pydroid.util.tintWith

internal class SettingsPreferenceViewImpl internal constructor(
  private val owner: LifecycleOwner,
  private val preferenceManager: PreferenceScreen,
  private val theming: Theming,
  private val applicationName: String,
  private val bugreportUrl: String,
  private val hideClearAll: Boolean,
  private val hideUpgradeInformation: Boolean
) : SettingsPreferenceView, LifecycleObserver {

  private val context = preferenceManager.context

  init {
    owner.lifecycle.addObserver(this)

    adjustIconTint()
    setupApplicationTitle()
  }

  @Suppress("unused")
  @OnLifecycleEvent(ON_DESTROY)
  internal fun destroy() {
    owner.lifecycle.removeObserver(this)
  }

  private fun adjustIconTint() {
    val darkTheme = theming.isDarkTheme()
    preferenceManager.adjustTint(darkTheme)
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

  override fun onMoreAppsClicked(onClick: () -> Unit) {
    val moreApps = preferenceManager.findPreference(context.getString(R.string.more_apps_key))
    moreApps.setOnPreferenceClickListener {
      onClick()
      return@setOnPreferenceClickListener true
    }
  }

  override fun onFollowsClicked(
    onBlogClicked: (blogLink: HyperlinkIntent) -> Unit,
    onSocialClicked: (socialLink: HyperlinkIntent) -> Unit
  ) {
    val followBlog = preferenceManager.findPreference(context.getString(R.string.social_media_b_key))
    val blogLink = BLOG.hyperlink(context)
    followBlog.setOnPreferenceClickListener {
      onBlogClicked(blogLink)
      return@setOnPreferenceClickListener true
    }

    val followSocialMedia =
      preferenceManager.findPreference(context.getString(R.string.social_media_f_key))
    val socialLink = FACEBOOK.hyperlink(context)
    followSocialMedia.setOnPreferenceClickListener {
      onSocialClicked(socialLink)
      return@setOnPreferenceClickListener true
    }
  }

  override fun onRateAppClicked(onClick: () -> Unit) {
    val rateApplication = preferenceManager.findPreference(context.getString(R.string.rating_key))
    rateApplication.setOnPreferenceClickListener {
      onClick()
      return@setOnPreferenceClickListener true
    }
  }

  override fun onBugReportClicked(onClick: (report: HyperlinkIntent) -> Unit) {
    val reportUrl = bugreportUrl.hyperlink(context)
    val bugreport = preferenceManager.findPreference(context.getString(R.string.bugreport_key))
    bugreport.setOnPreferenceClickListener {
      onClick(reportUrl)
      return@setOnPreferenceClickListener true
    }
  }

  override fun onLicensesClicked(onClick: () -> Unit) {
    val showAboutLicenses =
      preferenceManager.findPreference(context.getString(R.string.about_license_key))
    showAboutLicenses.setOnPreferenceClickListener {
      onClick()
      return@setOnPreferenceClickListener true
    }
  }

  override fun onCheckVersionClicked(onClick: () -> Unit) {
    val checkVersion =
      preferenceManager.findPreference(context.getString(R.string.check_version_key))
    checkVersion.setOnPreferenceClickListener {
      onClick()
      return@setOnPreferenceClickListener true
    }
  }

  override fun onClearAllClicked(onClick: () -> Unit) {
    val clearAll = preferenceManager.findPreference(context.getString(R.string.clear_all_key))
    if (hideClearAll) {
      clearAll.isVisible = false
    } else {
      clearAll.setOnPreferenceClickListener {
        onClick()
        return@setOnPreferenceClickListener true
      }
    }
  }

  override fun onUpgradeClicked(onClick: () -> Unit) {
    val upgradeInfo = preferenceManager.findPreference(context.getString(R.string.upgrade_info_key))
    if (hideUpgradeInformation) {
      upgradeInfo.isVisible = false
    } else {
      upgradeInfo.setOnPreferenceClickListener {
        onClick()
        return@setOnPreferenceClickListener true
      }
    }
  }

  private fun setupApplicationTitle() {
    val applicationSettings = preferenceManager.findPreference("application_settings")
    applicationSettings.title = "$applicationName Settings"
  }

  companion object {

    private const val FACEBOOK = "https://www.facebook.com/pyamsoftware"
    private const val BLOG = "https://pyamsoft.blogspot.com/"
  }

}