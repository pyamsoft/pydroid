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
import com.pyamsoft.pydroid.billing.BillingConnector
import com.pyamsoft.pydroid.core.Logger
import com.pyamsoft.pydroid.core.requireNotNull
import com.pyamsoft.pydroid.inject.Injector
import com.pyamsoft.pydroid.protection.Protection
import com.pyamsoft.pydroid.ui.PYDroidComponent
import com.pyamsoft.pydroid.ui.internal.app.AppProvider
import com.pyamsoft.pydroid.ui.internal.billing.BillingComponent
import com.pyamsoft.pydroid.ui.theme.Theming
import com.pyamsoft.pydroid.util.doOnCreate

/**
 * The base Activity class for PYDroid.
 *
 * You are required to extend this class so that other ui bits work.
 */
public abstract class ActivityBase : AppCompatActivity(), AppProvider {

  /** The activity scoped component graph for the BillingDialog */
  private var injector: BillingComponent? = null

  /** The connection to the Billing client */
  internal var billingConnector: BillingConnector? = null

  /** Activity theming */
  internal var theming: Theming? = null

  /** Activity protection */
  internal var protection: Protection? = null

  init {
    protectApplication()
    connectBilling()
  }

  /** On activity create */
  @CallSuper
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    Injector.obtainFromApplication<PYDroidComponent>(this).also { inject ->
      inject.plusProtection().create().inject(this)
      injector = inject.plusBilling().create().also { c -> c.inject(this) }
    }
  }

  /** Attempts to connect to in-app billing */
  private fun connectBilling() {
    Logger.d("Prepare application billing connection on create callback")
    this.doOnCreate {
      Logger.d("Attempt Connect Billing")
      val billing = billingConnector
      if (billing == null) {
        val msg = "In-App Billing is not initialized!"
        val error = IllegalStateException(msg)
        Logger.e(error, msg)
        throw error
      } else {
        Logger.d("In-App billing is created, connect")
        billing.start(this)
      }
    }
  }

  /** Attempts to load and secure the application */
  private fun protectApplication() {
    Logger.d("Prepare application protection on create callback")

    this.doOnCreate {
      Logger.d("Attempt protection")
      val protector = protection
      if (protector == null) {
        val msg = "Application Protection is not initialized!"
        val error = IllegalStateException(msg)
        Logger.e(error, msg)
        throw error
      } else {
        Logger.d("Application is created, protect")
        protector.defend(this)
      }
    }
  }

  /** Get system service */
  @CallSuper
  override fun getSystemService(name: String): Any? =
      when (name) {
        BillingComponent::class.java.name -> injector.requireNotNull()
        else -> super.getSystemService(name)
      }

  /** On activity destroy */
  @CallSuper
  override fun onDestroy() {
    super.onDestroy()

    injector = null
    billingConnector = null
    protection = null
  }
}
