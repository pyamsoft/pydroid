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
import android.support.annotation.CallSuper
import android.support.annotation.CheckResult
import android.support.annotation.IdRes
import android.support.annotation.XmlRes
import android.support.design.widget.Snackbar
import android.support.v7.preference.Preference
import android.view.View
import com.pyamsoft.pydroid.base.rating.RatingPresenter
import com.pyamsoft.pydroid.base.version.VersionCheckPresenter
import com.pyamsoft.pydroid.base.version.VersionCheckProvider
import com.pyamsoft.pydroid.ui.PYDroid
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.about.AboutLibrariesFragment
import com.pyamsoft.pydroid.ui.rating.ChangeLogProvider
import com.pyamsoft.pydroid.ui.rating.RatingDialog
import com.pyamsoft.pydroid.ui.social.Linker
import com.pyamsoft.pydroid.ui.util.Snackbreak
import com.pyamsoft.pydroid.ui.util.Snackbreak.ErrorDetail
import com.pyamsoft.pydroid.ui.util.clickAppPage
import com.pyamsoft.pydroid.ui.util.show
import com.pyamsoft.pydroid.ui.version.VersionCheckActivity
import com.pyamsoft.pydroid.ui.version.VersionUpgradeDialog
import timber.log.Timber

abstract class SettingsPreferenceFragment : ToolbarPreferenceFragment(), VersionCheckPresenter.View,
    RatingPresenter.View, SettingsPreferencePresenter.View {

  internal lateinit var linker: Linker
  internal lateinit var presenter: SettingsPreferencePresenter
  internal lateinit var versionPresenter: VersionCheckPresenter
  internal lateinit var ratingPresenter: RatingPresenter
  private lateinit var snackbar: Snackbar

  @CallSuper
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    PYDroid.obtain()
        .plusAppComponent(versionedActivity.currentApplicationVersion)
        .inject(this)
  }

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

  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    super.onViewCreated(view, savedInstanceState)
    snackbar = Snackbar.make(view, "Checking for updates...", Snackbar.LENGTH_SHORT)

    val applicationSettings = findPreference("application_settings")
    if (applicationSettings != null) {
      applicationSettings.title = "$applicationName Settings"
    }

    val upgradeInfo: Preference? = findPreference(getString(R.string.upgrade_info_key))
    if (upgradeInfo != null) {
      if (hideUpgradeInformation) {
        upgradeInfo.isVisible = false
      } else {
        upgradeInfo.setOnPreferenceClickListener {
          onShowChangelogClicked()
          return@setOnPreferenceClickListener true
        }
      }
    }

    val clearAll: Preference? = findPreference(getString(R.string.clear_all_key))
    if (clearAll != null) {
      if (hideClearAll) {
        clearAll.isVisible = false
      } else {
        clearAll.setOnPreferenceClickListener {
          onClearAllClicked()
          return@setOnPreferenceClickListener true
        }
      }
    }

    val checkVersion: Preference = findPreference(getString(R.string.check_version_key))
    checkVersion.setOnPreferenceClickListener {
      onCheckForUpdatesClicked(versionPresenter)
      return@setOnPreferenceClickListener true
    }

    val showAboutLicenses: Preference = findPreference(getString(R.string.about_license_key))
    showAboutLicenses.setOnPreferenceClickListener {
      onLicenseItemClicked()
      return@setOnPreferenceClickListener true
    }

    val rateApplication: Preference = findPreference(getString(R.string.rating_key))
    rateApplication.setOnPreferenceClickListener {
      linker.clickAppPage(requireActivity(), view)
      return@setOnPreferenceClickListener true
    }

    versionPresenter.bind(viewLifecycle, this)
    ratingPresenter.bind(viewLifecycle, this)
    presenter.bind(viewLifecycle, this)
  }

  override fun onShowRating() {
    val activity = activity
    if (activity is ChangeLogProvider) {
      RatingDialog.newInstance(activity)
          .show(activity, RatingDialog.TAG)
    } else {
      throw ClassCastException("Activity is not a change log provider")
    }
  }

  override fun onShowRatingError(throwable: Throwable) {
    Timber.e(throwable, "Error loading rating dialog")
  }

  override fun onUpdatedVersionFound(
    current: Int,
    updated: Int
  ) {
    Timber.d("Updated version found. %d => %d", current, updated)
    VersionUpgradeDialog.newInstance(versionedActivity.applicationName, current, updated)
        .show(requireActivity(), VersionUpgradeDialog.TAG)
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
    ratingPresenter.loadRatingDialog(true)
  }

  /**
   * Checks the server for updates, override to use a custom behavior
   */
  protected open fun onCheckForUpdatesClicked(presenter: VersionCheckPresenter) {
    if (!snackbar.isShownOrQueued) {
      snackbar.show()
    }
    presenter.checkForUpdates(true)
  }

  private fun onError(throwable: Throwable) {
    view?.also {
      val details = ErrorDetail(message = throwable.localizedMessage)
      Snackbreak.short(requireActivity(), it, details)
    }
  }

  override fun onLinkerError(throwable: Throwable) {
    onError(throwable)
  }

  override fun onRatingError(throwable: Throwable) {
    onError(throwable)
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
}
