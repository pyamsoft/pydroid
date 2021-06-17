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
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import com.pyamsoft.pydroid.billing.BillingConnector
import com.pyamsoft.pydroid.inject.Injector
import com.pyamsoft.pydroid.ui.PYDroidComponent
import com.pyamsoft.pydroid.ui.internal.app.AppProvider
import com.pyamsoft.pydroid.ui.internal.billing.BillingComponent
import com.pyamsoft.pydroid.ui.theme.Theming
import kotlinx.coroutines.launch

/**
 * The base Activity class for PYDroid.
 *
 * You are required to extend this class so that other ui bits work.
 */
public abstract class ActivityBase :
    AppCompatActivity(), ToolbarActivity, ToolbarActivityProvider, AppProvider {

  /** The activity scoped component graph for the BillingDialog */
  private var injector: BillingComponent? = null

  /** The connection to the Billing client */
  internal var billingConnector: BillingConnector? = null

  /** The main view container for all page level fragment transactions */
  public abstract val fragmentContainerId: Int

  /** Activity level toolbar, similar to ActionBar */
  private var capturedToolbar: Toolbar? = null

  /** Activity theming */
  internal var theming: Theming? = null

  /** On activity create */
  @CallSuper
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    injector =
        Injector.obtainFromApplication<PYDroidComponent>(this).plusBilling().create().also {
            component ->
          component.inject(this)
        }

    requireNotNull(billingConnector).connect()

    lifecycleScope.launch { requireNotNull(theming).init() }
  }

  /** Get system service */
  @CallSuper
  override fun getSystemService(name: String): Any? =
      when (name) {
        BillingComponent::class.java.name -> requireNotNull(injector)
        else -> super.getSystemService(name)
      }

  /** On activity destroy */
  @CallSuper
  override fun onDestroy() {
    super.onDestroy()

    // Disconnect billing
    billingConnector?.disconnect()

    // Clear captured Toolbar
    capturedToolbar = null

    // Clear billing
    injector = null
    billingConnector = null
  }

  final override fun withToolbar(func: (Toolbar) -> Unit) {
    capturedToolbar?.let(func)
  }

  final override fun requireToolbar(func: (Toolbar) -> Unit) {
    requireNotNull(capturedToolbar).let(func)
  }

  final override fun setToolbar(toolbar: Toolbar?) {
    capturedToolbar = toolbar
  }
}
