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

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CheckResult
import com.pyamsoft.pydroid.arch.BaseUiView
import com.pyamsoft.pydroid.arch.UiViewEvent
import com.pyamsoft.pydroid.arch.UiViewState
import com.pyamsoft.pydroid.arch.UnitViewEvent
import com.pyamsoft.pydroid.arch.UnitViewState
import com.pyamsoft.pydroid.ui.databinding.TopshadowBinding

/** Topshadow UiView */
@Deprecated("Migrate to Jetpack Compose")
public class TopshadowView<S : UiViewState, E : UiViewEvent>
private constructor(parent: ViewGroup) : BaseUiView<S, E, TopshadowBinding>(parent) {

  /** Binding inflater */
  override val viewBinding: (LayoutInflater, ViewGroup) -> TopshadowBinding =
      TopshadowBinding::inflate

  /** Root layout */
  override val layoutRoot: View by boundView { topshadowView }

  public companion object {

    /** Create a typed dropshadow view */
    @JvmStatic
    @CheckResult
    public fun create(parent: ViewGroup): BaseUiView<UnitViewState, UnitViewEvent, *> {
      return createTyped(parent)
    }

    /** Create a typed dropshadow view */
    @JvmStatic
    @CheckResult
    public fun <S : UiViewState, E : UiViewEvent> createTyped(
        parent: ViewGroup
    ): BaseUiView<S, E, *> {
      return TopshadowView(parent)
    }
  }
}
