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

package com.pyamsoft.pydroid.ui.arch

import androidx.annotation.CheckResult
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import com.pyamsoft.pydroid.arch.UiStateViewModel
import com.pyamsoft.pydroid.ui.arch.FragmentFactoryProvider.FromActivity
import com.pyamsoft.pydroid.ui.arch.FragmentFactoryProvider.FromFragment
import timber.log.Timber
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * Allow nullable for easier caller API
 */
@CheckResult
@Deprecated(
    "Use fromViewModelFactory",
    replaceWith = ReplaceWith("fromViewModelFactory<T>(store, factoryProvider)")
)
inline fun <reified T : UiStateViewModel<*>> viewModelFactory(
    store: ViewModelStore,
    crossinline factoryProvider: () -> ViewModelProvider.Factory?
): ViewModelFactory<T> {
    return fromViewModelFactory(store, factoryProvider)
}

/**
 * Allow nullable for easier caller API
 */
@CheckResult
@JvmOverloads
@Deprecated(
    "Use fromViewModelFactory",
    replaceWith = ReplaceWith("fromViewModelFactory<T>(activity, factoryProvider)")
)
inline fun <reified T : UiStateViewModel<*>> Fragment.viewModelFactory(
    activity: Boolean = false,
    crossinline factoryProvider: () -> ViewModelProvider.Factory?
): ViewModelFactory<T> {
    return fromViewModelFactory(activity, factoryProvider)
}

/**
 * Allow nullable for easier caller API
 */
@CheckResult
@Deprecated(
    "Use fromViewModelFactory",
    replaceWith = ReplaceWith("fromViewModelFactory<T>(factoryProvider)")
)
inline fun <reified T : UiStateViewModel<*>> FragmentActivity.viewModelFactory(
    crossinline factoryProvider: () -> ViewModelProvider.Factory?
): ViewModelFactory<T> {
    return fromViewModelFactory(factoryProvider)
}

/**
 * Allow nullable for easier caller API
 */
@CheckResult
inline fun <reified T : UiStateViewModel<*>> fromViewModelFactory(
    store: ViewModelStore,
    crossinline factoryProvider: () -> ViewModelProvider.Factory?
): ViewModelFactory<T> {
    return ViewModelFactoryImpl(store, T::class.java) { requireNotNull(factoryProvider()) }
}

/**
 * Allow nullable for easier caller API
 */
@CheckResult
@JvmOverloads
inline fun <reified T : UiStateViewModel<*>> Fragment.fromViewModelFactory(
    activity: Boolean = false,
    crossinline factoryProvider: () -> ViewModelProvider.Factory?
): ViewModelFactory<T> {
    val factory = if (activity) FromActivity(this) else FromFragment(this)
    return ViewModelFactoryImpl(factory, T::class.java) { requireNotNull(factoryProvider()) }
}

/**
 * Allow nullable for easier caller API
 */
@CheckResult
inline fun <reified T : UiStateViewModel<*>> FragmentActivity.fromViewModelFactory(
    crossinline factoryProvider: () -> ViewModelProvider.Factory?
): ViewModelFactory<T> {
    return ViewModelFactoryImpl(this, T::class.java) { requireNotNull(factoryProvider()) }
}


/**
 * The ViewModelFactory interface
 */
interface ViewModelFactory<T : UiStateViewModel<*>> : ReadOnlyProperty<Any, T>

@PublishedApi
internal class ViewModelFactoryImpl<T : UiStateViewModel<*>> private constructor(
    type: Class<T>,
    store: ViewModelStore?,
    fragment: FragmentFactoryProvider?,
    activity: FragmentActivity?,
    factoryProvider: () -> ViewModelProvider.Factory
) : ViewModelFactory<T> {

    @PublishedApi
    internal constructor(
        store: ViewModelStore,
        type: Class<T>,
        factoryProvider: () -> ViewModelProvider.Factory
    ) : this(type, store, null, null, factoryProvider)

    @PublishedApi
    internal constructor(
        fragment: FragmentFactoryProvider,
        type: Class<T>,
        factoryProvider: () -> ViewModelProvider.Factory
    ) : this(type, null, fragment, null, factoryProvider)

    @PublishedApi
    internal constructor(
        activity: FragmentActivity,
        type: Class<T>,
        factoryProvider: () -> ViewModelProvider.Factory
    ) : this(type, null, null, activity, factoryProvider)

    private val lock = Any()

    @Volatile
    private var modelResolver: (() -> T)? = null

    @Volatile
    private var value: T? = null

    init {
        modelResolver = {
            when {
                store != null -> ViewModelProvider(store, factoryProvider())
                activity != null -> ViewModelProvider(activity, factoryProvider())
                fragment != null -> {
                    val f = fragment.fragment
                    when (fragment) {
                        is FromFragment -> ViewModelProvider(f, factoryProvider())
                        is FromActivity -> ViewModelProvider(f.requireActivity(), factoryProvider())
                    }
                }
                else -> throw IllegalArgumentException("Unable to create model resolver - ViewModelStore, Activity, and Fragment are NULL")
            }.get(type)
        }
    }

    @CheckResult
    private fun resolveValue(): T {
        val resolver = modelResolver
            ?: throw IllegalStateException("Cannot resolve ViewModel - resolver is NULL")

        modelResolver = null
        val vm = resolver()
        Timber.d("Resolved ViewModel $vm")
        return vm
    }

    override fun getValue(
        thisRef: Any,
        property: KProperty<*>
    ): T {
        val v = value
        if (v != null) {
            return v
        }

        if (value == null) {
            synchronized(lock) {
                if (value == null) {
                    value = resolveValue()
                }
            }
        }

        return requireNotNull(value)
    }
}

@PublishedApi
internal sealed class FragmentFactoryProvider(internal val fragment: Fragment) {

    @PublishedApi
    internal class FromFragment @PublishedApi internal constructor(
        fragment: Fragment
    ) : FragmentFactoryProvider(fragment)

    @PublishedApi
    internal class FromActivity @PublishedApi internal constructor(
        fragment: Fragment
    ) : FragmentFactoryProvider(fragment)
}
