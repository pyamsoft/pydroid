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

import android.app.Activity
import android.view.ViewGroup
import androidx.annotation.CheckResult
import androidx.lifecycle.LifecycleOwner
import com.pyamsoft.pydroid.ui.PYDroidViewModelFactory
import com.pyamsoft.pydroid.ui.app.ToolbarActivity

internal interface AboutComponent {

  fun inject(fragment: AboutFragment)

  interface Factory {

    @CheckResult
    fun create(
      activity: Activity,
      parent: ViewGroup,
      owner: LifecycleOwner,
      toolbarActivity: ToolbarActivity,
      backstack: Int
    ): AboutComponent

  }

  class Impl private constructor(
    private val activity: Activity,
    private val parent: ViewGroup,
    private val owner: LifecycleOwner,
    private val backstack: Int,
    private val toolbarActivity: ToolbarActivity,
    private val factoryProvider: (activity: Activity) -> PYDroidViewModelFactory
  ) : AboutComponent {

    override fun inject(fragment: AboutFragment) {
      val listView = AboutListView(owner, parent)
      val spinnerView = AboutSpinnerView(parent)

      val toolbar = AboutToolbarView(backstack, toolbarActivity)

      fragment.factory = factoryProvider(activity)
      fragment.listView = listView
      fragment.spinnerView = spinnerView
      fragment.toolbar = toolbar
    }

    class FactoryImpl internal constructor(
      private val factoryProvider: (activity: Activity) -> PYDroidViewModelFactory
    ) : Factory {

      override fun create(
        activity: Activity,
        parent: ViewGroup,
        owner: LifecycleOwner,
        toolbarActivity: ToolbarActivity,
        backstack: Int
      ): AboutComponent {
        return Impl(activity, parent, owner, backstack, toolbarActivity, factoryProvider)
      }

    }

  }
}
