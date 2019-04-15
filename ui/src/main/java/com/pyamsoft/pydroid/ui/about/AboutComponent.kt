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
import androidx.annotation.CheckResult
import androidx.lifecycle.LifecycleOwner
import com.pyamsoft.pydroid.bootstrap.SchedulerProvider
import com.pyamsoft.pydroid.bootstrap.about.AboutModule
import com.pyamsoft.pydroid.core.bus.EventBus
import com.pyamsoft.pydroid.ui.about.AboutHandler.AboutHandlerEvent
import com.pyamsoft.pydroid.ui.about.AboutToolbarHandler.ToolbarHandlerEvent
import com.pyamsoft.pydroid.ui.app.ToolbarActivity
import com.pyamsoft.pydroid.ui.navigation.FailedNavigationEvent
import com.pyamsoft.pydroid.ui.navigation.NavigationViewModel
import com.pyamsoft.pydroid.ui.widget.spinner.SpinnerView

internal interface AboutComponent {

  fun inject(fragment: AboutFragment)

  interface Factory {

    @CheckResult
    fun create(
      owner: LifecycleOwner,
      toolbarActivity: ToolbarActivity,
      backstack: Int,
      parent: ViewGroup
    ): AboutComponent

  }

  class Impl private constructor(
    private val owner: LifecycleOwner,
    private val parent: ViewGroup,
    private val backstack: Int,
    private val toolbarActivity: ToolbarActivity,
    private val schedulerProvider: SchedulerProvider,
    private val bus: EventBus<AboutHandlerEvent>,
    private val toolbarBus: EventBus<ToolbarHandlerEvent>,
    private val navigationBus: EventBus<FailedNavigationEvent>,
    private val module: AboutModule
  ) : AboutComponent {

    override fun inject(fragment: AboutFragment) {
      val handler = AboutHandler(schedulerProvider, bus)
      val listView = AboutListView(owner, parent, handler)
      val viewModel = AboutViewModel(handler, module.provideInteractor(), schedulerProvider)
      val spinner = SpinnerView(parent)
      val navigationViewModel = NavigationViewModel(schedulerProvider, navigationBus)
      val component = AboutUiComponentImpl(listView, spinner, viewModel, navigationViewModel)
      fragment.component = component

      val toolbarHandler = AboutToolbarHandler(schedulerProvider, toolbarBus)
      val toolbarViewModel = AboutToolbarViewModel(toolbarHandler)
      val toolbar = AboutToolbarView(backstack, toolbarActivity, toolbarHandler)
      val toolbarComponent = AboutToolbarUiComponentImpl(toolbar, toolbarViewModel)
      fragment.toolbarComponent = toolbarComponent
    }

    class FactoryImpl internal constructor(
      private val schedulerProvider: SchedulerProvider,
      private val bus: EventBus<AboutHandlerEvent>,
      private val toolbarBus: EventBus<ToolbarHandlerEvent>,
      private val navigationBus: EventBus<FailedNavigationEvent>,
      private val module: AboutModule
    ) : Factory {

      override fun create(
        owner: LifecycleOwner,
        toolbarActivity: ToolbarActivity,
        backstack: Int,
        parent: ViewGroup
      ): AboutComponent {
        return Impl(
            owner, parent, backstack,
            toolbarActivity, schedulerProvider, bus,
            toolbarBus, navigationBus, module
        )
      }

    }

  }
}
