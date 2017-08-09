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
import android.support.v7.preference.Preference
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
import com.pyamsoft.pydroid.version.VersionCheckPresenter.Callback
import com.pyamsoft.pydroid.version.VersionCheckProvider
import timber.log.Timber

abstract class ActionBarSettingsPreferenceFragment : ActionBarPreferenceFragment() {

  internal lateinit var presenter: VersionCheckPresenter
  internal lateinit var preferencePresenter: ActionBarSettingsPreferencePresenter
  private lateinit var toast: Toast
  private var upgradeInfo: Preference? = null
  private var clearAll: Preference? = null
  private lateinit var showAboutLicenses: Preference
  private lateinit var checkVersion: Preference
  private lateinit var rateApplication: Preference
  private val callback: (Int, Int) -> Unit = { current, updated ->
    Timber.d("Updated version found. %d => %d", current, updated)
    DialogUtil.guaranteeSingleDialogFragment(activity,
        VersionUpgradeDialog.newInstance(versionedActivity.provideApplicationName(), current,
            updated), VersionUpgradeDialog.TAG)
  }

  @CallSuper override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    PYDroid.with {
      it.plusAppComponent(context.packageName, versionedActivity.currentApplicationVersion).inject(
          this)
    }
  }

  @SuppressLint("ShowToast") @CallSuper override fun onCreateView(inflater: LayoutInflater,
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

    val applicationSettings = findPreference("application_settings")
    if (applicationSettings != null) {
      applicationSettings.title = "$applicationName Settings"
    }

    upgradeInfo = findPreference(getString(R.string.upgrade_info_key))
    clearAll = findPreference(getString(R.string.clear_all_key))
    showAboutLicenses = findPreference(getString(R.string.about_license_key))
    checkVersion = findPreference(getString(R.string.check_version_key))
    rateApplication = findPreference(getString(R.string.rating_key))
  }

  @CallSuper override fun onStart() {
    super.onStart()
    preferencePresenter.start(Unit)
    presenter.start(object : Callback {
      override fun onUpdatedVersionFound(current: Int, updated: Int) {
        callback(current, updated)
      }
    })

    val upgrade = upgradeInfo
    if (upgrade != null) {
      if (hideUpgradeInformation) {
        upgrade.isVisible = false
      } else {
        preferencePresenter.clickEvent(upgrade, {
          onShowChangelogClicked()
        })
      }
    }

    preferencePresenter.clickEvent(showAboutLicenses, {
      onLicenseItemClicked()
    })

    preferencePresenter.clickEvent(checkVersion, {
      onCheckForUpdatesClicked(presenter)
    })


    val clear = clearAll
    if (clear != null) {
      if (hideClearAll) {
        clear.isVisible = false
      } else {
        preferencePresenter.clickEvent(clear, {
          onClearAllClicked()
        })
      }
    }

    preferencePresenter.clickEvent(rateApplication, {
      Linker.clickAppPage(it.context.packageName)
    })
  }

  @CallSuper override fun onStop() {
    super.onStop()
    presenter.stop()
    preferencePresenter.stop()
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
      RatingDialog.showRatingDialog(activity, activity, true)
    } else {
      throw ClassCastException("Activity is not a change log provider")
    }
  }

  /**
   * Checks the server for updates, override to use a custom behavior
   */
  protected open fun onCheckForUpdatesClicked(presenter: VersionCheckPresenter) {
    toast.show()
    presenter.checkForUpdates(callback)
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
