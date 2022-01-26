/*
 * Copyright 2020 Peter Kenji Yamanaka
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
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.pyamsoft.pydroid.core.Logger
import com.pyamsoft.pydroid.core.requireNotNull
import com.pyamsoft.pydroid.inject.Injector
import com.pyamsoft.pydroid.ui.PYDroidComponent
import com.pyamsoft.pydroid.ui.internal.app.AppComponent
import com.pyamsoft.pydroid.ui.internal.app.AppInternalPresenter
import com.pyamsoft.pydroid.ui.internal.billing.BillingDelegate
import com.pyamsoft.pydroid.ui.internal.changelog.ChangeLogDelegate
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

  /** Change Log Delegate */
  internal var changeLog: ChangeLogDelegate? = null

  /** Disable the billing component */
  protected open val disableBilling: Boolean = false

  /** Disable the rating component */
  protected open val disableRating: Boolean = false

  /** Disable the version check component */
  protected open val disableVersionCheck: Boolean = false

  /** Disable the change log component */
  protected open val disableChangeLog: Boolean = false

  /** Disable the data policy component */
  protected open val disableDataPolicy: Boolean = false

  /** Injector component for Dialog and Fragment injection */
  private var injector: AppComponent? = null

  /** Presenter */
  internal var presenter: AppInternalPresenter? = null

  init {
    connectBilling()
    connectRating()
    connectVersionCheck()
    connectChangeLog()
    connectDataPolicy()
  }

  private fun showChangelog() {
    if (disableChangeLog) {
      Logger.w("Application has disabled the Change Log component")
      return
    }

    // Attempt to show the changelog if we are not disabled
    changeLog.requireNotNull().showChangeLog()
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

  /** Attempts to connect to in-app change log */
  private fun connectChangeLog() {
    if (disableChangeLog) {
      Logger.w("Application has disabled the Change Log component")
      return
    }

    this.doOnCreate {
      Logger.d("Attempt Connect Change Log")
      changeLog.requireNotNull().bindEvents()
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
  protected fun loadInAppRating() {
    if (disableRating) {
      Logger.w("Application has disabled the Rating component")
      return
    }

    rating.requireNotNull().loadInAppRating()
  }

  /** Check for in-app updates */
  protected fun checkUpdates() {
    if (disableVersionCheck) {
      Logger.w("Application has disabled the VersionCheck component")
      return
    }

    versionCheck.requireNotNull().checkUpdates()
  }

  /**
   * Version Check screen
   *
   * All UI and function related to checking for new updates to Applications
   */
  @Composable
  protected fun VersionCheckScreen(
      scaffoldState: ScaffoldState,
  ) {
    versionCheck
        .requireNotNull()
        .VersionCheck(
            scaffoldState = scaffoldState,
        )
  }

  /**
   * Version Check screen
   *
   * All UI and function related to checking for new updates to Applications
   */
  @Composable
  protected fun VersionCheckScreen(
      modifier: Modifier = Modifier,
      snackbarHostState: SnackbarHostState,
  ) {
    versionCheck
        .requireNotNull()
        .VersionCheck(
            modifier = modifier,
            snackbarHostState = snackbarHostState,
        )
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
                disableChangeLog = disableChangeLog,
            )
            .also { c -> c.inject(this) }
    super.onCreate(savedInstanceState)
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
            onShowChangeLog = { showChangelog() },
            onShowVersionCheck = { checkUpdates() },
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
    changeLog = null
    dataPolicy = null

    injector = null
    presenter = null
  }
}
