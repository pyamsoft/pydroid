/*
 * Copyright 2019 Peter Kenji Yamanaka
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
 *
 */

package com.pyamsoft.pydroid.ui.about

import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import com.pyamsoft.pydroid.bootstrap.SchedulerProvider
import com.pyamsoft.pydroid.bootstrap.about.AboutInteractor
import com.pyamsoft.pydroid.core.bus.EventBus
import com.pyamsoft.pydroid.ui.app.ToolbarActivity
import com.pyamsoft.pydroid.ui.widget.spinner.SpinnerView

internal class AboutComponentImpl(
  private val interactor: AboutInteractor,
  private val toolbarActivity: ToolbarActivity,
  private val backstackCount: Int,
  private val parent: ViewGroup,
  private val owner: LifecycleOwner,
  private val bus: EventBus<LicenseLoadState>,
  private val schedulerProvider: SchedulerProvider
) : AboutComponent {

  override fun inject(fragment: AboutFragment) {
    val presenter = AboutPresenterImpl(interactor, schedulerProvider, bus)
    val listView = AboutListView(owner, parent, presenter)
    val spinnerView = SpinnerView(parent)
    val toolbarView = AboutToolbarView(toolbarActivity, backstackCount, presenter)
    val component = AboutUiComponentImpl(listView, spinnerView, toolbarView, presenter)

    fragment.apply {
      this.component = component
    }
  }

}
