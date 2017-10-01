/*
 *     Copyright (C) 2017 Peter Kenji Yamanaka
 *
 *     This program is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation; either version 2 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License along
 *     with this program; if not, write to the Free Software Foundation, Inc.,
 *     51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.pyamsoft.pydroid.ui.app.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.annotation.CheckResult
import android.support.annotation.IdRes
import android.support.annotation.XmlRes
import android.support.v7.preference.Preference
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.pyamsoft.pydroid.presenter.Presenter
import com.pyamsoft.pydroid.ui.PYDroid
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.about.AboutLibrariesFragment
import com.pyamsoft.pydroid.ui.helper.Toasty
import com.pyamsoft.pydroid.ui.rating.RatingDialog
import com.pyamsoft.pydroid.ui.social.Linker
import com.pyamsoft.pydroid.ui.util.DialogUtil
import com.pyamsoft.pydroid.ui.version.VersionCheckActivity
import com.pyamsoft.pydroid.ui.version.VersionUpgradeDialog
import com.pyamsoft.pydroid.version.VersionCheckPresenter
import com.pyamsoft.pydroid.version.VersionCheckProvider
import timber.log.Timber

abstract class ActionBarSettingsPreferenceFragment : DisposablePreferenceFragment() {

  internal lateinit var presenter: VersionCheckPresenter
  private lateinit var toast: Toast
  private val onUpdatedVersionFound: (Int, Int) -> Unit = { current, updated ->
    Timber.d("Updated version found. %d => %d", current, updated)
    DialogUtil.guaranteeSingleDialogFragment(activity,
        VersionUpgradeDialog.newInstance(versionedActivity.applicationName, current,
            updated), VersionUpgradeDialog.TAG)
  }

  @CallSuper override fun provideBoundPresenters(): List<Presenter<*>> = listOf(presenter)

  @CallSuper override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    PYDroid.with {
      it.plusAppComponent(context.packageName,
          versionedActivity.currentApplicationVersion).inject(
          this)
    }
  }

  @SuppressLint("ShowToast")
  @CallSuper override fun onCreateView(inflater: LayoutInflater,
      container: ViewGroup?, savedInstanceState: Bundle?): View? {
    toast = Toasty.makeText(context, "Checking for updates...", Toasty.LENGTH_SHORT)
    return super.onCreateView(inflater, container, savedInstanceState)
  }

  @CallSuper override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
    @XmlRes val xmlResId = preferenceXmlResId
    if (xmlResId != 0) {
      addPreferencesFromResource(xmlResId)
    }
    addPreferencesFromResource(R.xml.pydroid)
  }

  override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

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
      onCheckForUpdatesClicked(presenter)
      return@setOnPreferenceClickListener true
    }

    val showAboutLicenses: Preference = findPreference(getString(R.string.about_license_key))
    showAboutLicenses.setOnPreferenceClickListener {
      onLicenseItemClicked()
      return@setOnPreferenceClickListener true
    }

    val rateApplication: Preference = findPreference(getString(R.string.rating_key))
    rateApplication.setOnPreferenceClickListener {
      Linker.clickAppPage(it.context, it.context.packageName)
      return@setOnPreferenceClickListener true
    }

    presenter.bind(Unit)
  }

  override fun onStart() {
    super.onStart()
    presenter.checkForUpdates(false, onUpdatedVersionFound)
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

  /**
   * Shows the changelog, override or extend to use unique implementation
   */
  protected open fun onShowChangelogClicked() {
    val activity = activity
    if (activity is RatingDialog.ChangeLogProvider) {
      DialogUtil.guaranteeSingleDialogFragment(activity, RatingDialog.newInstance(activity),
          "rating")
    } else {
      throw ClassCastException("Activity is not a change log provider")
    }
  }

  /**
   * Checks the server for updates, override to use a custom behavior
   */
  protected open fun onCheckForUpdatesClicked(presenter: VersionCheckPresenter) {
    toast.show()
    presenter.checkForUpdates(true, onUpdatedVersionFound)
  }

  /**
   * Indicates the state of the back stack with this fragment on it
   */
  protected open val isLastOnBackStack: AboutLibrariesFragment.BackStackState = AboutLibrariesFragment.BackStackState.NOT_LAST

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

  @get:[CheckResult IdRes] protected abstract val rootViewContainer: Int

  @get:CheckResult protected abstract val applicationName: String
}
