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
import com.pyamsoft.pydroid.core.Logger
import com.pyamsoft.pydroid.inject.Injector
import com.pyamsoft.pydroid.ui.PYDroidComponent
import com.pyamsoft.pydroid.ui.internal.app.AppProvider
import com.pyamsoft.pydroid.ui.internal.app.BillingDelegate
import com.pyamsoft.pydroid.ui.internal.app.ProtectionDelegate
import com.pyamsoft.pydroid.util.doOnCreate

/**
 * The base Activity class for PYDroid.
 *
 * You are required to extend this class so that other ui bits work.
 */
public abstract class PYDroidActivity : AppCompatActivity(), AppProvider {

  /** Billing Delegate */
  internal var billing: BillingDelegate? = null

  /** Billing Delegate */
  internal var protection: ProtectionDelegate? = null

  /** Disable the billing component */
  protected open val disableBilling: Boolean = false

  /** Disable the protection component */
  protected open val disableProtection: Boolean = false

  init {
    protectApplication()
    connectBilling()
  }

  /** On activity create */
  @CallSuper
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    Injector.obtainFromApplication<PYDroidComponent>(this)
  }

  /** Attempts to connect to in-app billing */
  private fun connectBilling() {
    if (disableBilling) {
      Logger.w("Application has disabled the billing component")
      return
    }

    this.doOnCreate {
      Logger.d("Attempt Connect Billing")
      billing?.connect(this)
    }
  }

  /** Attempts to connect to in-app billing */
  private fun protectApplication() {
    if (disableBilling) {
      Logger.w("Application has disabled the protection component")
      return
    }

    this.doOnCreate {
      Logger.d("Attempt Protect Application")
      protection?.connect(this)
    }
  }

  /** Get system service */
  @CallSuper
  override fun getSystemService(name: String): Any? {
    billing?.getSystemService(name)?.let { service ->
      Logger.d("BillingDelegate provided service: $service")
      return service
    }

    return super.getSystemService(name)
  }

  /** On activity destroy */
  @CallSuper
  override fun onDestroy() {
    super.onDestroy()

    billing = null
    protection = null
  }
}
