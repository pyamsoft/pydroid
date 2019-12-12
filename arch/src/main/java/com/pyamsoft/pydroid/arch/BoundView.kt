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

package com.pyamsoft.pydroid.arch

import android.view.View
import android.view.ViewGroup
import androidx.annotation.CheckResult
import androidx.annotation.IdRes
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class BoundView<V : View> internal constructor(
    parent: ViewGroup,
    @IdRes private val id: Int
) : ReadOnlyProperty<Any, V> {

    private var parent: ViewGroup? = parent
    private var view: V? = null

    private fun die(): Nothing {
        throw IllegalStateException("Cannot call BoundView methods after it has been torn down")
    }

    @CheckResult
    private fun parent(): ViewGroup {
        return parent ?: die()
    }

    private fun assertValidState() {
        if (parent == null) {
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
            val bound = requireNotNull(parent().findViewById<V>(id))
            view = bound
            result = bound
        } else {
            result = v
        }

        return result
    }

    internal fun teardown() {
        assertValidState()

        parent = null
        view = null
    }
}
