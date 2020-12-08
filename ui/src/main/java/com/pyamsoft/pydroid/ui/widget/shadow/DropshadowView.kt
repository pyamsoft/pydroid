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

package com.pyamsoft.pydroid.ui.widget.shadow

import android.view.ViewGroup
import androidx.annotation.CheckResult
import com.pyamsoft.pydroid.arch.BaseUiView
import com.pyamsoft.pydroid.arch.UiViewEvent
import com.pyamsoft.pydroid.arch.UiViewState
import com.pyamsoft.pydroid.arch.UnitViewEvent
import com.pyamsoft.pydroid.arch.UnitViewState
import com.pyamsoft.pydroid.ui.databinding.DropshadowBinding

class DropshadowView<S : UiViewState, E : UiViewEvent> private constructor(
    parent: ViewGroup
) : BaseUiView<S, E, DropshadowBinding>(parent) {

    override val viewBinding = DropshadowBinding::inflate

    override val layoutRoot by boundView { dropshadowView }

    companion object {

        @JvmStatic
        @CheckResult
        fun create(parent: ViewGroup): DropshadowView<UnitViewState, UnitViewEvent> {
            return createTyped(parent)
        }

        @JvmStatic
        @CheckResult
        fun <S : UiViewState, E : UiViewEvent> createTyped(parent: ViewGroup): DropshadowView<S, E> {
            return DropshadowView(parent)
        }
    }
}
