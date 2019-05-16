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

package com.pyamsoft.pydroid.arch.impl

import android.os.Bundle
import androidx.lifecycle.LifecycleOwner
import com.pyamsoft.pydroid.arch.UiControllerEvent
import com.pyamsoft.pydroid.arch.UiView
import com.pyamsoft.pydroid.arch.UiViewEvent
import com.pyamsoft.pydroid.arch.UiViewState
import com.pyamsoft.pydroid.core.tryDispose

inline fun <S : UiViewState, V : UiViewEvent, C : UiControllerEvent> createComponent(
  savedInstanceState: Bundle?,
  owner: LifecycleOwner,
  viewModel: BaseUiViewModel<S, V, C>,
  vararg views: UiView<S, V>,
  crossinline onControllerEvent: (event: C) -> Unit
) {
  views.forEach { it.inflate(savedInstanceState) }
  val viewModelBinding = viewModel.render(*views) { onControllerEvent(it) }
  owner.doOnDestroy {
    viewModelBinding.tryDispose()
    views.forEach { it.teardown() }
  }
}

