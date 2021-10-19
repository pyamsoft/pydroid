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
import com.pyamsoft.pydroid.bus.EventBus
import com.pyamsoft.pydroid.core.Enforcer
import kotlin.LazyThreadSafetyMode.NONE
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * A basic interface representing a UiView
 *
 * The UiView can render a UiViewState object, and can also publish View level events to a
 * Presentation layer.
 *
 * NOTE: Will be removed in the future in favor of Jetpack Compose
 */
@Deprecated("Migrate to Jetpack Compose")
public abstract class UiView<S : UiViewState, V : UiViewEvent> protected constructor() :
    Renderable<S> {

  private val viewEventBus = EventBus.create<V>(emitOnlyWhenActive = true)

  private val onInflateEventDelegate = lazy(NONE) { mutableListOf<(UiSavedStateReader) -> Unit>() }
  private val onInflateEvents by onInflateEventDelegate

  private val onTeardownEventDelegate = lazy(NONE) { mutableListOf<() -> Unit>() }
  private val onTeardownEvents by onTeardownEventDelegate

  private val onSaveEventDelegate = lazy(NONE) { mutableSetOf<(UiSavedStateWriter) -> Unit>() }
  private val onSaveEvents by onSaveEventDelegate

  /**
   * CoroutineScope for the View level
   *
   * Really should only be used to power the viewEventBus
   */
  protected val viewScope: CoroutineScope = MainScope()

  /**
   * This is really only used as a hack, so we can inflate the actual Layout before running init
   * hooks.
   *
   * This way a UiViewModel can bind event handlers and receive events, and the view can publish()
   * inside of doOnInflate hooks.
   *
   * NOTE: Not thread safe. Main thread only for the time being
   */
  @UiThread
  public fun init(savedInstanceState: UiSavedStateReader) {
    // We better be UI
    Enforcer.assertOnMainThread()

    onInit(savedInstanceState)
  }

  /**
   * Inflate this view
   *
   * NOTE: Not thread safe. Main thread only for the time being
   */
  @UiThread
  public fun inflate(savedInstanceState: UiSavedStateReader) {
    // We better be UI
    Enforcer.assertOnMainThread()

    // Only run the inflation hooks if they exist, otherwise we don't need to init the memory
    if (onInflateEventDelegate.isInitialized()) {

      // Call inflate hooks in FIFO order
      onInflateEvents.forEach { it(savedInstanceState) }

      // Clear the inflation hooks list to free up memory
      onInflateEvents.clear()
    }
  }

  /**
   * Destroy this view
   *
   * NOTE: Not thread safe. Main thread only for the time being
   */
  @UiThread
  public fun teardown() {
    // We better be UI
    Enforcer.assertOnMainThread()

    // If there are any inflate event hooks hanging around, clear them out too
    if (onInflateEventDelegate.isInitialized()) {
      onInflateEvents.clear()
    }

    // If there are any save state event hooks hanging around, clear them out too
    if (onSaveEventDelegate.isInitialized()) {
      onSaveEvents.clear()
    }

    // Only run teardown hooks if they exist, otherwise don't init memory
    if (onTeardownEventDelegate.isInitialized()) {

      // Reverse the list order so that we teardown in LIFO order
      onTeardownEvents.reversed().forEach { it() }

      // Clear the teardown hooks list to free up memory
      onTeardownEvents.clear()
    }

    // Cancel the view scope
    viewScope.cancel()

    // Final teardown
    onFinalTeardown()
  }

  /**
   * Save state
   *
   * NOTE: While not deprecated, do your best to use StateSaver.saveState to bundle state saving of
   * entire components in a safe way
   *
   * NOTE: Not thread safe. Main thread only for the time being
   */
  @UiThread
  public fun saveState(outState: UiSavedStateWriter) {
    // We better be UI
    Enforcer.assertOnMainThread()

    // Only run save state hooks if they exist, otherwise don't init memory
    if (onSaveEventDelegate.isInitialized()) {

      // Call save hooks in any arbitrary order
      onSaveEvents.forEach { it(outState) }

      // DO NOT clear the onSaveEvents hook list here because onSaveState can happen multiple
      // times before the UiView calls teardown()
    }
  }

  // Need PublishedApi so bindViews can be inline
  @PublishedApi
  internal fun onViewEvent(func: suspend (event: V) -> Unit) {
    viewScope.launch(context = Dispatchers.IO) {
      viewEventBus.onEvent { event -> withContext(context = Dispatchers.Main) { func(event) } }
    }
  }

  /** Publish View level events */
  protected fun publish(event: V) {
    viewScope.launch(context = Dispatchers.IO) { viewEventBus.send(event) }
  }

  /**
   * Use this to run an event after UiView teardown has successfully finished. Events are guaranteed
   * to be called in LIFO (reverse) order, one after the other.
   *
   * This is generally used in something like the constructor
   *
   * init {
   * ```
   *     doOnTeardown {
   *         ...
   *     }
   * ```
   * }
   *
   * NOTE: Not thread safe. Main thread only for the time being
   */
  @UiThread
  protected fun doOnTeardown(onTeardown: () -> Unit) {
    // We better be UI
    Enforcer.assertOnMainThread()

    onTeardownEvents.add(onTeardown)
  }

  /**
   * Use this to run an event after UiView inflation has successfully finished. Events are
   * guaranteed to be called in FIFO order, one after the other.
   *
   * This is generally used in something like the constructor
   *
   * init {
   * ```
   *     doOnInflate { savedInstanceState ->
   *         ...
   *     }
   * ```
   * }
   *
   * NOTE: Not thread safe. Main thread only for the time being
   */
  @UiThread
  protected fun doOnInflate(onInflate: (savedInstanceState: UiSavedStateReader) -> Unit) {
    // We better be UI
    Enforcer.assertOnMainThread()

    onInflateEvents.add(onInflate)
  }

  /**
   * Use this to run an event during a UiView lifecycle saveState event Events are not guaranteed to
   * run in any consistent order
   *
   * This is generally used in something like the constructor
   *
   * init {
   * ```
   *     doOnSaveState { outState ->
   *         ...
   *     }
   * ```
   * }
   *
   * NOTE: Not thread safe. Main thread only for the time being
   */
  @UiThread
  protected fun doOnSaveState(onSaveState: (outState: UiSavedStateWriter) -> Unit) {
    // We better be UI
    Enforcer.assertOnMainThread()

    onSaveEvents.add(onSaveState)
  }

  /** This runs onInit and before any onInflate hooks have happened */
  @UiThread
  protected open fun onInit(savedInstanceState: UiSavedStateReader) {
    // Intentionally blank
  }

  /** This runs after all onTeardown hooks have ran */
  @UiThread
  protected open fun onFinalTeardown() {
    // Intentionally blank
  }
}
