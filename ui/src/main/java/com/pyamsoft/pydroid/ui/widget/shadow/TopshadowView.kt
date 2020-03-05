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

package com.pyamsoft.pydroid.ui.widget.shadow

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.CheckResult
import com.pyamsoft.pydroid.arch.BindingUiView
import com.pyamsoft.pydroid.arch.UiViewEvent
import com.pyamsoft.pydroid.arch.UiViewState
import com.pyamsoft.pydroid.arch.UnitViewEvent
import com.pyamsoft.pydroid.arch.UnitViewState
import com.pyamsoft.pydroid.ui.databinding.TopshadowBinding

class TopshadowView<S : UiViewState, E : UiViewEvent> private constructor(
    parent: ViewGroup
) : BindingUiView<S, E, TopshadowBinding>(parent) {

    override val layoutRoot by boundView { topshadowView }

    override fun provideBindingInflater(): (LayoutInflater, ViewGroup) -> TopshadowBinding {
        return TopshadowBinding::inflate
    }

    override fun onRender(state: S) {
    }

    companion object {

        @JvmStatic
        @CheckResult
        fun create(parent: ViewGroup): TopshadowView<UnitViewState, UnitViewEvent> {
            return createTyped(parent)
        }

        @JvmStatic
        @CheckResult
        fun <S : UiViewState, E : UiViewEvent> createTyped(parent: ViewGroup): TopshadowView<S, E> {
            return TopshadowView(parent)
        }
    }
}
