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

import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.pyamsoft.pydroid.core.Logger
import com.pyamsoft.pydroid.core.requireNotNull
import com.pyamsoft.pydroid.inject.Injector
import com.pyamsoft.pydroid.ui.PYDroidComponent
import com.pyamsoft.pydroid.ui.internal.app.AppComponent
import com.pyamsoft.pydroid.ui.internal.app.AppInternalViewModeler
import com.pyamsoft.pydroid.ui.internal.billing.BillingDelegate
import com.pyamsoft.pydroid.ui.internal.changelog.ChangeLogProvider
import com.pyamsoft.pydroid.ui.internal.datapolicy.DataPolicyDelegate
import com.pyamsoft.pydroid.ui.internal.rating.RatingDelegate
import com.pyamsoft.pydroid.ui.internal.version.VersionCheckDelegate
import com.pyamsoft.pydroid.util.doOnCreate

/**
 * The base Activity class for PYDroid.
 *
 * You are required to extend this class so that other ui bits work.
 */
public abstract class PYDroidActivity : AppCompatActivity(), ChangeLogProvider {

  /** DataPolicy Delegate */
  internal var dataPolicy: DataPolicyDelegate? = null

  /** Billing Delegate */
  internal var billing: BillingDelegate? = null

  /** Rating Delegate */
  internal var rating: RatingDelegate? = null

  /** Version Check Delegate */
  internal var versionCheck: VersionCheckDelegate? = null

  /** Disable the billing component */
  protected open val disableBilling: Boolean = false

  /** Disable the rating component */
  protected open val disableRating: Boolean = false

  /** Disable the version check component */
  protected open val disableVersionCheck: Boolean = false

  /** Disable the data policy component */
  protected open val disableDataPolicy: Boolean = false

  /** Injector component for Dialog and Fragment injection */
  private var injector: AppComponent? = null

  /** Presenter */
  internal var presenter: AppInternalViewModeler? = null

  init {
    connectBilling()
    connectRating()
    connectVersionCheck()
    connectDataPolicy()
  }

  private fun showDataPolicyDisclosure() {
    if (disableDataPolicy) {
      Logger.w("Application has disabled the Data Policy component")
      return
    }

    // Attempt to show the data policy if we are not disabled
    dataPolicy.requireNotNull().showDataPolicyDisclosure()
  }

  /** Attempts to connect to in-app billing */
  private fun connectBilling() {
    if (disableBilling) {
      Logger.w("Application has disabled the billing component")
      return
    }

    this.doOnCreate {
      Logger.d("Attempt Connect Billing")
      billing.requireNotNull().connect()
    }
  }

  /** Attempts to connect to in-app rating */
  private fun connectRating() {
    if (disableRating) {
      Logger.w("Application has disabled the Rating component")
      return
    }

    this.doOnCreate {
      Logger.d("Attempt Connect Rating")
      rating.requireNotNull().bindEvents()
    }
  }

  /** Attempts to connect to in-app data policy dialog */
  private fun connectDataPolicy() {
    if (disableDataPolicy) {
      Logger.w("Application has disabled the Data Policy component")
      return
    }

    this.doOnCreate {
      Logger.d("Attempt Connect Data Policy")
      dataPolicy.requireNotNull().bindEvents()
    }
  }

  /** Attempts to connect to in-app updates */
  private fun connectVersionCheck() {
    if (disableVersionCheck) {
      Logger.w("Application has disabled the VersionCheck component")
      return
    }

    this.doOnCreate {
      Logger.d("Attempt Connect Version Check")
      versionCheck.requireNotNull().bindEvents()
    }
  }

  /**
   * Rating Attempt to call in-app rating dialog. Does not always result in showing the Dialog, that
   * is up to Google
   */
  public fun loadInAppRating() {
    if (disableRating) {
      Logger.w("Application has disabled the Rating component")
      return
    }

    rating.requireNotNull().loadInAppRating()
  }

  /** Confirm the potential version upgrade */
  public fun confirmUpgrade() {
    if (disableVersionCheck) {
      Logger.w("Application has disabled the VersionCheck component")
      return
    }

    versionCheck.requireNotNull().handleConfirmUpgrade()
  }

  /** Check for in-app updates */
  public fun checkUpdates() {
    if (disableVersionCheck) {
      Logger.w("Application has disabled the VersionCheck component")
      return
    }

    versionCheck.requireNotNull().checkUpdates()
  }

  /** On activity create */
  @CallSuper
  override fun onCreate(savedInstanceState: Bundle?) {
    // Must inject before super.onCreate or else getSystemService will be called and NPE
    injector =
        Injector.obtainFromApplication<PYDroidComponent>(this)
            .plusApp()
            .create(
                activity = this,
                disableDataPolicy = disableDataPolicy,
            )
            .also { it.inject(this) }
    super.onCreate(savedInstanceState)
  }

  /** Check for updates onStart if possible */
  @CallSuper
  override fun onStart() {
    super.onStart()
    checkUpdates()
  }

  /** On Resume show changelog if possible */
  @CallSuper
  override fun onPostResume() {
    super.onPostResume()

    // DialogFragments cannot be shown safely until at least onPostResume
    presenter
        .requireNotNull()
        .handleShowCorrectDialog(
            scope = lifecycleScope,
            onShowDataPolicy = { showDataPolicyDisclosure() },
        )
  }

  /** Get system service */
  @CallSuper
  override fun getSystemService(name: String): Any? =
      when (name) {
        // Must be defined before super.onCreate() is called or this will be null
        AppComponent::class.java.name -> injector.requireNotNull()
        else -> super.getSystemService(name)
      }

  /** On activity destroy */
  @CallSuper
  override fun onDestroy() {
    super.onDestroy()

    billing = null
    rating = null
    versionCheck = null
    dataPolicy = null

    injector = null
    presenter = null
  }
}
