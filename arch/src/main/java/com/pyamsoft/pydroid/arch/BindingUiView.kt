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
 *
 */

package com.pyamsoft.pydroid.arch

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CheckResult
import androidx.viewbinding.ViewBinding

// NOTE: This class will be removed and its functionality will be moved into BaseUiView in the next major version 21.X.X
abstract class BindingUiView<S : UiViewState, V : UiViewEvent, B : ViewBinding> protected constructor(
    parent: ViewGroup
) : BaseUiView<S, V>(parent) {

    private var _binding: B? = null

    @CheckResult
    protected fun binding(): B {
        return _binding ?: die()
    }

    @CheckResult
    protected abstract fun createBinding(inflater: LayoutInflater, root: ViewGroup): B

    private fun assertValidBinding() {
        if (_binding == null) {
            die()
        }
    }

    @CheckResult
    protected fun <V : View> boundView(func: B.() -> V): BoundView<V> {
        assertValidState()
        assertValidBinding()
        return createBoundView { func(binding()) }
    }

    final override fun inflateAndAddToParent(
        inflater: LayoutInflater,
        parent: ViewGroup,
        layout: Int
    ) {
        _binding = createBinding(inflater, parent)
    }
}
