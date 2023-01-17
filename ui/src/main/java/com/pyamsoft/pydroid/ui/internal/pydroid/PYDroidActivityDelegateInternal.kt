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
import com.pyamsoft.pydroid.core.requireNotNull
import com.pyamsoft.pydroid.ui.app.PYDroidActivityDelegate
import com.pyamsoft.pydroid.ui.billing.BillingUpsell
import com.pyamsoft.pydroid.ui.changelog.ChangeLogProvider
import com.pyamsoft.pydroid.ui.changelog.ShowUpdateChangeLog
import com.pyamsoft.pydroid.ui.datapolicy.ShowDataPolicy
import com.pyamsoft.pydroid.ui.internal.app.AppComponent
import com.pyamsoft.pydroid.ui.internal.rating.RatingDelegate
import com.pyamsoft.pydroid.ui.version.VersionUpdateProgress
import com.pyamsoft.pydroid.ui.version.VersionUpgradeAvailable
import com.pyamsoft.pydroid.util.doOnDestroy

internal class PYDroidActivityDelegateInternal
internal constructor(
    component: AppComponent,
    provider: ChangeLogProvider,
    activity: FragmentActivity,
) : PYDroidActivityDelegate {

  private var appComponent: AppComponent? = component
  private var appProvider: ChangeLogProvider? = provider

  // Copy these out of PYDroidActivityComponents so we can null them out onDestroy
  private var ratingDelegate: RatingDelegate?
  private var versionUpgradeAvailable: VersionUpgradeAvailable?
  private var versionUpdateProgress: VersionUpdateProgress?
  private var showUpdateChangeLog: ShowUpdateChangeLog?
  private var showDataPolicy: ShowDataPolicy?
  private var billingUpsell: BillingUpsell?

  init {
    val components = component.create(activity)

    val rd = components.rating

    ratingDelegate = rd
    versionUpgradeAvailable = components.versionUpgrader
    versionUpdateProgress = components.versionUpdateProgress
    showUpdateChangeLog = components.showUpdateChangeLog
    billingUpsell = components.billingUpsell
    showDataPolicy = components.dataPolicy

    activity.doOnDestroy {
      appComponent = null
      appProvider = null

      ratingDelegate = null
      versionUpgradeAvailable = null
      versionUpdateProgress = null
      billingUpsell = null
      showDataPolicy = null
    }
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

  /** Used in ShowDataPolicyDialog */
  @CheckResult
  internal fun dataPolicy(): ShowDataPolicy {
    return showDataPolicy.requireNotNull { "ShowDataPolicy is NULL, was this destroyed?" }
  }

  /** Used in ChangeLogWidget */
  @CheckResult
  internal fun changeLog(): ShowUpdateChangeLog {
    return showUpdateChangeLog.requireNotNull { "ShowUpdateChangeLog is NULL, was this destroyed?" }
  }

  /** Used in BillingUpsellWidget */
  @CheckResult
  internal fun billingUpsell(): BillingUpsell {
    return billingUpsell.requireNotNull { "BillingUpsell is NULL, was this destroyed?" }
  }

  /**
   * Rating Attempt to call in-app rating dialog. Does not always result in showing the Dialog, that
   * is up to Google
   */
  override fun loadInAppRating() {
    ratingDelegate
        .requireNotNull { "RatingDelegate is NULL, was this destroyed?" }
        .loadInAppRating()
  }

  /** Check for in-app updates */
  override fun checkUpdates() {
    // TODO left blank
  }
}
