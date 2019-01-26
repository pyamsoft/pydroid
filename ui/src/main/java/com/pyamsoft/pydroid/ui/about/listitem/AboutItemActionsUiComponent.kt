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

import androidx.lifecycle.LifecycleOwner
import com.pyamsoft.pydroid.bootstrap.SchedulerProvider
import com.pyamsoft.pydroid.bootstrap.libraries.OssLibrary
import com.pyamsoft.pydroid.core.bus.Listener
import com.pyamsoft.pydroid.ui.about.AboutViewEvent
import com.pyamsoft.pydroid.ui.arch.BaseUiComponent

internal class AboutItemActionsUiComponent internal constructor(
  schedulerProvider: SchedulerProvider,
  view: AboutItemActionsView,
  uiBus: Listener<AboutViewEvent>,
  owner: LifecycleOwner
) : BaseUiComponent<AboutViewEvent, AboutItemActionsView>(view, uiBus, owner, schedulerProvider),
    BaseAboutItem {

  override fun bind(model: OssLibrary) {
    view.bind(model)
  }

  override fun unbind() {
    view.unbind()
  }

}
