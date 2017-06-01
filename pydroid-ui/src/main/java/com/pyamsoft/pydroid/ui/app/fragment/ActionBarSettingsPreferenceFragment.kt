/*
 * Copyright 2017 Peter Kenji Yamanaka
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
 */

package com.pyamsoft.pydroid.ui.app.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.annotation.CheckResult
import android.support.annotation.IdRes
import android.support.annotation.XmlRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.pyamsoft.pydroid.ui.PYDroid
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.about.AboutLibrariesFragment
import com.pyamsoft.pydroid.ui.rating.RatingDialog
import com.pyamsoft.pydroid.ui.social.Linker
import com.pyamsoft.pydroid.ui.util.DialogUtil
import com.pyamsoft.pydroid.ui.version.VersionCheckActivity
import com.pyamsoft.pydroid.ui.version.VersionUpgradeDialog
import com.pyamsoft.pydroid.version.VersionCheckPresenter
import com.pyamsoft.pydroid.version.VersionCheckProvider
import timber.log.Timber
import java.util.Locale

abstract class ActionBarSettingsPreferenceFragment : ActionBarPreferenceFragment() {

  internal lateinit var presenter: VersionCheckPresenter
  private lateinit var toast: Toast

  @CallSuper override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    PYDroid.getInstance().provideComponent().plusAppComponent().inject(this)
  }

  @SuppressLint("ShowToast") @CallSuper override fun onCreateView(inflater: LayoutInflater,
      container: ViewGroup?, savedInstanceState: Bundle?): View? {
    toast = Toast.makeText(context, "Checking for updates...", Toast.LENGTH_SHORT)
    return super.onCreateView(inflater, container, savedInstanceState)
  }

  @CallSuper override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
    @XmlRes val xmlResId = preferenceXmlResId
    if (xmlResId != 0) {
      addPreferencesFromResource(xmlResId)
    }
    addPreferencesFromResource(R.xml.pydroid)

    val applicationSettings = findPreference("application_settings")
    if (applicationSettings != null) {
      applicationSettings.title = String.format(Locale.getDefault(), "%s Settings", applicationName)
    }
  }

  @CallSuper override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    val upgradeInfo = findPreference(getString(R.string.upgrade_info_key))
    if (upgradeInfo != null) {
      if (hideUpgradeInformation) {
        upgradeInfo.isVisible = false
      } else {
        upgradeInfo.setOnPreferenceClickListener {
          onShowChangelogClicked()
          true
        }
      }
    }

    val showAboutLicenses = findPreference(getString(R.string.about_license_key))
    showAboutLicenses.setOnPreferenceClickListener {
      onLicenseItemClicked()
      true
    }

    val checkVersion = findPreference(getString(R.string.check_version_key))
    checkVersion.setOnPreferenceClickListener {
      onCheckForUpdatesClicked()
      true
    }

    val clearAll = findPreference(getString(R.string.clear_all_key))
    if (clearAll != null) {
      if (hideClearAll) {
        clearAll.isVisible = false
      } else {
        clearAll.setOnPreferenceClickListener {
          onClearAllClicked()
          true
        }
      }
    }

    val rateApplication = findPreference(getString(R.string.rating_key))
    rateApplication.setOnPreferenceClickListener {
      Linker.with(it.context).clickAppPage(it.context.packageName)
      true
    }
  }

  @CallSuper override fun onStop() {
    super.onStop()
    presenter.stop()
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
  @CallSuper protected open fun onLicenseItemClicked() {
    Timber.d("Show about licenses fragment")
    AboutLibrariesFragment.show(activity, rootViewContainer, isLastOnBackStack)
  }

  internal fun onShowChangelogClicked() {
    val activity = activity
    if (activity is RatingDialog.ChangeLogProvider) {
      onShowChangelogClicked(activity)
    } else {
      throw ClassCastException("Activity is not a change log provider")
    }
  }

  /**
   * Shows the changelog, override or extend to use unique implementation
   */
  protected fun onShowChangelogClicked(provider: RatingDialog.ChangeLogProvider) {
    RatingDialog.showRatingDialog(activity, provider, true)
  }

  internal fun onCheckForUpdatesClicked() {
    onCheckForUpdatesClicked(presenter)
  }

  /**
   * Checks the server for updates, override to use a custom behavior
   */
  protected fun onCheckForUpdatesClicked(presenter: VersionCheckPresenter) {
    toast.show()
    presenter.forceCheckForUpdates(context.packageName, currentApplicationVersion,
        onUpdatedVersionFound = { current, updated ->
          Timber.d("Updated version found. %d => %d", current, updated)
          DialogUtil.guaranteeSingleDialogFragment(activity,
              VersionUpgradeDialog.newInstance(provideApplicationName(), current, updated),
              VersionUpgradeDialog.TAG)
        }, onVersionCheckFinished = {
      Timber.d("License check finished.")
    })
  }

  protected open val isLastOnBackStack: AboutLibrariesFragment.BackStackState
    @CheckResult get() = AboutLibrariesFragment.BackStackState.NOT_LAST

  @CheckResult internal fun provideApplicationName(): String {
    return versionedActivity.provideApplicationName()
  }

  internal val currentApplicationVersion: Int
    @CheckResult get() = versionedActivity.curentApplicationVersion

  private val versionedActivity: VersionCheckProvider
    @CheckResult get() {
      val activity = activity
      if (activity is VersionCheckActivity) {
        return activity
      } else {
        throw IllegalStateException("Activity is not VersionCheckActivity")
      }
    }

  protected open val preferenceXmlResId: Int
    @CheckResult @XmlRes get() = 0

  protected open val hideUpgradeInformation: Boolean
    @CheckResult get() = false

  protected open val hideClearAll: Boolean
    @CheckResult get() = false

  @get:CheckResult @get:IdRes protected abstract val rootViewContainer: Int

  @get:CheckResult protected abstract val applicationName: String
}
