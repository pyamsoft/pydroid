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

package com.pyamsoft.pydroid.ui.about.dialog

import android.content.ActivityNotFoundException
import androidx.constraintlayout.widget.ConstraintLayout
import com.pyamsoft.pydroid.arch.UiComponent

internal interface UrlUiComponent : UiComponent<UrlUiComponent.Callback> {

  fun layout(
    constraintLayout: ConstraintLayout,
    aboveId: Int
  )

  fun navigationFailed(error: ActivityNotFoundException)

  interface Callback {

    fun onNavigateToExternalUrl(url: String)

  }

}