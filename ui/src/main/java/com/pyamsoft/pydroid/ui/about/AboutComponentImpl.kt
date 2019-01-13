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
import com.pyamsoft.pydroid.bootstrap.about.AboutModule
import com.pyamsoft.pydroid.core.bus.EventBus
import com.pyamsoft.pydroid.core.bus.Listener
import com.pyamsoft.pydroid.ui.widget.spinner.SpinnerView

internal class AboutComponentImpl(
  private val aboutModule: AboutModule,
  private val parent: ViewGroup,
  private val owner: LifecycleOwner,
  private val controllerBus: EventBus<AboutStateEvents>,
  private val uiBus: Listener<AboutViewEvents>,
  private val schedulerProvider: SchedulerProvider
) : AboutComponent {

  override fun inject(fragment: AboutFragment) {
    val listView = AboutListView(parent)
    val spinnerView = SpinnerView(parent)
    fragment.presenter = AboutPresenter(aboutModule.interactor, controllerBus, schedulerProvider)
    fragment.loadingComponent = AboutLoadingUiComponent(spinnerView, owner, controllerBus)
    fragment.listComponent = AboutListUiComponent(
        owner, listView, controllerBus, uiBus, schedulerProvider
    )
  }

}
