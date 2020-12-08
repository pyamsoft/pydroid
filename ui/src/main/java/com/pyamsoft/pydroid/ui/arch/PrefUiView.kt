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
import androidx.annotation.StringRes
import androidx.annotation.UiThread
import androidx.preference.Preference
import androidx.preference.PreferenceScreen
import com.pyamsoft.pydroid.arch.UiBundleReader
import com.pyamsoft.pydroid.arch.UiRender
import com.pyamsoft.pydroid.arch.UiView
import com.pyamsoft.pydroid.arch.UiViewEvent
import com.pyamsoft.pydroid.arch.UiViewState
import timber.log.Timber
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

abstract class PrefUiView<S : UiViewState, V : UiViewEvent> protected constructor(
    parent: PreferenceScreen
) : UiView<S, V>() {

    private var _parent: PreferenceScreen? = parent
    private var boundPrefs: MutableSet<BoundPref<*>>? = null

    init {
        doOnTeardown {
            assertValidState()

            boundPrefs?.forEach { it.teardown() }
            boundPrefs?.clear()
            boundPrefs = null
        }
    }

    final override fun onFinalTeardown() {
        Timber.d("Teardown complete, unbind")
        _parent = null
    }

    final override fun onInit(savedInstanceState: UiBundleReader) {
        // Intentionally blank
    }

    @CheckResult
    private fun parent(): PreferenceScreen {
        return requireNotNull(_parent)
    }

    private fun die(): Nothing {
        throw IllegalStateException("Cannot call UiView methods after it has been torn down")
    }

    private fun assertValidState() {
        if (_parent == null) {
            die()
        }
    }

    override fun render(state: UiRender<S>) {
        assertValidState()
        onRender(state)
    }

    @UiThread
    protected open fun onRender(state: UiRender<S>) {
        state.render { onRender(it) }
    }

    @UiThread
    @Deprecated("Use onRender(UiRender<S>)")
    protected open fun onRender(state: S) {

    }

    @CheckResult
    protected fun <V : Preference> boundPref(@StringRes id: Int): BoundPref<V> {
        return boundPref(parent().context.getString(id))
    }

    private fun trackBound(v: BoundPref<*>) {
        assertValidState()

        val bv: MutableSet<BoundPref<*>>? = boundPrefs
        val mutateMe: MutableSet<BoundPref<*>>
        if (bv == null) {
            val bound = LinkedHashSet<BoundPref<*>>()
            boundPrefs = bound
            mutateMe = bound
        } else {
            mutateMe = bv
        }

        mutateMe.add(v)
    }

    @CheckResult
    protected fun <V : Preference> boundPref(key: String): BoundPref<V> {
        return createBoundPref { requireNotNull(parent().findPreference<V>(key)) }
    }

    @CheckResult
    protected fun <V : Preference> createBoundPref(resolver: () -> V): BoundPref<V> {
        assertValidState()
        return BoundPref(resolver).also { trackBound(it) }
    }

    protected class BoundPref<V : Preference> internal constructor(
        resolver: () -> V
    ) : ReadOnlyProperty<Any, V> {

        private var resolver: (() -> V)? = resolver
        private var view: V? = null

        private fun die(): Nothing {
            throw IllegalStateException("Cannot call BoundPref methods after it has been torn down")
        }

        private fun assertValidState() {
            if (resolver == null) {
                die()
            }
        }

        override fun getValue(
            thisRef: Any,
            property: KProperty<*>
        ): V {
            assertValidState()

            val v: V? = view
            val result: V
            if (v == null) {
                @Suppress("UNCHECKED_CAST")
                val bound = requireNotNull(resolver).invoke()
                view = bound
                result = bound
            } else {
                result = v
            }

            return result
        }

        internal fun teardown() {
            assertValidState()

            resolver = null
            view = null
        }
    }
}
