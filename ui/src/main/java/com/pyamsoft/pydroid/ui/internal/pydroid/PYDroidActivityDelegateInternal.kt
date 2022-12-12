/*
 * Copyright 2022 Peter Kenji Yamanaka
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

package com.pyamsoft.pydroid.ui.internal.pydroid

import androidx.annotation.CheckResult
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.pyamsoft.pydroid.core.Logger
import com.pyamsoft.pydroid.core.requireNotNull
import com.pyamsoft.pydroid.ui.app.PYDroidActivityDelegate
import com.pyamsoft.pydroid.ui.app.PYDroidActivityOptions
import com.pyamsoft.pydroid.ui.changelog.ChangeLogProvider
import com.pyamsoft.pydroid.ui.changelog.ShowUpdateChangeLog
import com.pyamsoft.pydroid.ui.internal.app.AppComponent
import com.pyamsoft.pydroid.ui.internal.app.AppInternalViewModeler
import com.pyamsoft.pydroid.ui.internal.billing.BillingDelegate
import com.pyamsoft.pydroid.ui.internal.datapolicy.DataPolicyDelegate
import com.pyamsoft.pydroid.ui.internal.rating.RatingDelegate
import com.pyamsoft.pydroid.ui.internal.version.VersionCheckDelegate
import com.pyamsoft.pydroid.ui.version.VersionUpdateProgress
import com.pyamsoft.pydroid.ui.version.VersionUpgradeAvailable
import com.pyamsoft.pydroid.util.doOnCreate
import com.pyamsoft.pydroid.util.doOnDestroy
import com.pyamsoft.pydroid.util.doOnResume
import com.pyamsoft.pydroid.util.doOnStart

internal class PYDroidActivityDelegateInternal
internal constructor(
    component: AppComponent,
    provider: ChangeLogProvider,
    activity: FragmentActivity,
) : PYDroidActivityDelegate {

  private var appComponent: AppComponent? = component
  private var appProvider: ChangeLogProvider? = provider
  private var appOptions: PYDroidActivityOptions? = component.options()

  // Copy these out of PYDroidActivityComponents so we can null them out onDestroy
  private var ratingDelegate: RatingDelegate?
  private var versionCheckDelegate: VersionCheckDelegate?
  private var versionUpgradeAvailable: VersionUpgradeAvailable?
  private var versionUpdateProgress: VersionUpdateProgress?
  private var showUpdateChangeLog: ShowUpdateChangeLog?

  init {
    val components = component.create(activity)
    val options = component.options()

    val rd = components.rating
    val vc = components.versionCheck
    val dp = components.dataPolicy

    ratingDelegate = rd
    versionCheckDelegate = vc
    versionUpgradeAvailable = components.versionUpgrader
    versionUpdateProgress = components.versionUpdateProgress
    showUpdateChangeLog = components.showUpdateChangeLog

    activity.doOnCreate {
      connectBilling(
          activity = activity,
          billingDelegate = components.billing,
          options = options,
      )

      connectDataPolicy(
          activity = activity,
          dataPolicy = dp,
          options = options,
      )

      connectRating(
          activity = activity,
          ratingDelegate = rd,
          options = options,
      )

      connectVersionCheck(
          activity = activity,
          versionCheckDelegate = vc,
          options = options,
      )
    }

    activity.doOnStart {
      showDataPolicyDisclosure(
          activity = activity,
          presenter = components.internalPresenter,
          dataPolicy = dp,
          options = options,
      )

      checkUpdates(
          versionCheckDelegate = vc,
          options = options,
      )
    }

    activity.doOnDestroy {
      appComponent = null
      appOptions = null
      appProvider = null

      ratingDelegate = null
      versionCheckDelegate = null
      versionUpgradeAvailable = null
      versionUpdateProgress = null
    }
  }

  private fun showDataPolicyDisclosure(
      activity: FragmentActivity,
      presenter: AppInternalViewModeler,
      dataPolicy: DataPolicyDelegate,
      options: PYDroidActivityOptions,
  ) {
    if (options.disableDataPolicy) {
      Logger.w("Application has disabled the Data Policy component")
      return
    }

    activity.doOnResume {
      // DialogFragments cannot be shown safely until at least onPostResume
      presenter.handleShowCorrectDialog(
          scope = activity.lifecycleScope,
          onShowDataPolicy = {
            // Attempt to show the data policy if we are not disabled
            dataPolicy.showDataPolicyDisclosure()
          },
      )
    }
  }

  /** Attempts to connect to in-app billing */
  private fun connectBilling(
      activity: FragmentActivity,
      billingDelegate: BillingDelegate,
      options: PYDroidActivityOptions,
  ) {
    if (options.disableBilling) {
      Logger.w("Application has disabled the billing component")
      return
    }

    activity.doOnCreate {
      Logger.d("Attempt Connect Billing")
      billingDelegate.connect()
    }
  }

  /** Attempts to connect to in-app rating */
  private fun connectRating(
      activity: FragmentActivity,
      ratingDelegate: RatingDelegate,
      options: PYDroidActivityOptions,
  ) {
    if (options.disableRating) {
      Logger.w("Application has disabled the Rating component")
      return
    }

    activity.doOnCreate {
      Logger.d("Attempt Connect Rating")
      ratingDelegate.bindEvents()
    }
  }

  /** Attempts to connect to in-app data policy dialog */
  private fun connectDataPolicy(
      activity: FragmentActivity,
      dataPolicy: DataPolicyDelegate,
      options: PYDroidActivityOptions,
  ) {
    if (options.disableDataPolicy) {
      Logger.w("Application has disabled the Data Policy component")
      return
    }

    activity.doOnCreate {
      Logger.d("Attempt Connect Data Policy")
      dataPolicy.bindEvents()
    }
  }

  /** Attempts to connect to in-app updates */
  private fun connectVersionCheck(
      activity: FragmentActivity,
      versionCheckDelegate: VersionCheckDelegate,
      options: PYDroidActivityOptions,
  ) {
    if (options.disableVersionCheck) {
      Logger.w("Application has disabled the VersionCheck component")
      return
    }

    activity.doOnCreate {
      Logger.d("Attempt Connect Version Check")
      versionCheckDelegate.bindEvents()
    }
  }

  private fun checkUpdates(
      versionCheckDelegate: VersionCheckDelegate,
      options: PYDroidActivityOptions,
  ) {
    if (options.disableVersionCheck) {
      Logger.w("Application has disabled the VersionCheck component")
      return
    }

    versionCheckDelegate.checkUpdates()
  }

  /** Expose the ChangeLogProvider */
  @CheckResult
  internal fun changeLogProvider(): ChangeLogProvider {
    return appProvider.requireNotNull { "ChangeLogProvider is NULL, was this destroyed?" }
  }

  /** Expose the Component injector */
  @CheckResult
  internal fun injector(): AppComponent {
    return appComponent.requireNotNull { "AppComponent is NULL, was this destroyed?" }
  }

  /** Used in NewVersionWidget */
  @CheckResult
  internal fun versionUpgrader(): VersionUpgradeAvailable {
    return versionUpgradeAvailable.requireNotNull {
      "VersionUpgradeAvailable is NULL, was this destroyed?"
    }
  }

  /** Used in UpdateProgressWidget */
  @CheckResult
  internal fun updateProgress(): VersionUpdateProgress {
    return versionUpdateProgress.requireNotNull {
      "VersionUpdateProgress is NULL, was this destroyed?"
    }
  }

  /** Used in ChangeLogWidget */
  @CheckResult
  internal fun changeLog(): ShowUpdateChangeLog {
    return showUpdateChangeLog.requireNotNull { "ShowUpdateChangeLog is NULL, was this destroyed?" }
  }

  /**
   * Rating Attempt to call in-app rating dialog. Does not always result in showing the Dialog, that
   * is up to Google
   */
  override fun loadInAppRating() {
    val rating = ratingDelegate.requireNotNull { "RatingDelegate is NULL, was this destroyed?" }
    val options = appOptions.requireNotNull { "AppOptions is NULL, was this destroyed?" }

    if (options.disableRating) {
      Logger.w("Application has disabled the Rating component")
      return
    }

    rating.loadInAppRating()
  }

  /** Confirm the potential version upgrade */
  override fun confirmUpgrade() {
    val versionCheck =
        versionCheckDelegate.requireNotNull { "VersionCheckDelegate is NULL, was this destroyed?" }
    val options = appOptions.requireNotNull { "AppOptions is NULL, was this destroyed?" }

    if (options.disableVersionCheck) {
      Logger.w("Application has disabled the VersionCheck component")
      return
    }

    versionCheck.handleConfirmUpgrade()
  }

  /** Check for in-app updates */
  override fun checkUpdates() {
    val versionCheck =
        versionCheckDelegate.requireNotNull { "VersionCheckDelegate is NULL, was this destroyed?" }
    val options = appOptions.requireNotNull { "AppOptions is NULL, was this destroyed?" }

    checkUpdates(versionCheck, options)
  }
}
