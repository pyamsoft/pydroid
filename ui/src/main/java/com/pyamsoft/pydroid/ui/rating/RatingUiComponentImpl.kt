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

package com.pyamsoft.pydroid.ui.rating

import android.os.Bundle
import androidx.lifecycle.LifecycleOwner
import com.pyamsoft.pydroid.arch.BaseUiComponent
import com.pyamsoft.pydroid.arch.doOnDestroy
import com.pyamsoft.pydroid.ui.arch.InvalidIdException
import com.pyamsoft.pydroid.ui.rating.RatingUiComponent.Callback
import com.pyamsoft.pydroid.ui.rating.RatingViewModel.RatingState

internal class RatingUiComponentImpl internal constructor(
  private val viewModel: RatingViewModel
) : BaseUiComponent<Callback>(),
    RatingUiComponent {

  override fun id(): Int {
    throw InvalidIdException
  }

  override fun onBind(
    owner: LifecycleOwner,
    savedInstanceState: Bundle?,
    callback: Callback
  ) {
    owner.doOnDestroy {
      viewModel.unbind()
    }

    viewModel.bind { state, oldState ->
      renderShowRating(state, oldState)
    }
  }

  private fun renderShowRating(
    state: RatingState,
    oldState: RatingState?
  ) {
    state.renderOnChange(oldState, value = { it.showRating }) { show ->
      if (show) {
        callback.onShowRating()
      }
    }
  }

  override fun onSaveState(outState: Bundle) {
  }

}
