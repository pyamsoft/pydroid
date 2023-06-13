/*
 * Copyright 2023 pyamsoft
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

package com.pyamsoft.pydroid.ui.internal.settings

import androidx.activity.ComponentActivity
import com.pyamsoft.pydroid.ui.app.PYDroidActivityOptions
import com.pyamsoft.pydroid.ui.inject.ComposableInjector
import com.pyamsoft.pydroid.ui.internal.billing.BillingViewModeler
import com.pyamsoft.pydroid.ui.internal.changelog.ChangeLogViewModeler
import com.pyamsoft.pydroid.ui.internal.pydroid.ObjectGraph
import com.pyamsoft.pydroid.ui.internal.version.VersionCheckViewModeler

internal class SettingsInjector internal constructor() : ComposableInjector() {

  internal var options: PYDroidActivityOptions? = null
  internal var viewModel: SettingsViewModeler? = null
  internal var versionViewModel: VersionCheckViewModeler? = null
  internal var changeLogViewModel: ChangeLogViewModeler? = null
  internal var billingViewModel: BillingViewModeler? = null

  override fun onInject(activity: ComponentActivity) {
    ObjectGraph.ActivityScope.retrieve(activity).injector().plusSettings().create().inject(this)
  }

  override fun onDispose() {
    options = null
    viewModel = null
    versionViewModel = null
    changeLogViewModel = null
    billingViewModel = null
  }
}
