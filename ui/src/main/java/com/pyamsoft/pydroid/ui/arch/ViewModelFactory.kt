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
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.Factory
import androidx.lifecycle.ViewModelStore
import com.pyamsoft.pydroid.arch.UiViewModel
import com.pyamsoft.pydroid.ui.arch.FragmentFactoryProvider.FromActivity
import com.pyamsoft.pydroid.ui.arch.FragmentFactoryProvider.FromFragment
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty
import timber.log.Timber

/**
 * Allow nullable for easier caller API
 */
@CheckResult
@Deprecated(
    message = "Use viewModelFactory",
    replaceWith = ReplaceWith(
        expression = "viewModelFactory<T>(store, factoryProvider)",
        imports = ["com.pyamsoft.pydroid.ui.arch.viewModelFactory"]
    )
)
inline fun <reified T : UiViewModel<*, *, *>> factory(
    store: ViewModelStore,
    crossinline factoryProvider: () -> Factory?
): ViewModelFactory<T> {
    return viewModelFactory(store, factoryProvider)
}

/**
 * Allow nullable for easier caller API
 */
@CheckResult
inline fun <reified T : UiViewModel<*, *, *>> viewModelFactory(
    store: ViewModelStore,
    crossinline factoryProvider: () -> Factory?
): ViewModelFactory<T> {
    return ViewModelFactory(store, T::class.java) { requireNotNull(factoryProvider()) }
}

/**
 * Allow nullable for easier caller API
 */
@CheckResult
@JvmOverloads
@Deprecated(
    message = "Use viewModelFactory",
    replaceWith = ReplaceWith(
        expression = "viewModelFactory<T>(activity, factoryProvider)",
        imports = ["com.pyamsoft.pydroid.ui.arch.viewModelFactory"]
    )
)
inline fun <reified T : UiViewModel<*, *, *>> Fragment.factory(
    activity: Boolean = false,
    crossinline factoryProvider: () -> Factory?
): ViewModelFactory<T> {
    return viewModelFactory(activity, factoryProvider)
}

/**
 * Allow nullable for easier caller API
 */
@CheckResult
@JvmOverloads
inline fun <reified T : UiViewModel<*, *, *>> Fragment.viewModelFactory(
    activity: Boolean = false,
    crossinline factoryProvider: () -> Factory?
): ViewModelFactory<T> {
    val factory = if (activity) FromActivity(this) else FromFragment(this)
    return ViewModelFactory(factory, T::class.java) { requireNotNull(factoryProvider()) }
}

/**
 * Allow nullable for easier caller API
 */
@CheckResult
@Deprecated(
    message = "Use viewModelFactory",
    replaceWith = ReplaceWith(
        expression = "viewModelFactory<T>(factoryProvider)",
        imports = ["com.pyamsoft.pydroid.ui.arch.viewModelFactory"]
    )
)
inline fun <reified T : UiViewModel<*, *, *>> FragmentActivity.factory(crossinline factoryProvider: () -> Factory?): ViewModelFactory<T> {
    return viewModelFactory(factoryProvider)
}

/**
 * Allow nullable for easier caller API
 */
@CheckResult
inline fun <reified T : UiViewModel<*, *, *>> FragmentActivity.viewModelFactory(
    crossinline factoryProvider: () -> Factory?
): ViewModelFactory<T> {
    return ViewModelFactory(this, T::class.java) { requireNotNull(factoryProvider()) }
}

class ViewModelFactory<T : UiViewModel<*, *, *>> private constructor(
    type: Class<T>,
    store: ViewModelStore?,
    fragment: FragmentFactoryProvider?,
    activity: FragmentActivity?,
    factoryProvider: () -> Factory
) : ReadOnlyProperty<Any, T> {

    constructor(
        store: ViewModelStore,
        type: Class<T>,
        factoryProvider: () -> Factory
    ) : this(type, store, null, null, factoryProvider)

    constructor(
        fragment: FragmentFactoryProvider,
        type: Class<T>,
        factoryProvider: () -> Factory
    ) : this(type, null, fragment, null, factoryProvider)

    constructor(
        activity: FragmentActivity,
        type: Class<T>,
        factoryProvider: () -> Factory
    ) : this(type, null, null, activity, factoryProvider)

    private val lock = Any()

    @Volatile
    private var modelResolver: (() -> T)? = null

    @Volatile
    private var value: T? = null

    init {
        modelResolver = resolver@{
            return@resolver when {
                store != null -> {
                    Timber.d("Store init() ViewModel with type: $type")
                    ViewModelProvider(store, factoryProvider())
                        .get(type)
                }
                fragment != null -> {
                    return@resolver when (fragment) {
                        is FromFragment -> {
                            Timber.d("Fragment init() ViewModel with type: $type")
                            ViewModelProvider(fragment.fragment, factoryProvider())
                                .get(type)
                        }
                        is FromActivity -> {
                            Timber.d("FragmentActivity init() ViewModel with type: $type")
                            ViewModelProvider(
                                fragment.fragment.requireActivity(),
                                factoryProvider()
                            )
                                .get(type)
                        }
                    }
                }
                activity != null -> {
                    Timber.d("Activity init() ViewModel with type: $type")
                    ViewModelProvider(activity, factoryProvider())
                        .get(type)
                }
                else -> throw ResolverException("Unable to create model resolver - ViewModelStore, Activity, and Fragment are NULL")
            }
        }
    }

    @CheckResult
    private fun resolveValue(): T {
        val resolver = modelResolver
            ?: throw ResolverException("Cannot resolve ViewModel - resolver is NULL")

        modelResolver = null
        val vm = resolver()
        Timber.d("Resolved ViewModel $vm")
        return vm
    }

    @CheckResult
    fun get(): T {
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

    override fun getValue(
        thisRef: Any,
        property: KProperty<*>
    ): T {
        return get()
    }
}

class ResolverException internal constructor(
    message: String
) : IllegalStateException(message)

sealed class FragmentFactoryProvider {

    data class FromFragment(internal val fragment: Fragment) : FragmentFactoryProvider()

    data class FromActivity(internal val fragment: Fragment) : FragmentFactoryProvider()
}
