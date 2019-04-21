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

package com.pyamsoft.pydroid.ui.about.listitem

import android.view.ViewGroup
import androidx.annotation.CheckResult
import com.pyamsoft.pydroid.bootstrap.SchedulerProvider
import com.pyamsoft.pydroid.bootstrap.libraries.OssLibrary
import com.pyamsoft.pydroid.core.bus.EventBus
import com.pyamsoft.pydroid.ui.about.listitem.AboutItemHandler.AboutItemHandlerEvent

internal interface AboutItemComponent {

  fun inject(viewHolder: AboutViewHolder)

  interface Factory {

    @CheckResult
    fun create(
      parent: ViewGroup,
      model: OssLibrary
    ): AboutItemComponent

  }

  class Impl private constructor(
    private val parent: ViewGroup,
    private val model: OssLibrary,
    private val schedulerProvider: SchedulerProvider,
    private val bus: EventBus<AboutItemHandlerEvent>
  ) : AboutItemComponent {

    override fun inject(viewHolder: AboutViewHolder) {
      val handler = AboutItemHandler(schedulerProvider, bus)
      val viewModel = AboutItemViewModel(handler)
      val title = AboutItemTitleView(model, parent)
      val actions = AboutItemActionsView(model, parent, handler)
      val description = AboutItemDescriptionView(model, parent)
      val component = AboutViewHolderUiComponentImpl(title, actions, description, viewModel)
      viewHolder._component = component
    }

    class FactoryImpl internal constructor(
      private val schedulerProvider: SchedulerProvider,
      private val bus: EventBus<AboutItemHandlerEvent>
    ) : Factory {

      override fun create(
        parent: ViewGroup,
        model: OssLibrary
      ): AboutItemComponent {
        return Impl(parent, model, schedulerProvider, bus)
      }

    }

  }

}
