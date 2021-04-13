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

@file:JvmName("Component")

package com.pyamsoft.pydroid.arch

import android.os.Bundle
import androidx.annotation.CheckResult
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.pyamsoft.pydroid.arch.Internals.EMPTY_READER
import com.pyamsoft.pydroid.arch.internal.BundleUiSavedStateReader
import com.pyamsoft.pydroid.arch.internal.BundleUiSavedStateWriter
import com.pyamsoft.pydroid.util.doOnDestroy
import kotlinx.coroutines.CoroutineScope

/**
 * Component internals
 */
@PublishedApi
internal object Internals {

    /**
     * A bundle reader with no data
     */
    @JvmStatic
    @PublishedApi
    internal val EMPTY_READER: UiSavedStateReader = BundleUiSavedStateReader(null)

    /**
     * Plumbing for creating a pydroid-arch Component
     */
    @JvmStatic
    @CheckResult
    @PublishedApi
    internal inline fun <S : UiViewState, V : UiViewEvent, C : UiControllerEvent> performCreateComponent(
        savedInstanceState: Bundle?,
        owner: LifecycleOwner,
        controller: UiController<C>,
        views: Array<out UiView<S, out V>>,
        onCreateComponent: CoroutineScope.(UiController<C>, UiSavedStateReader, Array<out UiView<S, out V>>) -> Unit,
    ): StateSaver {
        val reader: UiSavedStateReader = savedInstanceState.toReader()

        // Bind view event listeners, inflate and attach
        owner.lifecycleScope.onCreateComponent(controller, reader, views)

        // Teardown on destroy
        owner.doOnDestroy {
            views.forEach { it.teardown() }
        }

        // State saver
        return StateSaver { outState ->
            val writer: UiSavedStateWriter = BundleUiSavedStateWriter(outState)
            views.forEach { it.saveState(writer) }
        }
    }


    /**
     * Bind view events to the UiView list, and bind the nested UiViews if they exist
     */
    @JvmStatic
    @PublishedApi
    internal fun <S : UiViewState, V : UiViewEvent> bindViewEvents(
        views: List<UiView<S, out V>>,
        onViewEvent: (event: V) -> Unit
    ) {
        views.forEach { v ->
            v.onViewEvent { onViewEvent(it) }

            if (v is BaseUiView<S, out V, *>) {
                val nestedViews = v.nestedViews()
                bindViewEvents(nestedViews, onViewEvent)
            }
        }
    }
}

/**
 * Create a pydroid-arch Component using a UiViewModel, one or more UiViews, and a Controller
 */
@CheckResult
public inline fun <S : UiViewState, V : UiViewEvent, C : UiControllerEvent> createComponent(
    savedInstanceState: Bundle?,
    owner: LifecycleOwner,
    viewModel: UiViewModel<S, C>,
    controller: UiController<C>,
    vararg views: UiView<S, out V>,
    crossinline onViewEvent: (event: V) -> Unit
): StateSaver {
    return Internals.performCreateComponent(
        savedInstanceState,
        owner,
        controller,
        views
    ) { uiController, reader, uiViews ->
        viewModel.bindController(this, uiController)
        viewModel.bindViews(this, reader, *uiViews) { onViewEvent(it) }
    }
}

/**
 * Bind a ViewHolder to the pydroid-arch style using one or more UiViews
 */
@CheckResult
public inline fun <S : UiViewState, V : UiViewEvent> createViewBinder(
    vararg views: UiView<S, out V>,
    crossinline onViewEvent: (event: V) -> Unit
): ViewBinder<S> {
    val reader = EMPTY_READER

    // Bind view event listeners
    Internals.bindViewEvents(views.toList()) { onViewEvent(it) }

    // Init first
    views.forEach { it.init(reader) }

    // Inflate and attach
    views.forEach { it.inflate(reader) }

    return object : ViewBinder<S> {

        override fun bindState(state: S) {
            val bound = state.asUiRender()
            views.forEach { it.render(bound) }
        }

        override fun teardown() {
            views.forEach { it.teardown() }
        }
    }
}

/**
 * Convenience function for converting a nullable Bundle into a SavedStateReader
 */
@CheckResult
public fun Bundle?.toReader(): UiSavedStateReader {
    return BundleUiSavedStateReader(this)
}