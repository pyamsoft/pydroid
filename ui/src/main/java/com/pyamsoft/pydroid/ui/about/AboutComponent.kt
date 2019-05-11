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
import com.pyamsoft.pydroid.ui.app.ToolbarActivity

internal interface AboutComponent {

  fun inject(fragment: AboutFragment)

  interface Factory {

    @CheckResult
    fun create(
      parent: ViewGroup,
      owner: LifecycleOwner,
      toolbarActivity: ToolbarActivity,
      backstack: Int
    ): AboutComponent

  }

  class Impl private constructor(
    private val parent: ViewGroup,
    private val owner: LifecycleOwner,
    private val backstack: Int,
    private val toolbarActivity: ToolbarActivity,
    private val schedulerProvider: SchedulerProvider,
    private val module: AboutModule
  ) : AboutComponent {

    override fun inject(fragment: AboutFragment) {
      val listViewModel = AboutListViewModel(module.provideInteractor(), schedulerProvider)
      val listView = AboutListView(owner, parent)
      val spinnerView = AboutSpinnerView(parent)

      val toolbar = AboutToolbarView(backstack, toolbarActivity)
      val toolbarViewModel = AboutToolbarViewModel()

      fragment.listView = listView
      fragment.listViewModel = listViewModel
      fragment.spinnerView = spinnerView
      fragment.toolbar = toolbar
      fragment.toolbarViewModel = toolbarViewModel
    }

    class FactoryImpl internal constructor(
      private val schedulerProvider: SchedulerProvider,
      private val module: AboutModule
    ) : Factory {

      override fun create(
        parent: ViewGroup,
        owner: LifecycleOwner,
        toolbarActivity: ToolbarActivity,
        backstack: Int
      ): AboutComponent {
        return Impl(parent, owner, backstack, toolbarActivity, schedulerProvider, module)
      }

    }

  }
}
