/*
 * Copyright 2020 Peter Kenji Yamanaka
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
 */

package com.pyamsoft.pydroid.arch

import android.os.Bundle
import androidx.annotation.CheckResult
import androidx.lifecycle.LifecycleOwner
import com.pyamsoft.pydroid.util.doOnDestroy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Create a pydroid-arch Component using a UiViewModel, one or more UiViews, and a Controller
 */
@CheckResult
public inline fun <S : UiViewState, V : UiViewEvent, C : UiControllerEvent> createComponent(
    savedInstanceState: Bundle?,
    owner: LifecycleOwner,
    viewModel: UiViewModel<S, V, C>,
    vararg views: UiView<S, V>,
    crossinline onControllerEvent: (event: C) -> Unit
): StateSaver {
    val reader = UiBundleReader.create(savedInstanceState)

    // Bind view event listeners, inflate and attach
    val viewModelBinding = viewModel.bindToComponent(reader, views) { onControllerEvent(it) }

    // Teardown on destroy
    owner.doOnDestroy {
        viewModelBinding.cancel()
        views.forEach { it.teardown() }
    }

    // State saver
    return StateSaver { outState ->
        val writer = UiBundleWriter.create(outState)
        viewModel.saveState(writer)
        views.forEach { it.saveState(writer) }
    }
}

/**
 * Bind a ViewHolder to the pydroid-arch style using one or more UiViews
 */
@Deprecated("Use createViewBinder and manually call teardown at the end of the Controller scope instead of using a LifecycleOwner.")
@CheckResult
public inline fun <S : UiViewState, V : UiViewEvent> bindViews(
    owner: LifecycleOwner,
    vararg views: UiView<S, V>,
    crossinline onViewEvent: (event: V) -> Unit
): ViewBinder<S> {
    val reader = UiBundleReader.create(null)

    // Bind view event listeners
    views.forEach { v ->
        v.onViewEvent { onViewEvent(it) }
    }

    // Init first
    views.forEach { it.init(reader) }

    // Inflate and attach
    views.forEach { it.inflate(reader) }

    // Teardown on destroy
    owner.doOnDestroy {
        views.forEach { it.teardown() }
    }

    return object : ViewBinder<S> {

        override fun bind(state: S) {
            val bound = state.bind()
            views.forEach { it.render(bound) }
        }

        override fun teardown() {
            // Intentionally blank
            // Kept blank for API compatibility
        }
    }
}

/**
 * Bind a ViewHolder to the pydroid-arch style using one or more UiViews
 */
@CheckResult
public inline fun <S : UiViewState, V : UiViewEvent> createViewBinder(
    vararg views: UiView<S, V>,
    crossinline onViewEvent: (event: V) -> Unit
): ViewBinder<S> {
    val reader = UiBundleReader.create(null)

    // Bind view event listeners
    views.forEach { v ->
        v.onViewEvent { onViewEvent(it) }
    }

    // Init first
    views.forEach { it.init(reader) }

    // Inflate and attach
    views.forEach { it.inflate(reader) }

    return object : ViewBinder<S> {

        override fun bind(state: S) {
            val bound = state.bind()
            views.forEach { it.render(bound) }
        }

        override fun teardown() {
            views.forEach { it.teardown() }
        }
    }
}

/**
 * Convert a state to a BoundUiRender<S>
 */
@CheckResult
@PublishedApi
internal fun <S : UiViewState> S.bind(): UiRender<S> {
    return BoundUiRender(this)
}

/**
 * Since a view holder is bound from its own list lifecycle, we do not need to
 * pull changes from a stream, since the List handles the stream itself.
 *
 * We can just apply the state as is to all bound views.
 */
private class BoundUiRender<S>(private val state: S) : UiRender<S> {

    override fun render(scope: CoroutineScope, onRender: (state: S) -> Unit) {
        scope.launch(context = Dispatchers.Main) {
            onRender(state)
        }
    }

    override fun <T> distinctBy(distinctBy: (state: S) -> T): UiRender<T> {
        return BoundUiRender(distinctBy(state))
    }

    override fun distinct(areEquivalent: (old: S, new: S) -> Boolean): UiRender<S> {
        return this
    }

}
