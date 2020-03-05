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

    final override val layout: Int = 0

    final override val layoutRoot by createBoundView { provideBindingRoot(binding) }

    private var _binding: B? = null
    protected val binding: B
        get() = _binding ?: die()

    init {
        doOnTeardown {
            _binding = null
        }
    }

    final override fun inflateAndAddToParent(inflater: LayoutInflater, parent: ViewGroup) {
        _binding = provideBindingInflater().invoke(inflater, parent)
    }

    @CheckResult
    protected abstract fun provideBindingRoot(binding: B): View

    @CheckResult
    protected abstract fun provideBindingInflater(): (LayoutInflater, ViewGroup) -> B
}
