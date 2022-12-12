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

package com.pyamsoft.pydroid.ui.app

import androidx.annotation.CheckResult
import androidx.appcompat.app.AppCompatActivity
import com.pyamsoft.pydroid.core.requireNotNull
import com.pyamsoft.pydroid.ui.changelog.ChangeLogProvider
import com.pyamsoft.pydroid.ui.internal.pydroid.PYDroidActivityDelegateInternal
import com.pyamsoft.pydroid.ui.internal.pydroid.PYDroidActivityInstallTracker
import com.pyamsoft.pydroid.ui.internal.pydroid.PYDroidApplicationInstallTracker
import com.pyamsoft.pydroid.util.doOnCreate
import com.pyamsoft.pydroid.util.doOnDestroy

/**
 * The base Activity class for PYDroid.
 *
 * You are required to extend this class so that other ui bits work.
 *
 * Also see [installPYDroid] and [PYDroidActivityDelegate]
 */
@Deprecated(
    """Use AppCompatActivity.installPYDroid(provider, options) instead.

PYDroidActivity requires that an implementation extend it, which may not be easy for
an application which already has its own BaseActivity style implementation.

Users are instead encouraged to use the extension function AppCompatActivity.installPYDroid()
which will set up all of the expected bits of the old PYDroidActivity but can be used
with any kind of BaseActivity as long as it extends from the AppCompat library's AppCompatActivity
(as PYDroid internally relies on the use of FragmentActivity and its LifecycleOwner)
""")
public abstract class PYDroidActivity :
    AppCompatActivity(), ChangeLogProvider, PYDroidActivityDelegate {

  /** Disable the billing component */
  protected open val disableBilling: Boolean = false

  /** Disable the rating component */
  protected open val disableRating: Boolean = false

  /** Disable the version check component */
  protected open val disableVersionCheck: Boolean = false

  /** Disable the data policy component */
  protected open val disableDataPolicy: Boolean = false

  /** Disable the changelog component */
  protected open val disableChangeLog: Boolean = false

  /** Activity delegate */
  private var delegate: PYDroidActivityDelegate? = null

  init {
    this.doOnCreate {
      delegate =
          installPYDroid(
              provider = this,
              options =
                  PYDroidActivityOptions(
                      disableBilling = disableBilling,
                      disableRating = disableRating,
                      disableVersionCheck = disableVersionCheck,
                      disableDataPolicy = disableDataPolicy,
                      disableChangeLog = disableChangeLog,
                  ),
          )
    }

    this.doOnDestroy { delegate = null }
  }

  /**
   * Rating Attempt to call in-app rating dialog. Does not always result in showing the Dialog, that
   * is up to Google
   */
  override fun loadInAppRating() {
    delegate.requireNotNull().loadInAppRating()
  }

  /** Confirm the potential version upgrade */
  override fun confirmUpgrade() {
    delegate.requireNotNull().confirmUpgrade()
  }

  /** Check for in-app updates */
  override fun checkUpdates() {
    delegate.requireNotNull().checkUpdates()
  }

  override fun showChangelog() {
    delegate.requireNotNull().showChangelog()
  }
}

@CheckResult
private fun createPYDroidDelegate(
    activity: AppCompatActivity,
    provider: ChangeLogProvider,
    options: PYDroidActivityOptions,
): PYDroidActivityDelegateInternal {
  val component =
      PYDroidApplicationInstallTracker.retrieve(activity.application)
          .injector()
          .plusApp()
          .create(options)
  return PYDroidActivityDelegateInternal(component, provider, activity)
}

/**
 * Install PYDroid into an Activity
 *
 * Returns a delegate that can optionally be saved or used in the calling Activity level to handle
 * common functions like checking for updates or showing an in-app review dialog
 */
@JvmOverloads
public fun AppCompatActivity.installPYDroid(
    provider: ChangeLogProvider,
    options: PYDroidActivityOptions = PYDroidActivityOptions(),
): PYDroidActivityDelegate {
  val self = this
  val internals = createPYDroidDelegate(self, provider, options)
  PYDroidActivityInstallTracker.install(self, internals)
  return internals
}
