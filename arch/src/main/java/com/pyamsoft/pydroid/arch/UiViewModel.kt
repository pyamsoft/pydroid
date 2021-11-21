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

import androidx.annotation.UiThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.pyamsoft.pydroid.bus.EventBus
import com.pyamsoft.pydroid.core.Enforcer
import kotlin.LazyThreadSafetyMode.NONE
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * A default implementation of a UiStateViewModel which knows how to set up along with UiViews and a
 * UiController to become a full UiComponent
 */
@Deprecated(
    "PYDroid has migrated to ViewModeler and handling Activity configChanges as recommended by Compose")
public abstract class UiViewModel<S : UiViewState, C : UiControllerEvent>
protected constructor(delegate: UiStateModel<S>) : UiStateViewModel<S>(delegate = delegate) {

  /** Construct with default delegate using initialState */
  protected constructor(
      initialState: S
  ) : this(
      delegate = UiStateModel(initialState = initialState),
  )

  private val onClearEventDelegate = lazy(NONE) { mutableSetOf<() -> Unit>() }
  private val onClearEvents by onClearEventDelegate

  private val controllerEventBus = EventBus.create<C>()

  /**
   * Bind one or more UiViews to be driven by this UiViewModel
   *
   * This is automatically scoped to the life of the [scope]
   */
  @UiThread
  @Deprecated("Migrate to Jetpack Compose")
  public fun <V : UiViewEvent> bindViews(
      scope: CoroutineScope,
      savedInstanceState: UiSavedStateReader,
      vararg views: UiView<S, out V>,
      onEvent: (event: V) -> Unit
  ) {
    // Guarantee views are initialized
    // Run this outside of the view model scope to guarantee that it executes immediately
    views.forEach { it.init(savedInstanceState) }

    scope.launch(context = Dispatchers.Default) {
      // Bind ViewModel
      bindViewEvents(views.asIterable()) { onEvent(it) }

      // Inflate the views on main thread
      withContext(context = Dispatchers.Main) { views.forEach { it.inflate(savedInstanceState) } }

      // Bind state
      internalBindState(views)
    }
  }

  /**
   * Bind this UiViewModel to be driven by a UiController
   *
   * This is automatically scoped to the life of the [owner]
   */
  @UiThread
  public fun bindController(owner: LifecycleOwner, controller: UiController<C>) {
    bindController(owner.lifecycleScope, controller)
  }

  /**
   * Bind this UiViewModel to be driven by a UiController
   *
   * This is automatically scoped to the life of the [scope]
   */
  @UiThread
  public fun bindController(scope: CoroutineScope, controller: UiController<C>) {
    scope.launch(context = Dispatchers.Default) {
      // Bind Controller
      bindControllerEvents { controller.onControllerEvent(it) }
    }
  }

  /** Called when the UiViewModel is being cleared for good. */
  @UiThread
  final override fun onCleared() {
    Enforcer.assertOnMainThread()

    if (onClearEventDelegate.isInitialized()) {

      // Call teardown hooks in random order
      onClearEvents.forEach { it() }

      // Clear the teardown hooks list to free up memory
      onClearEvents.clear()
    }

    super.onCleared()
  }

  /** Fire a controller event */
  protected fun publish(event: C) {
    viewModelScope.launch(context = Dispatchers.IO) { controllerEventBus.send(event) }
  }

  private fun <V : UiViewEvent> CoroutineScope.bindViewEvents(
      views: Iterable<UiView<S, out V>>,
      onEvent: suspend (event: V) -> Unit
  ) {
    launch(context = Dispatchers.IO) {
      views.forEach { view ->
        view.onViewEvent(onEvent)
        if (view is BaseUiView<S, out V, *>) {
          val nestedViews = view.nestedViews()
          if (nestedViews.isNotEmpty()) {
            bindViewEvents(nestedViews, onEvent)
          }
        }
      }
    }
  }

  private inline fun CoroutineScope.bindControllerEvents(
      crossinline onControllerEvent: suspend (event: C) -> Unit
  ) {
    launch(context = Dispatchers.IO) {
      controllerEventBus.onEvent {
        // Controller events must fire onto the main thread
        withContext(context = Dispatchers.Main) { onControllerEvent(it) }
      }
    }
  }

  /**
   * Use this to run an event after UiViewModel onCleared has successfully finished.
   *
   * This is generally used in something like the constructor
   *
   * init {
   * ```
   *     doOnCleared {
   *         ...
   *     }
   * ```
   * }
   *
   * NOTE: Not thread safe. Main thread only for the time being
   */
  @UiThread
  protected fun doOnCleared(onTeardown: () -> Unit) {
    Enforcer.assertOnMainThread()
    onClearEvents.add(onTeardown)
  }
}
