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

package com.pyamsoft.pydroid.ui.arch

import androidx.annotation.CheckResult
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Lifecycle.Event.ON_DESTROY
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.Factory
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.ViewModelStore
import com.pyamsoft.pydroid.arch.UiViewModel
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * Allow nullable for easier caller API
 */
@CheckResult
inline fun <reified T : UiViewModel<*, *, *>> LifecycleOwner.factory(
    store: ViewModelStore,
    crossinline factoryProvider: () -> Factory?
): ViewModelFactory<T> {
    return lifecycle.factory(store, factoryProvider)
}

/**
 * Allow nullable for easier caller API
 */
@CheckResult
inline fun <reified T : UiViewModel<*, *, *>> Lifecycle.factory(
    store: ViewModelStore,
    crossinline factoryProvider: () -> Factory?
): ViewModelFactory<T> {
    return ViewModelFactory(this, store, T::class.java) { requireNotNull(factoryProvider()) }
}

/**
 * Allow nullable for easier caller API
 */
@CheckResult
inline fun <reified T : UiViewModel<*, *, *>> Fragment.factory(
    crossinline factoryProvider: () -> Factory?
): ViewModelFactory<T> {
    return ViewModelFactory(this, T::class.java) { requireNotNull(factoryProvider()) }
}

/**
 * Allow nullable for easier caller API
 */
@CheckResult
inline fun <reified T : UiViewModel<*, *, *>> FragmentActivity.factory(
    crossinline factoryProvider: () -> Factory?
): ViewModelFactory<T> {
    return ViewModelFactory(this, T::class.java) { requireNotNull(factoryProvider()) }
}

class ViewModelFactory<T : UiViewModel<*, *, *>> private constructor(
    private val lifecycleProvider: () -> Lifecycle,
    private val type: Class<T>,
    private var store: ViewModelStore?,
    private var fragment: Fragment?,
    private var activity: FragmentActivity?,
    private val factoryProvider: () -> Factory
) : ReadOnlyProperty<Any, T> {

    constructor(
        lifecycle: Lifecycle,
        store: ViewModelStore,
        type: Class<T>,
        factoryProvider: () -> Factory
    ) : this({ lifecycle }, type, store, null, null, factoryProvider)

    constructor(
        fragment: Fragment,
        type: Class<T>,
        factoryProvider: () -> Factory
    ) : this({ fragment.viewLifecycleOwner.lifecycle }, type, null, fragment, null, factoryProvider)

    constructor(
        activity: FragmentActivity,
        type: Class<T>,
        factoryProvider: () -> Factory
    ) : this({ activity.lifecycle }, type, null, null, activity, factoryProvider)

    private val lock = Any()
    @Volatile
    private var value: T? = null

    private fun clear() {
        store = null
        fragment = null
        activity = null
    }

    private fun attachToLifecycle(lifecycle: Lifecycle) {
        lifecycle.addObserver(object : LifecycleObserver {

            @Suppress("unused")
            @OnLifecycleEvent(ON_DESTROY)
            fun onDestroy() {
                lifecycle.removeObserver(this)
                clear()
                value = null
            }
        })
    }

    @CheckResult
    private fun resolveValue(): T {
        attachToLifecycle(lifecycleProvider())
        store?.let { s ->
            return ViewModelProvider(s, factoryProvider())
                .get(type)
        }
        fragment?.let { f ->
            return ViewModelProviders.of(f, factoryProvider())
                .get(type)
        }
        activity?.let { a ->
            return ViewModelProviders.of(a, factoryProvider())
                .get(type)
        }
        throw IllegalStateException("Both Fragment an Activity are null")
    }

    @CheckResult
    fun get(): T {
        val lifecycle = lifecycleProvider()
        check(lifecycle.currentState != Lifecycle.State.DESTROYED) {
            "Cannot access ViewModel after Lifecycle is DESTROYED"
        }

        val v = value
        if (v != null) {
            return v
        }

        if (value == null) {
            synchronized(lock) {
                if (value == null) {
                    value = resolveValue()
                    clear()
                }
            }
        }

        return requireNotNull(value)
    }

    override fun getValue(
        thisRef: Any,
        property: KProperty<*>
    ): T {
        return get()
    }
}
