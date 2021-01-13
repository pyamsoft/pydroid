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

@file:JvmName("UiViewModelFactories")

package com.pyamsoft.pydroid.arch

import android.os.Bundle
import androidx.annotation.CheckResult
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.savedstate.SavedStateRegistryOwner
import com.pyamsoft.pydroid.arch.internal.RealUiSavedState
import kotlin.reflect.KClass

/**
 * A ViewModelProvider.Factory which returns UiStateViewModel and UiViewModel instances.
 */
public abstract class UiViewModelFactory protected constructor() : ViewModelProvider.Factory {

    /**
     * Resolve the requested UiViewModel
     */
    final override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (UiStateViewModel::class.java.isAssignableFrom(modelClass)) {
            @Suppress("UNCHECKED_CAST")
            val viewModelClass = modelClass as Class<out UiStateViewModel<*>>

            @Suppress("UNCHECKED_CAST")
            return viewModel(viewModelClass.kotlin) as T
        } else {
            fail()
        }
    }

    /**
     * Factory fails to return a value
     */
    protected fun fail(): Nothing {
        throw IllegalArgumentException("Factory can only handle classes that extend UiViewModel")
    }

    /**
     * Resolve the requested UiViewModel
     */
    @CheckResult
    protected abstract fun <T : UiStateViewModel<*>> viewModel(modelClass: KClass<T>): UiStateViewModel<*>
}

/**
 * A ViewModelProvider.Factory which returns UiStateViewModel and UiViewModel instances.
 *
 * Integrated with androidx.savedstate
 */
public abstract class UiSavedStateViewModelFactory @JvmOverloads protected constructor(
    owner: SavedStateRegistryOwner,
    defaultArgs: Bundle? = null
) : AbstractSavedStateViewModelFactory(owner, defaultArgs) {

    /**
     * Resolve the requested UiViewModel
     */
    final override fun <T : ViewModel?> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        if (UiStateViewModel::class.java.isAssignableFrom(modelClass)) {
            @Suppress("UNCHECKED_CAST")
            val viewModelClass = modelClass as Class<out UiStateViewModel<*>>

            @Suppress("UNCHECKED_CAST")
            return viewModel(viewModelClass.kotlin, RealUiSavedState(handle)) as T
        } else {
            fail()
        }
    }

    /**
     * Factory fails to return a value
     */
    protected fun fail(): Nothing {
        throw IllegalArgumentException("Factory can only handle classes that extend UiViewModel")
    }

    /**
     * Resolve the requested UiViewModel
     */
    @CheckResult
    protected abstract fun <T : UiStateViewModel<*>> viewModel(
        modelClass: KClass<T>,
        savedState: UiSavedState,
    ): UiStateViewModel<*>
}

/**
 * Create a save state aware view model factory
 */
@CheckResult
@JvmOverloads
@JvmName("createSavedStateFactory")
public inline fun <reified T : ViewModel> SavedStateRegistryOwner.createFactory(
    provider: SavedStateViewModelProvider<T>?,
    defaultArgs: Bundle? = null
): ViewModelProvider.Factory {
    return object : UiSavedStateViewModelFactory(this, defaultArgs) {
        override fun <T : UiStateViewModel<*>> viewModel(
            modelClass: KClass<T>,
            savedState: UiSavedState
        ): UiStateViewModel<*> {
            @Suppress("UNCHECKED_CAST")
            return requireNotNull(provider).create(savedState) as? T ?: fail()
        }

    }
}

/**
 * Create a view model factory
 */
@CheckResult
public inline fun <reified T : ViewModel> createFactory(crossinline provider: () -> T?): ViewModelProvider.Factory {
    return object : UiViewModelFactory() {
        override fun <T : UiStateViewModel<*>> viewModel(modelClass: KClass<T>): UiStateViewModel<*> {
            @Suppress("UNCHECKED_CAST")
            return provider() as? T ?: fail()
        }
    }
}

/**
 * The interface around a factory with saved state
 */
public interface SavedStateViewModelProvider<T : ViewModel> {

    public fun create(savedState: UiSavedState): T
}
