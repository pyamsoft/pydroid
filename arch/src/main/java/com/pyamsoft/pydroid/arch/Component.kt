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

package com.pyamsoft.pydroid.arch

import android.os.Bundle
import androidx.annotation.CheckResult
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

inline fun <S : UiViewState, V : UiViewEvent, C : UiControllerEvent> createComponent(
    savedInstanceState: Bundle?,
    owner: LifecycleOwner,
    viewModel: UiViewModel<S, V, C>,
    vararg views: RenderableUiView<S, V>,
    crossinline onControllerEvent: (event: C) -> Unit
) {
    views.forEach { it.inflate(savedInstanceState) }
    val viewModelBinding = viewModel.render(savedInstanceState, *views) { onControllerEvent(it) }
    owner.doOnDestroy {
        viewModelBinding.cancel()
        views.forEach { it.teardown() }
    }
}

@CheckResult
fun <S : UiViewState, V : UiViewEvent> bindViews(
    owner: LifecycleOwner,
    vararg views: BindableUiView<S, V>,
    onViewEvent: suspend (event: V) -> Unit
): Bindable<S> {
    views.forEach { it.inflate(null) }
    views.forEach {
        owner.lifecycleScope.launch(context = Dispatchers.Default) {
            it.onViewEvent(onViewEvent)
        }
    }
    owner.doOnDestroy { views.forEach { it.teardown() } }

    return object : Bindable<S> {
        override fun bind(state: S) {
            views.forEach { it.bind(state) }
        }

        override fun unbind() {
            views.forEach { it.unbind() }
        }
    }
}

