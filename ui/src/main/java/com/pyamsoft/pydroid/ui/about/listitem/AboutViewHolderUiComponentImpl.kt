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

import android.os.Bundle
import androidx.lifecycle.LifecycleOwner
import com.pyamsoft.pydroid.arch.BaseUiComponent
import com.pyamsoft.pydroid.arch.doOnDestroy
import com.pyamsoft.pydroid.ui.about.listitem.AboutItemViewModel.AboutItemState
import com.pyamsoft.pydroid.ui.about.listitem.AboutViewHolderUiComponent.Callback
import com.pyamsoft.pydroid.ui.arch.InvalidIdException
import javax.inject.Inject

internal class AboutViewHolderUiComponentImpl @Inject internal constructor(
  private val titleView: AboutItemTitleView,
  private val actionsView: AboutItemActionsView,
  private val descriptionView: AboutItemDescriptionView,
  private val viewModel: AboutItemViewModel
) : BaseUiComponent<Callback>(),
    AboutViewHolderUiComponent {

  override fun id(): Int {
    throw InvalidIdException
  }

  override fun onBind(
    owner: LifecycleOwner,
    savedInstanceState: Bundle?,
    callback: Callback
  ) {
    owner.doOnDestroy {
      titleView.teardown()
      actionsView.teardown()
      descriptionView.teardown()
      viewModel.unbind()
    }

    titleView.inflate(savedInstanceState)
    actionsView.inflate(savedInstanceState)
    descriptionView.inflate(savedInstanceState)
    viewModel.bind { state, oldState ->
      renderUrl(state, oldState)
    }
  }

  private fun renderUrl(
    state: AboutItemState,
    oldState: AboutItemState?
  ) {
    state.renderOnChange(oldState, value = { it.url }) { url ->
      if (url.isNotBlank()) {
        callback.onNavigateExternalUrl(url)
      }
    }
  }

  override fun onSaveState(outState: Bundle) {
    titleView.saveState(outState)
    actionsView.saveState(outState)
    descriptionView.saveState(outState)
  }

}