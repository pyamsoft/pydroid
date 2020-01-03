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

@CheckResult
inline fun <S : UiViewState, V : UiViewEvent, C : UiControllerEvent> createComponent(
    savedInstanceState: Bundle?,
    owner: LifecycleOwner,
    viewModel: UiViewModel<S, V, C>,
    vararg views: UiView<S, V>,
    crossinline onControllerEvent: (event: C) -> Unit
): StateSaver {
    // Init first
    views.forEach { it.init(savedInstanceState) }

    // Bind view event listeners, inflate and attach
    val viewModelBinding = viewModel.render(savedInstanceState, *views) { onControllerEvent(it) }
    owner.doOnDestroy {
        viewModelBinding.cancel()
        views.forEach { it.teardown() }
    }

    // State saver
    return object : StateSaver {

        override fun saveState(outState: Bundle) {
            viewModel.saveState(outState)
            views.forEach { it.saveState(outState) }
        }
    }
}

@CheckResult
fun <S : UiViewState, V : UiViewEvent> bindViews(
    owner: LifecycleOwner,
    vararg views: UiView<S, V>,
    onViewEvent: suspend (event: V) -> Unit
): ViewBinder<S> {
    // Init first
    views.forEach { it.init(null) }

    // Bind view event listeners
    views.forEach {
        owner.lifecycleScope.launch(context = Dispatchers.Default) {
            it.onViewEvent(onViewEvent)
        }
    }

    // Inflate and attach
    views.forEach { it.inflate(null) }
    owner.doOnDestroy { views.forEach { it.teardown() } }

    // State saver
    return object : ViewBinder<S> {
        override fun bind(state: S) {
            views.forEach { it.render(state, UiSavedState.EMPTY) }
        }
    }
}
