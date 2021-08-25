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
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.savedstate.SavedStateRegistryOwner
import com.pyamsoft.pydroid.arch.internal.HandleUiSavedState

/** A ViewModelProvider.Factory which returns UiStateViewModel and UiViewModel instances. */
public abstract class ViewModelFactory protected constructor() : ViewModelProvider.Factory {

  /** Resolve the requested UiViewModel */
  final override fun <T : ViewModel> create(modelClass: Class<T>): T {
    if (ViewModel::class.java.isAssignableFrom(modelClass)) {
      @Suppress("UNCHECKED_CAST") return createViewModel(modelClass) as T
    } else {
      fail(modelClass)
    }
  }

  /** Factory fails to return a value */
  protected fun <T : ViewModel> fail(modelClass: Class<T>): Nothing {
    throw IllegalArgumentException(
        "Factory cannot handle ViewModel class: ${modelClass.simpleName}")
  }

  /** Resolve the requested UiViewModel */
  @CheckResult
  protected abstract fun <T : ViewModel> createViewModel(modelClass: Class<T>): ViewModel
}

/**
 * A ViewModelProvider.Factory which returns UiStateViewModel and UiViewModel instances.
 *
 * Integrated with androidx.savedstate
 */
public abstract class SavedStateViewModelFactory
@JvmOverloads
protected constructor(owner: SavedStateRegistryOwner, defaultArgs: Bundle? = null) :
    AbstractSavedStateViewModelFactory(owner, defaultArgs) {

  /** Resolve the requested UiViewModel */
  final override fun <T : ViewModel> create(
      key: String,
      modelClass: Class<T>,
      handle: SavedStateHandle
  ): T {
    if (UiStateViewModel::class.java.isAssignableFrom(modelClass)) {
      @Suppress("UNCHECKED_CAST")
      return createViewModel(modelClass, HandleUiSavedState(handle)) as T
    } else {
      fail(modelClass)
    }
  }

  /** Factory fails to return a value */
  protected fun <T : ViewModel> fail(modelClass: Class<T>): Nothing {
    throw IllegalArgumentException(
        "Factory cannot handle ViewModel class: ${modelClass.simpleName}")
  }

  /** Resolve the requested UiViewModel */
  @CheckResult
  protected abstract fun <T : ViewModel> createViewModel(
      modelClass: Class<T>,
      savedState: UiSavedState,
  ): ViewModel
}

/** Create a save state aware view model factory */
@CheckResult
@JvmOverloads
public inline fun <reified T : ViewModel> UiSavedStateViewModelProvider<T>.asFactory(
    owner: SavedStateRegistryOwner,
    defaultArgs: Bundle? = null
): ViewModelProvider.Factory {
  val self = this
  return object : SavedStateViewModelFactory(owner, defaultArgs) {
    override fun <T : ViewModel> createViewModel(
        modelClass: Class<T>,
        savedState: UiSavedState
    ): ViewModel {
      @Suppress("UNCHECKED_CAST") return self.create(savedState) as? T ?: fail(modelClass)
    }
  }
}

/** Create a view model factory */
@CheckResult
public inline fun <reified T : ViewModel> createViewModelFactory(
    crossinline provider: () -> T?
): ViewModelProvider.Factory {
  return object : ViewModelFactory() {
    override fun <T : ViewModel> createViewModel(modelClass: Class<T>): ViewModel {
      @Suppress("UNCHECKED_CAST") return provider() as? T ?: fail(modelClass)
    }
  }
}

/** The interface around a factory with saved state */
public interface UiSavedStateViewModelProvider<T : ViewModel> {

  /** Create a new view model */
  public fun create(savedState: UiSavedState): T
}
