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
import androidx.lifecycle.LifecycleOwner
import com.pyamsoft.pydroid.core.bus.EventBus
import com.pyamsoft.pydroid.ui.navigation.FailedNavigationEvent
import com.pyamsoft.pydroid.ui.navigation.FailedNavigationPresenterImpl

internal class AboutItemComponentImpl internal constructor(
  private val parent: ViewGroup,
  private val owner: LifecycleOwner,
  private val failedNavigationBus: EventBus<FailedNavigationEvent>
) : AboutItemComponent {

  override fun inject(viewHolder: AboutViewHolder) {
    val presenter = AboutItemPresenterImpl(owner)
    val aboutTitleView = AboutItemTitleView(parent)
    val aboutActionsView = AboutItemActionsView(parent, presenter)
    val aboutDescriptionView = AboutItemDescriptionView(parent)

    viewHolder.apply {
      this.titleView = aboutTitleView
      this.actionsView = aboutActionsView
      this.descriptionView = aboutDescriptionView
      this.presenter = presenter
      this.failedNavigationPresenter = FailedNavigationPresenterImpl(owner, failedNavigationBus)
    }
  }

}
