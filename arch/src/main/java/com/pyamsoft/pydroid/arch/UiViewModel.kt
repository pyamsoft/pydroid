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

import androidx.annotation.CheckResult
import androidx.annotation.UiThread
import androidx.lifecycle.viewModelScope
import com.pyamsoft.pydroid.core.Enforcer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.LazyThreadSafetyMode.NONE

/**
 * A default implementation of a UiStateViewModel which knows how to set up along with UiViews and a UiController to become a full UiComponent
 */
public abstract class UiViewModel<S : UiViewState, V : UiViewEvent, C : UiControllerEvent> protected constructor(
    initialState: S
) : UiStateViewModel<S>(initialState), SaveableState {

    @Suppress("UNUSED_PARAMETER")
    @Deprecated(
        "\"debug\" parameter will be removed soon. Instead of a debug check to determine whether to run extra debug code, the debug check and code are removed via ProGuard rules. Be sure to assemble your release builds using ProGuard minification.",
        replaceWith = ReplaceWith("UiViewModel<S, V, C>(initialState)")
    )
    protected constructor(initialState: S, debug: Boolean) : this(initialState)

    private val onRestoreEventDelegate = lazy(NONE) { mutableSetOf<(UiBundleReader) -> Unit>() }
    private val onRestoreEvents by onRestoreEventDelegate

    private val onClearEventDelegate = lazy(NONE) { mutableSetOf<() -> Unit>() }
    private val onClearEvents by onClearEventDelegate

    private val onSaveEventDelegate = lazy(NONE) { mutableSetOf<(UiBundleWriter, S) -> Unit>() }
    private val onSaveEvents by onSaveEventDelegate

    private val controllerEventBus = EventBus.create<C>(emitOnlyWhenActive = true)

    // Need PublishedApi so createComponent can be inline
    @UiThread
    @CheckResult
    @PublishedApi
    internal fun bindToComponent(
        savedInstanceState: UiBundleReader,
        views: Array<out UiView<S, out V>>,
        onControllerEvent: (event: C) -> Unit
    ): Job {

        // Guarantee views are initialized
        // Run this outside of the view model scope to guarantee that it executes immediately
        views.forEach { it.init(savedInstanceState) }

        return viewModelScope.launch(context = Dispatchers.Main) {

            // Bind ViewModel
            bindControllerEvents(onControllerEvent)
            bindViewEvents(views.asIterable())

            // Initialize before first render
            // Generally, since you will add your doOnBind hooks in the ViewModel init {} block,
            // they will only run once - which is when the object is created.
            //
            // If you wanna do some strange kind of stuff though, you do you.
            initialize(savedInstanceState)

            // Inflate the views
            views.forEach { it.inflate(savedInstanceState) }

            // Bind state
            bindState(views)
        }
    }

    @UiThread
    private fun initialize(savedInstanceState: UiBundleReader) {
        // Only run the init hooks if they exist, otherwise we don't need to init the memory
        if (onRestoreEventDelegate.isInitialized()) {

            // Call init hooks in random order
            onRestoreEvents.forEach { it(savedInstanceState) }
        }
    }

    /**
     * Used for saving state in persistent lifecycle
     *
     * NOTE: While not deprecated, do your best to use StateSaver.saveState to bundle state
     * saving of entire components in a safe way
     *
     * NOTE: Not thread safe, Main thread only for now
     */
    @UiThread
    override fun saveState(outState: UiBundleWriter) {
        Enforcer.assertOnMainThread()

        // Only run the save state hooks if they exist, otherwise we don't need to init the memory
        if (onSaveEventDelegate.isInitialized()) {

            // Call save state hooks in random order
            val s = state
            onSaveEvents.forEach { it(outState, s) }
        }
    }

    /**
     * Called when the UiViewModel is being cleared for good.
     */
    @UiThread
    final override fun onCleared() {
        super.onCleared()
        Enforcer.assertOnMainThread()

        if (onClearEventDelegate.isInitialized()) {

            // Call teardown hooks in random order
            onClearEvents.forEach { it() }

            // Clear the teardown hooks list to free up memory
            onClearEvents.clear()
        }

        // If there are any bind event hooks hanging around, clear them out too
        if (onRestoreEventDelegate.isInitialized()) {
            onRestoreEvents.clear()
        }

        // If there are save state hooks around, clear them out
        if (onSaveEventDelegate.isInitialized()) {
            onSaveEvents.clear()
        }
    }

    /**
     * Fire a controller event
     */
    protected fun publish(event: C) {
        viewModelScope.launch(context = Dispatchers.IO) {
            controllerEventBus.send(event)
        }
    }

    private fun CoroutineScope.bindViewEvents(views: Iterable<UiView<S, out V>>) {
        launch(context = Dispatchers.IO) {
            views.forEach { view ->
                view.onViewEvent { handleViewEvent(it) }
                if (view is BaseUiView<S, out V, *>) {
                    val nestedViews = view.nestedViews()
                    if (nestedViews.isNotEmpty()) {
                        bindViewEvents(nestedViews)
                    }
                }
            }
        }
    }

    private inline fun CoroutineScope.bindControllerEvents(crossinline onControllerEvent: (event: C) -> Unit) {
        launch(context = Dispatchers.IO) {
            controllerEventBus.onEvent {
                // Controller events must fire onto the main thread
                withContext(context = Dispatchers.Main) {
                    onControllerEvent(it)
                }
            }
        }
    }

    /**
     * Use this to run an event after UiViewModel initialization has successfully finished.
     *
     * This is generally used in something like the constructor
     *
     * init {
     *     doOnInit { savedInstanceState ->
     *         ...
     *     }
     * }
     *
     * NOTE: Not thread safe. Main thread only for the time being
     */
    @UiThread
    @Deprecated("Use doOnRestoreState", ReplaceWith("doOnRestoreState(onInit)"))
    protected fun doOnInit(onInit: (savedInstanceState: UiBundleReader) -> Unit) {
        doOnRestoreState(onInit)
    }

    /**
     * Use this to run an event after UiViewModel binding has successfully finished.
     *
     * This is generally used in something like the constructor
     *
     * You will want to use this when you are running code which uses publish()
     * as this will guarantee that your Controller is bound at the time of calling,
     * or if you are running code each time this ViewModel is bound to a View.
     *
     * init {
     *     doOnBind { savedInstanceState ->
     *         ...
     *     }
     * }
     *
     * NOTE: Not thread safe. Main thread only for the time being
     */
    @UiThread
    @Deprecated("Use doOnRestoreState instead", ReplaceWith("doOnRestoreState(onBind)"))
    protected fun doOnBind(onBind: (savedInstanceState: UiBundleReader) -> Unit) {
        doOnRestoreState(onBind)
    }

    /**
     * Use this to run an event when the savedInstanceState is being restored
     *
     * This is generally used in something like the constructor
     *
     * init {
     *     doOnRestore { savedInstanceState ->
     *         ...
     *     }
     * }
     *
     * NOTE: Not thread safe. Main thread only for the time being
     */
    @UiThread
    protected fun doOnRestoreState(onRestore: (savedInstanceState: UiBundleReader) -> Unit) {
        Enforcer.assertOnMainThread()
        onRestoreEvents.add(onRestore)
    }

    /**
     * Use this to run an event when lifecycle is saving state
     *
     * This is generally used in something like the constructor
     *
     * init {
     *     doOnSaveState { outState, state ->
     *          outState.putInt(...)
     *          outState.putString(...)
     *     }
     * }
     *
     * NOTE: Not thread safe. Main thread only for the time being
     */
    @UiThread
    protected fun doOnSaveState(onSaveState: (outState: UiBundleWriter, state: S) -> Unit) {
        Enforcer.assertOnMainThread()
        onSaveEvents.add(onSaveState)
    }

    /**
     * Use this to run an event after UiViewModel onCleared has successfully finished.
     *
     * This is generally used in something like the constructor
     *
     * init {
     *     doOnTeardown {
     *         ...
     *     }
     * }
     *
     * NOTE: Not thread safe. Main thread only for the time being
     */
    @UiThread
    @Deprecated("Use doOnCleared", ReplaceWith("doOnCleared(onTeardown)"))
    protected fun doOnTeardown(onTeardown: () -> Unit) {
        doOnCleared(onTeardown)
    }

    /**
     * Use this to run an event after UiViewModel onCleared has successfully finished.
     *
     * This is generally used in something like the constructor
     *
     * init {
     *     doOnCleared {
     *         ...
     *     }
     * }
     *
     * NOTE: Not thread safe. Main thread only for the time being
     */
    @UiThread
    protected fun doOnCleared(onTeardown: () -> Unit) {
        Enforcer.assertOnMainThread()
        onClearEvents.add(onTeardown)
    }

    /**
     * Handle a UiViewEvent
     */
    protected abstract fun handleViewEvent(event: V)
}
